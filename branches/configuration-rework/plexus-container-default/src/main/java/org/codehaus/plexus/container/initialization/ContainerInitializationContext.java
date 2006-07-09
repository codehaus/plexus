package org.codehaus.plexus.container.initialization;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class ContainerInitializationContext
{
    private DefaultPlexusContainer container;

    private ClassWorld classWorld;

    private ClassRealm containerRealm;

    private PlexusConfiguration containerConfiguration;

    private BasicComponentConfigurator componentConfigurator;

    public ContainerInitializationContext( DefaultPlexusContainer container,
                                           ClassWorld classWorld,
                                           ClassRealm containerRealm,
                                           PlexusConfiguration configuration,
                                           BasicComponentConfigurator componentConfigurator)
    {
        this.container = container;
        this.classWorld = classWorld;
        this.containerRealm = containerRealm;
        this.containerConfiguration = configuration;
        this.componentConfigurator = componentConfigurator;
    }

    public DefaultPlexusContainer getContainer()
    {
        return container;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public ClassRealm getContainerRealm()
    {
        return containerRealm;
    }

    public PlexusConfiguration getContainerConfiguration()
    {
        return containerConfiguration;
    }

    public BasicComponentConfigurator getComponentConfigurator()
    {
        return componentConfigurator;
    }
}
