package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class DiscoverServiceComponentsPhase
    extends AbstractServiceDeploymentPhase
{
    public void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException
    {
        try
        {
            context.getContainer().discoverComponents( context.getContainer().getCoreRealm() );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ServiceDeploymentException( "Error while looking for new service components.", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new ServiceDeploymentException( "Error discovering components.", e );
        }
    }
}
