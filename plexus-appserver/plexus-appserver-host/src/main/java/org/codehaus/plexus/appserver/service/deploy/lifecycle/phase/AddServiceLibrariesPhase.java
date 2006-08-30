package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class AddServiceLibrariesPhase
    extends AbstractServiceDeploymentPhase
{
    public void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException
    {
        File libDir = new File( context.getServiceDirectory(), "lib" );

        if ( !libDir.exists() )
        {
            throw new ServiceDeploymentException( "The service must have a /lib directory." );
        }

        context.getContainer().addJarRepository( libDir );
    }
}
