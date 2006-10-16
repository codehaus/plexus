package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.core.ProcessEventManager;
import org.codehaus.plexus.spe.event.StartingProcessEvent;
import org.codehaus.plexus.spe.event.StartingStepEvent;
import org.codehaus.plexus.spe.event.StepCompletedEvent;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;
import org.codehaus.plexus.spe.store.ProcessInstanceStore;
import org.codehaus.plexus.util.ExceptionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultProcessExecutor
    extends AbstractLogEnabled
    implements ProcessExecutor, Initializable, Startable, Serviceable
{
    /**
     * @plexus.requirement
     */
    private ProcessInstanceStore store;

    /**
     * @plexus.requirement
     */
    private ProcessEventManager eventManager;

    /**
     * @plexus.requirement
     */
    private Map stepExecutors;

    private ServiceLocator serviceLocator;

    private ExecutorService executorService;

    private Map<StepExecutorRunner, ProcessRuntimeDescriptor> runningProcesses = new HashMap<StepExecutorRunner, ProcessRuntimeDescriptor>();

    // ----------------------------------------------------------------------
    // ProcessExecutor Implementation
    // ----------------------------------------------------------------------

    public void startProcess( ProcessDescriptor process, ProcessInstance instance )
        throws ProcessException
    {
        StartingProcessEvent event = new StartingProcessEvent();
        event.setProcessId( process.getId() );
        eventManager.sendEvent( event );

        // ----------------------------------------------------------------------
        // Look up all the required actions and put them in the runtime process.
        // Stores the first action and adds that to the executor queue.
        // ----------------------------------------------------------------------

        ProcessRuntimeDescriptor runtimeDescriptor = new ProcessRuntimeDescriptor( process, instance.getInstanceId() );

        runtimeDescriptor.setCurrentStep( 0 );

        // ----------------------------------------------------------------------
        // Schedule the first action
        // ----------------------------------------------------------------------

        scheduleStep( runtimeDescriptor );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        executorService = new ProcessThreadPoolExecutor();
    }

    public void start()
        throws StartingException
    {
    }

    public void stop()
        throws StoppingException
    {
        executorService.shutdownNow();
    }

    public void service( ServiceLocator serviceLocator )
    {
        this.serviceLocator = serviceLocator;
    }

    // ----------------------------------------------------------------------
    // Process lifecycle
    // ----------------------------------------------------------------------

    private void onStepStart( StepExecutorRunner stepExecutorRunner, ProcessRuntimeDescriptor runtimeDescriptor )
    {
        StartingStepEvent event = new StartingStepEvent();
        event.setProcessId( runtimeDescriptor.getProcessDescriptor().getId() );
        event.setProcessInstanceId( runtimeDescriptor.getInstanceId() );
        event.setStepNumber( runtimeDescriptor.getCurrentStep() );
        eventManager.sendEvent( event );

        try
        {
            // ----------------------------------------------------------------------
            // Create and record the new step in the process
            // ----------------------------------------------------------------------

            ProcessInstance processInstance = store.getInstance( runtimeDescriptor.getInstanceId(), true );
            StepInstance step = (StepInstance) processInstance.getSteps().get( runtimeDescriptor.getCurrentStep() );
            step.setStartTime( System.currentTimeMillis() );
            store.saveInstance( processInstance );

            processInstance = store.getInstance( runtimeDescriptor.getInstanceId(), true );
            stepExecutorRunner.setContext( processInstance.getContext() );
        }
        catch ( ProcessException e )
        {
            throw new RuntimeException( "Abort", e );
        }
    }

    private void onStepCompletion( StepExecutorRunner stepExecutorRunner,
                                   ProcessRuntimeDescriptor runtimeDescriptor )
        throws ProcessException
    {
        long timestamp = System.currentTimeMillis();

        Throwable throwable = stepExecutorRunner.getThrowable();

        // ----------------------------------------------------------------------
        // Load the process
        // ----------------------------------------------------------------------

        ProcessInstance processInstance = store.getInstance( runtimeDescriptor.getInstanceId(), true );

        // ----------------------------------------------------------------------
        // Update the step and context
        // ----------------------------------------------------------------------

        List steps = processInstance.getSteps();
        StepInstance step = (StepInstance) steps.get( steps.size() - 1 );
        step.setEndTime( timestamp );

        processInstance.setContext( stepExecutorRunner.getContext() );

        StepCompletedEvent event = new StepCompletedEvent();
        event.setProcessInstance( processInstance );
        event.setStepInstance( step );

        // ----------------------------------------------------------------------
        // Check if the step failed. If so stop the processing for now and let
        // the system handle the restart of the process at a later point in time.
        // ----------------------------------------------------------------------

        if ( throwable != null )
        {
            String stackTrace = ExceptionUtils.getFullStackTrace( throwable );
            if ( stackTrace.length() > 8000 )
            {
                stackTrace = stackTrace.substring( 0, 8000 );
            }
            processInstance.setErrorMessage( stackTrace );

            store.saveInstance( processInstance );

            event.setThrowable( throwable );
            eventManager.sendEvent( event );

            return;
        }

        // ----------------------------------------------------------------------
        // Check if this is the last step in the process
        // ----------------------------------------------------------------------

        if ( runtimeDescriptor.getProcessDescriptor().getSteps().size() == runtimeDescriptor.getCurrentStep() + 1 )
        {
            processInstance.setCompleted( true );
            processInstance.setEndTime( timestamp );

            store.saveInstance( processInstance );

            eventManager.sendEvent( event );

            // TODO: Send process completed event

            return;
        }

        // ----------------------------------------------------------------------
        // Schedule the next step
        // ----------------------------------------------------------------------

        runtimeDescriptor.setCurrentStep( runtimeDescriptor.getCurrentStep() + 1 );

        StepInstance nextStep = (StepInstance) processInstance.getSteps().get( runtimeDescriptor.getCurrentStep() );
        nextStep.setStartTime( timestamp );

        store.saveInstance( processInstance );

        eventManager.sendEvent( event );

        scheduleStep( runtimeDescriptor );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void scheduleStep( ProcessRuntimeDescriptor runtimeDescriptor )
        throws ProcessException
    {
        int currentStep = runtimeDescriptor.getCurrentStep();

        StepDescriptor stepDescriptor = (StepDescriptor) runtimeDescriptor.getProcessDescriptor().getSteps().get( currentStep );

        StepExecutor stepExecutor = (StepExecutor) stepExecutors.get( stepDescriptor.getExecutorId() );

        if ( stepExecutor == null )
        {
            throw new ProcessException( "No such executor '" + stepDescriptor.getExecutorId() + "'." );
        }

        StepExecutorRunner stepExecutorRunner = new StepExecutorRunner( stepExecutor, stepDescriptor );

        runningProcesses.put( stepExecutorRunner, runtimeDescriptor );

        executorService.execute( stepExecutorRunner );
    }

    /**
     * Holder object containing the runtime information of the process.
     */
    private static class ProcessRuntimeDescriptor
    {
        private ProcessDescriptor processDescriptor;

        private int instanceId;

        private int currentStep;

        public ProcessRuntimeDescriptor( ProcessDescriptor processDescriptor, int processId )
        {
            this.processDescriptor = processDescriptor;
            this.instanceId = processId;
        }

        public ProcessDescriptor getProcessDescriptor()
        {
            return processDescriptor;
        }

        public int getInstanceId()
        {
            return instanceId;
        }

        public int getCurrentStep()
        {
            return currentStep;
        }

        public void setCurrentStep( int currentStep )
        {
            this.currentStep = currentStep;
        }
    }

    private class ProcessThreadPoolExecutor
        extends ThreadPoolExecutor
    {
        public ProcessThreadPoolExecutor()
        {
            super( 1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000) );
        }

        protected void beforeExecute( Thread thread, Runnable runnable )
        {
            StepExecutorRunner stepExecutorRunner = (StepExecutorRunner) runnable;

            ProcessRuntimeDescriptor processRuntimeDescriptor = runningProcesses.get( stepExecutorRunner );

            onStepStart( stepExecutorRunner, processRuntimeDescriptor );
        }

        protected void afterExecute( Runnable runnable, Throwable throwable )
        {
            try
            {
                StepExecutorRunner stepExecutorRunner = (StepExecutorRunner) runnable;

                ProcessRuntimeDescriptor processRuntimeDescriptor = runningProcesses.get( stepExecutorRunner );

                onStepCompletion( stepExecutorRunner, processRuntimeDescriptor );
            }
            catch ( ProcessException e )
            {
                getLogger().error( "Error while saving process state.", e );
            }
        }
    }
}
