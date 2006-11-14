package org.codehaus.plexus.appserver.lifecycle.phase;

import org.codehaus.plexus.appserver.application.deploy.ApplicationDeployer;
import org.codehaus.plexus.appserver.lifecycle.AppServerLifecycleException;
import org.codehaus.plexus.appserver.supervisor.Supervisor;
import org.codehaus.plexus.appserver.supervisor.SupervisorException;
import org.codehaus.plexus.appserver.supervisor.SupervisorListener;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class ApplicationDeploymentPhase
    extends AbstractAppServerPhase
{
    private ApplicationDeployer applicationDeployer;

    private Supervisor applicationSupervisor;

    public void execute( ApplicationServer appServer )
        throws AppServerLifecycleException
    {
        try
        {
            File appsDirectory = new File( appServer.getAppServerHome(), PlexusRuntimeConstants.APPLICATIONS_DIRECTORY );
            if ( appsDirectory.exists() && appsDirectory.isDirectory() )
            {
                applicationSupervisor.addDirectory( appsDirectory, new SupervisorListener()
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
                        catch ( ApplicationServerException e )
                        {
                            getLogger().error( "Error while deploying appserver " + name + ".", e );
                        }
                    }
                } );
            }
            else
            {
                getLogger().info( "No apps directory exists - not scanning for applications" );
            }

            applicationSupervisor.scan();
        }
        catch ( SupervisorException e )
        {
            throw new AppServerLifecycleException( "Error deploying applications in the app server.", e );
        }
    }
}
