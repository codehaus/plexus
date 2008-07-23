package org.codehaus.plexus.spe.event;

import org.codehaus.plexus.spe.ProcessEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StartingProcessEvent
    implements ProcessEvent
{
    private String processId;

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId( String processId )
    {
        this.processId = processId;
    }
}
