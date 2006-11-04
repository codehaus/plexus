package org.codehaus.plexus.spe.event;

import org.codehaus.plexus.spe.ProcessEvent;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepInstance;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StepCompletedEvent
    implements ProcessEvent
{
    private ProcessInstance processInstance;

    private StepInstance stepInstance;

    private Throwable throwable;

    public ProcessInstance getProcessInstance()
    {
        return processInstance;
    }

    public void setProcessInstance( ProcessInstance processInstance )
    {
        this.processInstance = processInstance;
    }

    public StepInstance getStepInstance()
    {
        return stepInstance;
    }

    public void setStepInstance( StepInstance stepInstance )
    {
        this.stepInstance = stepInstance;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }

    public void setThrowable( Throwable throwable )
    {
        this.throwable = throwable;
    }
}
