package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.appserver.deploy.DeploymentException;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

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
