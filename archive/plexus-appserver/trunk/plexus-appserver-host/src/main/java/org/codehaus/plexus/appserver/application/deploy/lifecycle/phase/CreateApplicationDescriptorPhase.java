package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.AppDescriptor;
import org.codehaus.plexus.appserver.AppServerObject;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;

/**
 * @author Jason van Zyl
 */
public class CreateApplicationDescriptorPhase
    extends AppServerObject
    implements AppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        ApplicationServer appServer = context.getAppServer();

        AppDescriptor appDescriptor = new AppDescriptor( context.getApplicationId(), context.getApplicationId(),
                                                         context.getPar(), context.getAppDir() );

        appServer.addAppDescriptor( appDescriptor );
    }
}
