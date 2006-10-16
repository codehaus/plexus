package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.lifecycle.AppServerContext;
import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.appserver.service.deploy.ServiceDeployer;
import org.codehaus.plexus.appserver.supervisor.Supervisor;
import org.codehaus.plexus.appserver.supervisor.SupervisorException;
import org.codehaus.plexus.appserver.supervisor.SupervisorListener;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ServiceDeploymentPhase
    extends AbstractAppServerPhase
{
    private ServiceDeployer serviceDeployer;

    private Supervisor serviceSupervisor;

    public void execute( AppServerContext context )
        throws AppServerLifecycleException
    {
        try
        {
            serviceSupervisor.addDirectory( new File( context.getAppServerHome(), "services" ), new SupervisorListener()
            {
                public void onJarDiscovered( File jar )
                {
                    String name = jar.getName();

                    try
                    {
                        String serviceName = name.substring( 0, name.length() - 4 );

                        getLogger().info( serviceSupervisor.getName() + " is deploying " + serviceName + "." );

                        serviceDeployer.deploy( serviceName, jar );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error while deploying service " + name + ".", e );
                    }
                }
            } );

            serviceSupervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying services in the app server.", e );
        }
    }
}
