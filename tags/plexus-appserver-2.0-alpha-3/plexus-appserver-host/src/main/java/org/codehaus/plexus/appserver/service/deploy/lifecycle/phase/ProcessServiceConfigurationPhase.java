package org.codehaus.plexus.appserver.service.deploy.lifecycle.phase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.PlexusServiceConstants;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentContext;
import org.codehaus.plexus.appserver.service.deploy.lifecycle.ServiceDeploymentException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * @author Jason van Zyl
 */
public class ProcessServiceConfigurationPhase
    extends AbstractServiceDeploymentPhase
{
    public void execute( ServiceDeploymentContext context )
        throws ServiceDeploymentException
    {
        File config = new File( new File( context.getServiceDirectory(), PlexusServiceConstants.CONF_DIRECTORY ),
                                PlexusServiceConstants.CONFIGURATION_FILE );

        /**
         * The first time the configurator runs, we don't care
         * what the user configurator is, so only use the components.xml
         * files.  However, we still need to load on start components.
         */

        try
        {
            Reader reader = new FileReader( config );

            PlexusConfiguration serviceConfig = PlexusTools.buildConfiguration( config.getPath(), reader );

            startComponents( serviceConfig, context.getContainer() );
        }
        catch ( FileNotFoundException e )
        {
            throw new ServiceDeploymentException( "Cannot find configuration file.", e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ServiceDeploymentException( "Error reading service configuration.", e );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServiceDeploymentException( "Error starting service.", e );
        }
    }

    private void startComponents( PlexusConfiguration serviceConfig, DefaultPlexusContainer container )
        throws PlexusConfigurationException, ComponentLookupException
    {
        PlexusConfiguration[] loadOnStartComponents =
            serviceConfig.getChild( "load-on-start" ).getChildren( "component" );

        getLogger().debug( "Found " + loadOnStartComponents.length + " components to load on start" );

        for ( int i = 0; i < loadOnStartComponents.length; i++ )
        {
            String role = loadOnStartComponents[i].getChild( "role" ).getValue( null );

            String roleHint = loadOnStartComponents[i].getChild( "role-hint" ).getValue();

            if ( role == null )
            {
                throw new PlexusConfigurationException( "Missing 'role' element from load-on-start." );
            }

            if ( roleHint == null )
            {
                getLogger().info( "Loading on start [role]: " + "[" + role + "]" );
            }
            else
            {
                getLogger().info( "Loading on start [role,roleHint]: " + "[" + role + "," + roleHint + "]" );
            }

            if ( roleHint == null )
            {
                container.lookup( role );
            }
            else
            {
                container.lookup( role, roleHint );
            }
        }
    }
}
