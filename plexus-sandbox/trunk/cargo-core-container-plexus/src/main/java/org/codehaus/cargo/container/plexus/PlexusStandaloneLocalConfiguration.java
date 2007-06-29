package org.codehaus.cargo.container.plexus;

import java.io.File;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfiguration;

/**
 * 
 * @author eredmond
 */
public class PlexusStandaloneLocalConfiguration
    extends AbstractStandaloneLocalConfiguration
{
    private ConfigurationCapability capability = new PlexusStandaloneLocalConfigurationCapability();

    public PlexusStandaloneLocalConfiguration( String dir )
    {
        super( dir );
    }

    public ConfigurationCapability getCapability()
    {
        return capability;
    }

    protected void doConfigure( LocalContainer localcontainer ) throws Exception
    {
        try
        {
            setupConfigurationDir();

            // Jetty does this... not really sure why
            getResourceUtils().copyResource( RESOURCE_PATH + "cargocpc.war", new File( getHome(), "cargocpc.war" ) );
        }
        catch (Exception e)
        {
            throw new ContainerException( "Failed to create a " + localcontainer.getName() + " " + getType().getType() + " configuration", e);
        }
    }
}
