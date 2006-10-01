package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;

/**
 * @author Jason van Zyl
 */
public interface AppDeploymentPhase
{
    String ROLE = AppDeploymentPhase.class.getName();

    void execute( AppDeploymentContext context )
        throws AppDeploymentException;
}
