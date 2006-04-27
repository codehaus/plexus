package org.codehaus.plexus.application.lifecycle.phase;

import org.codehaus.plexus.application.lifecycle.AppServerContext;
import org.codehaus.plexus.application.lifecycle.AppServerLifecycleException;

/**
 * @author Jason van Zyl
 */
public interface AppServerPhase
{
    String ROLE = AppServerPhase.class.getName();

    void execute( AppServerContext context )
        throws AppServerLifecycleException;
}
