package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;

/**
 * @author Jason van Zyl
 */
public class AppInitializationPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        String name = "plexus.application." + context.getApplicationId();

        // ----------------------------------------------------------------------------
        // Create the container and start
        // ----------------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer;

        try
        {
            // This call will initialise and start the container

            applicationContainer = new DefaultPlexusContainer( name, context.getContextValues(),
                                                               context.getAppConfigurationFile().getAbsoluteFile(),
                                                               context.getAppRuntimeProfile().getApplicationWorld() );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }

        context.getAppRuntimeProfile().setApplicationContainer( applicationContainer );
    }
}
