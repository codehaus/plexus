package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.lifecycle.AppServerContext;
import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;

/**
 * @author Jason van Zyl
 */
public interface AppServerPhase
{
    String ROLE = AppServerPhase.class.getName();

    void execute( AppServerContext context )
        throws AppServerLifecycleException;
}
