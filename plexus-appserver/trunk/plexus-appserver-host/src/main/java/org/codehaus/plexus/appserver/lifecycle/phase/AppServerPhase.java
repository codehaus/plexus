package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.appserver.ApplicationServer;

/**
 * @author Jason van Zyl
 */
public interface AppServerPhase
{
    String ROLE = AppServerPhase.class.getName();

    void execute( ApplicationServer appServer )
        throws AppServerLifecycleException;
}
