package org.codehaus.plexus.application.lifecycle.phase.deploy.application;

import org.codehaus.plexus.application.lifecycle.AppServerContext;
import org.codehaus.plexus.application.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.application.lifecycle.phase.AbstractAppServerPhase;
import org.codehaus.plexus.application.supervisor.Supervisor;
import org.codehaus.plexus.application.supervisor.SupervisorException;
import org.codehaus.plexus.application.supervisor.SupervisorListener;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ApplicationDeploymentPhase
    extends AbstractAppServerPhase
{
    private ApplicationDeployer applicationDeployer;

    private Supervisor supervisor;

    public void execute( AppServerContext context )
        throws AppServerLifecycleException
    {
        try
        {
            supervisor.addDirectory( new File( context.getAppServerHome(), "apps" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        String appName = name.substring( 0, name.length() - 4 );

                        getLogger().info( "Deploying " + appName + "." );

                        applicationDeployer.deploy( appName, jar.toURL().toExternalForm() );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error while deploying application " + name + ".", e );
                    }
                }
            } );

            supervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying applications in the app server.", e );
        }
    }
}
