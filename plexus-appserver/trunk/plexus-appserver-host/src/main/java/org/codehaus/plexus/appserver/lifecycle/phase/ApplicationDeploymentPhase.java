package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.appserver.lifecycle.AppServerContext;
import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.appserver.supervisor.Supervisor;
import org.codehaus.plexus.appserver.supervisor.SupervisorException;
import org.codehaus.plexus.appserver.supervisor.SupervisorListener;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ApplicationDeploymentPhase
    extends AbstractAppServerPhase
{
    private ApplicationDeployer applicationDeployer;

    private Supervisor applicationSupervisor;

    public void execute( AppServerContext context )
        throws AppServerLifecycleException
    {
        try
        {
            applicationSupervisor.addDirectory( new File( context.getAppServerHome(), "apps" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        String appName = name.substring( 0, name.length() - 4 );

                        getLogger().info( applicationSupervisor.getName() + " is deploying " + appName + "." );

                        applicationDeployer.deploy( appName, jar );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error while deploying appserver " + name + ".", e );
                    }
                }
            } );

            applicationSupervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying applications in the app server.", e );
        }
    }
}
