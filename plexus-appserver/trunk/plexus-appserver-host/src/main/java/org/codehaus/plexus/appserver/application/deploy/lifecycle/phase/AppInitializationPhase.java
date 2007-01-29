package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.service.PlexusServiceException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.util.FileUtils;

import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author Jason van Zyl
 */
public class AppInitializationPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        try
        {
            ClassRealm realm = context.getAppRuntimeProfile().getApplicationRealm();

            List jars = FileUtils.getFiles( context.getAppLibDirectory(), "*.jar", null );

            for ( Iterator i = jars.iterator(); i.hasNext(); )
            {
                File file = (File) i.next();

                realm.addURL( file.toURL() );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new AppDeploymentException( e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( e.getMessage(), e );
        }

    }
}
