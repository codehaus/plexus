package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.appserver.PlexusServiceConstants;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Adds service /classes and /lib/*.jar to the service classpath. The order is important - entries earlier in the
 * classpath will override later entries, so /classes/META-INF/plexus/components.xml can be used to override/reconfigure
 * components declared in lib/*.jar.
 *
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public class AddServiceLibrariesPhase
    extends AbstractServiceDeploymentPhase
{
    public void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException
    {
        File classesDir = new File( context.getServiceDirectory(), PlexusServiceConstants.CLASSES_DIRECTORY );

        try
        {
            if ( classesDir.isDirectory() )
            {
                context.getRealm().addURL( classesDir.toURL() );
            }
        }
        catch ( IOException e )
        {
            throw new ServiceDeploymentException( "Cannot scan the service's /classes directory: " + classesDir, e );
        }

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
