package org.codehaus.plexus.spe.event;

import org.codehaus.plexus.spe.ProcessEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StartingStepEvent
    implements ProcessEvent
{
    private String processId;

    private int processInstanceId;

    private int stepNumber;

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId( String processId )
    {
        this.processId = processId;
    }

    public int getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId( int processInstanceId )
    {
        this.processInstanceId = processInstanceId;
    }

    public int getStepNumber()
    {
        return stepNumber;
    }

    public void setStepNumber( int stepNumber )
    {
        this.stepNumber = stepNumber;
    }
}
