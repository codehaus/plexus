package org.codehaus.plexus.application.lifecycle.phase.deploy.service;

import org.codehaus.plexus.application.lifecycle.phase.AbstractAppServerPhase;
import org.codehaus.plexus.application.lifecycle.AppServerContext;
import org.codehaus.plexus.application.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.application.supervisor.SupervisorListener;
import org.codehaus.plexus.application.supervisor.SupervisorException;
import org.codehaus.plexus.application.supervisor.Supervisor;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentPhase
    extends AbstractAppServerPhase
{
    private ServiceDeployer serviceDeployer;

    private Supervisor supervisor;

    public void execute( AppServerContext context )
        throws AppServerLifecycleException
    {
        try
        {
            supervisor.addDirectory( new File( context.getAppServerHome(), "services" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        String serviceName = name.substring( 0, name.length() - 4 );

                        serviceDeployer.deploy( serviceName, jar.getAbsolutePath() );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error while deploying service " + name + ".", e );
                    }
                }
            } );

            supervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying services in the app server.", e );
        }
    }
}
