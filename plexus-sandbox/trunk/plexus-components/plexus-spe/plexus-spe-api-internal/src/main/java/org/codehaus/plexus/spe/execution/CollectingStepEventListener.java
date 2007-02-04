package org.codehaus.plexus.spe.execution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CollectingStepEventListener
    implements StepEventListener
{
    private List<String> logMessages = new ArrayList<String>();

    // -----------------------------------------------------------------------
    // StepEventListener Implementation
    // -----------------------------------------------------------------------

    public synchronized void onLogMessage( String string )
    {
        logMessages.add( string );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public List<String> getLogMessages()
    {
        return logMessages;
    }
}
