package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;

/**
 * @author Jason van Zyl
 */
public class CreateAppRuntimeProfilePhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        //TODO: this is little redundant can probably use the context ... jvz.

        AppRuntimeProfile profile = new AppRuntimeProfile( context.getApplicationId(),
                                                           context.getApplicationsDirectory(),
                                                           context.getPar(),
                                                           context.getApplicationContainer(),
                                                           context.getAppServerContainer(),
                                                           context.getAppConfiguration() );

        context.setAppRuntimeProfile( profile );
    }
}
