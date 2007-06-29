package org.codehaus.cargo.container.plexus;

import java.io.InputStream;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.internal.ServletContainerCapability;
import org.codehaus.cargo.container.spi.AbstractEmbeddedLocalContainer;
import org.codehaus.plexus.appserver.PlexusApplicationHost;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.launcher.Configurator;

/**
 * 
 * @author eredmond
 */
public class PlexusEmbeddedLocalContainer
    extends AbstractEmbeddedLocalContainer
{
    private ContainerCapability capability = new ServletContainerCapability();

    private PlexusApplicationHost host;

    public PlexusEmbeddedLocalContainer( LocalConfiguration configuration )
    {
        super( configuration );

        host = new PlexusApplicationHost();
    }

    protected void doStart() throws Exception
    {
        LocalConfiguration configuration = getConfiguration();

        ClassWorld classWorld = new ClassWorld();

        // how to set this configuration
        String classworldsConf = configuration.getPropertyValue( "classworlds.conf" );

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream is = cl.getResourceAsStream( classworldsConf );
        if(is == null)
        {
            throw new Exception("classworlds configuration not specified nor found in the classpath");
        }

        Configurator configurator = new Configurator( classWorld );
        configurator.configure( is );

        host.start( classWorld );
        host.run();
    }

    protected void doStop() throws Exception
    {
        host.shutdown();
    }

    public ContainerCapability getCapability()
    {
        return capability;
    }

    public String getId()
    {
        return "plexus";
    }

    public String getName()
    {
        return "Plexus Application Host";
    }
}
