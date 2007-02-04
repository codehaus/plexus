package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.model.StepDescriptor;

import java.io.Serializable;
import java.util.Map;

/**
 * Contains the thread-local state of the currently running state.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StepExecutorRunner
    implements Runnable
{
    private StepExecutor executor;

    private StepDescriptor stepDescriptor;

    private CollectingStepEventListener listener;

    private Map<String, Serializable> context;

    private Throwable throwable;

    public StepExecutorRunner( StepExecutor executor, StepDescriptor stepDescriptor, CollectingStepEventListener listener )
    {
        this.executor = executor;
        this.stepDescriptor = stepDescriptor;
        this.listener = listener;
    }

    public StepExecutor getExecutor()
    {
        return executor;
    }

    public StepDescriptor getStepDescriptor()
    {
        return stepDescriptor;
    }

    public CollectingStepEventListener getListener()
    {
        return listener;
    }

    public Map<String, Serializable> getContext()
    {
        return context;
    }

    public void setContext( Map<String, Serializable> context )
    {
        this.context = context;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }

    // ----------------------------------------------------------------------
    // Runnable Implementation
    // ----------------------------------------------------------------------

    public void run()
    {
        try
        {
            executor.execute( stepDescriptor, context, listener );
        }
        catch ( Throwable e )
        {
            this.throwable = e;
        }
    }
}
