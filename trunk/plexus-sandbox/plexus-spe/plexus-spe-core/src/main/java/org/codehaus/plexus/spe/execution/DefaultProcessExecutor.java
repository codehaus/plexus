package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;
import org.codehaus.plexus.spe.store.ProcessInstanceStore;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

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

    private void onStart( StepExecutorRunner stepExecutorRunner, ProcessRuntimeDescriptor runtimeDescriptor )
    {
        try
        {
            // ----------------------------------------------------------------------
            // Create and record the new step in the process
            // ----------------------------------------------------------------------

            ProcessInstance processInstance = store.getInstance( runtimeDescriptor.getProcessId(), true );
            StepInstance step = (StepInstance) processInstance.getSteps().get( runtimeDescriptor.getCurrentStep() );
            step.setStartTime( System.currentTimeMillis() );
            store.saveInstance( processInstance );

            processInstance = store.getInstance( runtimeDescriptor.getProcessId(), true );
            stepExecutorRunner.setContext( processInstance.getContext() );
        }
        catch ( ProcessException e )
        {
            throw new RuntimeException( "Abort", e );
        }
    }

    private void onSuccess( StepExecutorRunner stepExecutorRunner,
                            ProcessRuntimeDescriptor runtimeDescriptor )
        throws ProcessException
    {
        long timestamp = System.currentTimeMillis();

        getLogger().info( "Process step #" + runtimeDescriptor.getCurrentStep() + " " +
            "in process " + runtimeDescriptor.getProcessDescriptor().getId() + ", instance #" + runtimeDescriptor.getProcessId() +
            " completed successfully." );

        // ----------------------------------------------------------------------
        // Load the process
        // ----------------------------------------------------------------------

        ProcessInstance processInstance = store.getInstance( runtimeDescriptor.getProcessId(), true );

        // ----------------------------------------------------------------------
        // Update the step and set the new context
        // ----------------------------------------------------------------------

        List steps = processInstance.getSteps();
        StepInstance step = (StepInstance) steps.get( steps.size() - 1 );
        step.setEndTime( timestamp );

        processInstance.setContext( stepExecutorRunner.getContext() );

        // ----------------------------------------------------------------------
        // Check if this is the last step in the process
        // ----------------------------------------------------------------------

        if ( runtimeDescriptor.getProcessDescriptor().getSteps().size() == runtimeDescriptor.getCurrentStep() + 1 )
        {
            getLogger().info( "Process completed." );

            processInstance.setEndTime( timestamp );

            store.saveInstance( processInstance );

            return;
        }

        // ----------------------------------------------------------------------
        // Schedule the next step
        // ----------------------------------------------------------------------

        runtimeDescriptor.setCurrentStep( runtimeDescriptor.getCurrentStep() + 1 );

        getLogger().info( "Next process step: " + runtimeDescriptor.getCurrentStep() );

        step = (StepInstance) processInstance.getSteps().get( runtimeDescriptor.getCurrentStep() );
        step.setStartTime( timestamp );

        store.saveInstance( processInstance );

        scheduleStep( runtimeDescriptor );
    }

    private void onFailure( ProcessRuntimeDescriptor processRuntimeDescriptor, Throwable throwable )
    {
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

        private int processId;

        private int currentStep;

        public ProcessRuntimeDescriptor( ProcessDescriptor processDescriptor, int processId )
        {
            this.processDescriptor = processDescriptor;
            this.processId = processId;
        }

        public ProcessDescriptor getProcessDescriptor()
        {
            return processDescriptor;
        }

        public int getProcessId()
        {
            return processId;
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

            onStart( stepExecutorRunner, processRuntimeDescriptor );
        }

        protected void afterExecute( Runnable runnable, Throwable throwable )
        {
            try
            {
                StepExecutorRunner stepExecutorRunner = (StepExecutorRunner) runnable;

                ProcessRuntimeDescriptor processRuntimeDescriptor = runningProcesses.get( stepExecutorRunner );

                if ( throwable != null )
                {
                    onFailure( processRuntimeDescriptor, throwable );
                }
                else
                {
                    onSuccess( stepExecutorRunner, processRuntimeDescriptor );
                }
            }
            catch ( ProcessException e )
            {
                getLogger().error( "Error while saving process state.", e );
            }
        }
    }
}
