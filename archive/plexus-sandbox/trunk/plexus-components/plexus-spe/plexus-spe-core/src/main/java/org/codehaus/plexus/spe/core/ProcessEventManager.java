package org.codehaus.plexus.spe.core;

import org.codehaus.plexus.spe.ProcessListener;
import org.codehaus.plexus.spe.ProcessEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ProcessEventManager
{
    String ROLE = ProcessEventManager.class.getName();

    void addProcessListener( ProcessListener processListener );

    void removeProcessListener( ProcessListener processListener );

    void sendEvent( ProcessEvent event );
}
