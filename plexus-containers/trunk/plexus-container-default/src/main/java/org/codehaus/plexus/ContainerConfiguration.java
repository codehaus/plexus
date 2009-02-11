package org.codehaus.plexus;

import java.net.URL;
import java.util.Map;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.configuration.source.ConfigurationSource;
import org.codehaus.plexus.container.initialization.ContainerInitializationPhase;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;

/**
 * @author Jason van Zyl
 */
public interface ContainerConfiguration
{
    ContainerConfiguration setName( String name );

    String getName();

    ContainerConfiguration setContext( Map<Object, Object> context );

    Map<Object, Object> getContext();

    ContainerConfiguration setClassWorld( ClassWorld classWorld );

    ClassWorld getClassWorld();

    ContainerConfiguration setRealm( ClassRealm realm );

    ClassRealm getRealm();
    
    //
    // Configuration
    //
    ContainerConfiguration setContainerConfiguration( String configuration );

    String getContainerConfiguration();

    ContainerConfiguration setContainerConfigurationURL( URL configuration );

    URL getContainerConfigurationURL();

    // Programmatic Container Initialization and Setup

    // Much of this setup and initialization can be completely hidden. It's probably not likely
    // someone will need to change these core components, but rather adding things like different
    // factories, and component managers.

    // Container initialization phases

    ContainerInitializationPhase[] getInitializationPhases();

    // Component lookup manager

    // Component discoverer manager

    ContainerConfiguration addComponentDiscoverer( ComponentDiscoverer componentDiscoverer );

    ContainerConfiguration addComponentDiscoveryListener( ComponentDiscoveryListener componentDiscoveryListener );

    ContainerConfiguration setComponentDiscovererManager( ComponentDiscovererManager componentDiscovererManager );

    ComponentDiscovererManager getComponentDiscovererManager();

    // Component factory manager

    ContainerConfiguration setComponentFactoryManager( ComponentFactoryManager componentFactoryManager );

    ComponentFactoryManager getComponentFactoryManager();

    // Lifecycle handler manager

    ContainerConfiguration addLifecycleHandler( LifecycleHandler lifecycleHandler );

    ContainerConfiguration setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    LifecycleHandlerManager getLifecycleHandlerManager();

    // Configuration Sources

    ContainerConfiguration setConfigurationSource( ConfigurationSource configurationSource );

    ConfigurationSource getConfigurationSource();
}

