package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

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
        try
        {
            context.getApplicationContainer().initialize();

            context.getApplicationContainer().start();
        }
        catch ( Exception e )
        {
            throw new AppDeploymentException( "Error starting Plexus.", e );
        }
    }
}
