package org.codehaus.plexus.spe.execution;

import org.codehaus.plexus.spe.model.LogMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CollectingStepEventListener
    implements StepEventListener
{
    private List<LogMessage> logMessages = new ArrayList<LogMessage>();

    // -----------------------------------------------------------------------
    // StepEventListener Implementation
    // -----------------------------------------------------------------------

    public synchronized void onLogMessage( String string )
    {
        LogMessage message = new LogMessage();

        message.setMessage( string );
        message.setTimestamp( System.currentTimeMillis() );

        logMessages.add( message );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public List<LogMessage> getLogMessages()
    {
        return logMessages;
    }
}
