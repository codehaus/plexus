package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class AfterAppStartServiceSetupPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        AppRuntimeProfile profile = context.getAppRuntimeProfile();

        for ( int i = 0; i < profile.getServices().size(); i++ )
        {
            PlexusService service = (PlexusService) profile.getServices().get( i );

            PlexusConfiguration configuration = (PlexusConfiguration) profile.getServiceConfigurations().get( i );

            try
            {
                service.afterApplicationStart( profile, configuration );
            }
            catch ( Exception e )
            {
                throw new AppDeploymentException( "Error calling service in pre-app init phase.", e );
            }
        }
    }
}
