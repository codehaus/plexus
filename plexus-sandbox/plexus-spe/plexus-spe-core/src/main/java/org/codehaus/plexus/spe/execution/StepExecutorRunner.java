package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.model.StepDescriptor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StepExecutorRunner
    implements Runnable
{
    private StepExecutor executor;

    private StepDescriptor stepDescriptor;

    private Map<String, Serializable> context;

    public StepExecutorRunner( StepExecutor executor, StepDescriptor stepDescriptor )
    {
        this.executor = executor;
        this.stepDescriptor = stepDescriptor;
    }

    public StepExecutor getExecutor()
    {
        return executor;
    }

    public StepDescriptor getStepDescriptor()
    {
        return stepDescriptor;
    }

    public Map<String, Serializable> getContext()
    {
        return context;
    }

    public void setContext( Map<String, Serializable> context )
    {
        this.context = context;
    }

    // ----------------------------------------------------------------------
    // Runnable Implementation
    // ----------------------------------------------------------------------

    public void run()
    {
        try
        {
            executor.execute( stepDescriptor, context );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
