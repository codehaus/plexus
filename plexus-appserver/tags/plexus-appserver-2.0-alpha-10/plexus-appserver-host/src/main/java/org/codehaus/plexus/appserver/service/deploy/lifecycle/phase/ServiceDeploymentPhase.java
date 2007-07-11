package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;

/**
 * @author Jason van Zyl
 */
public interface ServiceDeploymentPhase
{
    String ROLE = ServiceDeploymentPhase.class.getName();

    void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException;
}
