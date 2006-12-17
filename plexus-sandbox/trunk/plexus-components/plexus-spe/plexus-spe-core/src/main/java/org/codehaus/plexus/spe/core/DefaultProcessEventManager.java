package org.codehaus.plexus.spe.core;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.spe.ProcessEvent;
import org.codehaus.plexus.spe.ProcessListener;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultProcessEventManager
    extends AbstractLogEnabled
    implements ProcessEventManager
{
    private final Set<ProcessListener> listeners = new HashSet<ProcessListener>();

    // ----------------------------------------------------------------------
    // ProcessEventManager Implementation
    // ----------------------------------------------------------------------

    public void addProcessListener( ProcessListener processListener )
    {
        synchronized( listeners )
        {
            listeners.add( processListener );
        }
    }

    public void removeProcessListener( ProcessListener processListener )
    {
        synchronized( listeners )
        {
            listeners.remove( processListener );
        }
    }

    public void sendEvent( ProcessEvent event )
    {
        synchronized( listeners )
        {
            for ( ProcessListener listener : listeners )
            {
                listener.onProcessEvent( event );
            }
        }
    }
}
