package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.net.URL;
import java.util.Map;

/** @author Jason van Zyl */
public class DefaultContainerConfiguration
    implements ContainerConfiguration
{
    private String name;

    private Map context;

    private ClassWorld classWorld;

    private ClassRealm realm;

    private PlexusContainer parentContainer;

    private String containerConfiguration;

    private URL containerConfigurationURL;

    public ContainerConfiguration setName( String name )
    {
        this.name = name;

        return this;
    }

    public ContainerConfiguration setContext( Map context )
    {
        this.context = context;

        return this;
    }

    public ContainerConfiguration setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;

        return this;
    }

    public ContainerConfiguration setRealm( ClassRealm realm )
    {
        this.realm = realm;

        return this;
    }

    public ContainerConfiguration setParentContainer( PlexusContainer parentContainer )
    {
        this.parentContainer = parentContainer;

        return this;
    }

    public ContainerConfiguration setContainerConfiguration( String containerConfiguration )
    {
        this.containerConfiguration = containerConfiguration;

        return this;
    }

    public String getContainerConfiguration()
    {
        return containerConfiguration;
    }

    public ContainerConfiguration setContainerConfigurationURL( URL containerConfiguration )
    {
        this.containerConfigurationURL = containerConfiguration;

        return this;
    }

    public URL getContainerConfigurationURL()
    {
        return containerConfigurationURL;
    }

    public String getName()
    {
        return name;
    }

    public Map getContext()
    {
        return context;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public PlexusContainer getParentContainer()
    {
        return parentContainer;
    }

    public ClassRealm getRealm()
    {
        return realm;
    }

    // Programmatic Container Initialization and Setup

    public ContainerConfiguration setInitializationPhases( String[] initializationPhases )
    {
        this.initializationPhases = initializationPhases;

        return this;
    }

    public String[] getInitializationPhases()
    {
        return initializationPhases;
    }

    private String[] initializationPhases =
        {
            "org.codehaus.plexus.container.initialization.InitializeResourcesPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentRepositoryPhase",
            "org.codehaus.plexus.container.initialization.InitializeLifecycleHandlerManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentManagerManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentDiscovererManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentFactoryManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentLookupManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeComponentComposerPhase",
            "org.codehaus.plexus.container.initialization.InitializeLoggerManagerPhase",
            "org.codehaus.plexus.container.initialization.InitializeContextPhase",
            "org.codehaus.plexus.container.initialization.InitializeSystemPropertiesPhase",
            "org.codehaus.plexus.container.initialization.RegisterComponentDiscoveryListenersPhase",
            "org.codehaus.plexus.container.initialization.ComponentDiscoveryPhase",
            "org.codehaus.plexus.container.initialization.StartLoadOnStartComponentsPhase",

        };

    public String getComponentLookupManager()
    {
        return "org.codehaus.plexus.DefaultComponentLookupManager";        
    }
}
