package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.deploy.DeploymentException;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Jason van Zyl
 */
public class ExpandServicePhase
    extends AbstractServiceDeploymentPhase
{
    public void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException
    {
        File serviceDir = new File( context.getServicesDirectory(), context.getServiceId() );

        context.setServiceDirectory( serviceDir );

        if ( serviceDir.exists() )
        {
            getLogger().info( "Removing old service." );

            try
            {
                FileUtils.deleteDirectory( serviceDir );
            }
            catch ( IOException e )
            {
                throw new ServiceDeploymentException( "Cannot delete old service deployment in " + serviceDir, e );
            }
        }

        try
        {
            expand( context.getSar(), serviceDir, false );
        }
        catch ( DeploymentException e )
        {
            throw new ServiceDeploymentException( "Error expanding service archive.", e );
        }
    }
}
