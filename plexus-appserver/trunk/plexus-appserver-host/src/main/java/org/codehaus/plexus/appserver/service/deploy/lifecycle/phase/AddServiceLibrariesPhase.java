package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.util.FileUtils;

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

        try
        {
            List files = FileUtils.getFiles( libDir, "*.jar", null );

            for ( Iterator it = files.iterator(); it.hasNext(); )
            {
                context.getRealm().addURL( ( (File) it.next() ).toURL() );
            }
        }
        catch ( IOException e )
        {
            throw new ServiceDeploymentException( "Cannot scan the service's /lib directory: " + libDir, e );
        }
    }
}
