package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;
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

    public void execute( ApplicationServer appServer )
        throws AppServerLifecycleException
    {
        try
        {
            File servicesDirectory =
                new File( appServer.getAppServerHome(), PlexusRuntimeConstants.SERVICES_DIRECTORY );
            if ( servicesDirectory.exists() && servicesDirectory.isDirectory() )
            {
                serviceSupervisor.addDirectory( servicesDirectory, new SupervisorListener()
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
                        catch ( ApplicationServerException e )
                        {
                            getLogger().error( "Error while deploying service " + name + ".", e );
                        }
                    }
                } );
            }
            else
            {
                getLogger().info( "No services directory exists - not adding to scanner" );
            }

            serviceSupervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying services in the app server.", e );
        }
    }
}
