package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.deploy.DeploymentException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ExpandAppPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        String appId = context.getApplicationId();

        File directory = context.getApplicationsDirectory();

        File file = context.getPar();

        File location = FileUtils.resolveFile( directory, appId );

        // Don't extract if it has been extracted before.
        if ( location.exists() )
        {
            getLogger().info( "Application '" + appId + "' already extracted." );
        }
        else
        {
            getLogger().info( "Extracting " + file + " to '" + location.getAbsolutePath() + "'." );

            try
            {
                expand( file, location, false );
            }
            catch ( DeploymentException e )
            {
                throw new AppDeploymentException( "Could not deploy the JAR", e );
            }
        }

        getLogger().info( "Deploying application '" + appId + "' at '" + location.getAbsolutePath() + "'." );

        if ( context.getDeployments().containsKey( appId ) )
        {
            throw new AppDeploymentException(
                "A appserver with the specified appId ('" + appId + "') already exists." );
        }
    }
}
