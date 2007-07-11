package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;

/**
 * @author Jason van Zyl
 * @author Andrew Williams
 */
public class CreateAppRuntimeProfilePhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        AppRuntimeProfile profile = new AppRuntimeProfile( context.getApplicationId(),
                                                           context.getApplicationsDirectory(), context.getPar(),
                                                           context.getAppServerContainer() );

        context.setAppRuntimeProfile( profile );
    }
}
