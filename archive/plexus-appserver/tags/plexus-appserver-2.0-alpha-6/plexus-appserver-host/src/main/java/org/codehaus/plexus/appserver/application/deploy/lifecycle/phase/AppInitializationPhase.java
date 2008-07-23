package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

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

        DefaultPlexusContainer serverContainer = context.getAppServerContainer();

        // ----------------------------------------------------------------------------
        // Create the container and start
        // ----------------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer;

        try
        {
            // This call will initialise and start the container

            applicationContainer = new DefaultPlexusContainer( name, context.getContextValues(),
                                                               context.getAppConfigurationFile().getPath(),
                                                               serverContainer.getClassWorld()/*, serverContainer*/ );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }


        // ----------------------------------------------------------------------
        // Create the realm for the application
        // ----------------------------------------------------------------------

        ClassRealm realm = new ClassRealm( applicationContainer.getClassWorld(),
                                           "plexus.application." + context.getApplicationId(),
                                           applicationContainer.getContainerRealm() );

        applicationContainer.setContainerRealm( realm );

        context.setApplicationContainer( applicationContainer );

        context.getAppRuntimeProfile().setApplicationContainer( applicationContainer );
    }
}
