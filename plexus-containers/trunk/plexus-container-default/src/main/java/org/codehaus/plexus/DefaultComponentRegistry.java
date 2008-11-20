package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.IdentityHashMap;
import java.util.Collections;

public class DefaultComponentRegistry implements ComponentRegistry
{
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final MutablePlexusContainer container;
    private final ComponentRepository repository;
    private final LifecycleHandlerManager lifecycleHandlerManager;
    private final Logger logger;

    private final Map<String, ComponentManagerFactory> componentManagerFactories =
        Collections.synchronizedMap( new TreeMap<String, ComponentManagerFactory>() );

    private final Map<Key, ComponentManager<?>> componentManagers = new TreeMap<Key, ComponentManager<?>>();
    private final IdentityHashMap<Object, ComponentManager<?>> componentManagersByComponent = new IdentityHashMap<Object, ComponentManager<?>>();

    public DefaultComponentRegistry( MutablePlexusContainer container,
                                     ComponentRepository repository,
                                     LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.container = container;
        this.repository = repository;
        this.lifecycleHandlerManager = lifecycleHandlerManager;
        logger = container.getLogger();
    }

    public void dispose()
    {
        Collection<ComponentManager<?>> managers;
        synchronized ( this )
        {
            managers = new ArrayList<ComponentManager<?>>( componentManagers.values() );
            componentManagers.clear();
            componentManagersByComponent.clear();
        }

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager<?> componentManager : managers )
        {
            try
            {
                componentManager.dispose();
            }
            catch ( Exception e )
            {
                // todo dain use a monitor instead of a logger
                logger.error( "Error while disposing component manager. Continuing with the rest", e );
            }
        }
    }

    public void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor ) throws ComponentRepositoryException
    {
        repository.addComponentDescriptor( componentDescriptor );
    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String role, String roleHint )
    {
        return repository.getComponentDescriptor( type, role, roleHint );
    }

    @Deprecated
    @SuppressWarnings({"deprecation"})
    public ComponentDescriptor<?> getComponentDescriptor( String role, String roleHint, ClassRealm realm )
    {
        return repository.getComponentDescriptor( role, roleHint, realm );
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type, String role )
    {
        return repository.getComponentDescriptorMap( type, role );
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type, String role )
    {
        return repository.getComponentDescriptorList( type, role );
    }

    public <T> T lookup( Class<T> type, String role, String roleHint ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }

        return getComponent( type, role, roleHint, null );
    }

    public <T> Map<String, T> lookupMap( Class<T> type, String role, List<String> roleHints )
        throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        Map<String, T> components = new LinkedHashMap<String, T>();
        if ( roleHints == null )
        {
            Map<String, ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorMap( type, role );
            for ( Entry<String, ComponentDescriptor<T>> entry : componentDescriptors.entrySet() )
            {
                String roleHint = entry.getKey();
                ComponentDescriptor<T> componentDescriptor = entry.getValue();
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, componentDescriptor );
                components.put( roleHint, component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, null );
                components.put( roleHint, component );
            }
        }

        return components;
    }

    public <T> List<T> lookupList( Class<T> type, String role, List<String> roleHints ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        List<T> components = new ArrayList<T>();
        if ( roleHints == null )
        {
            List<ComponentDescriptor<T>> componentDescriptors = getComponentDescriptorList( type, role );
            for ( ComponentDescriptor<T> componentDescriptor : componentDescriptors )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, componentDescriptor.getRoleHint(), componentDescriptor );
                components.add( component );
            }
        }
        else
        {
            for ( String roleHint : roleHints )
            {
                // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
                T component = getComponent( type, role, roleHint, null );
                components.add( component );
            }
        }

        return components;
    }

    public void release( Object component )
        throws ComponentLifecycleException
    {
        if ( component == null )
        {
            return;
        }

        // get the comonent manager
        ComponentManager<?> componentManager;
        synchronized ( this )
        {
            componentManager = componentManagersByComponent.get( component );
            if ( componentManager == null )
            {
                // This needs to be tracked down but the user doesn't need to see this
                logger.debug( "Component manager not found for returned component. Ignored. component=" + component );
                return;
            }
        }

        // release the component from the manager
        componentManager.release( component );

        // only drop the reference to this component if there are no other uses of the component
        // multiple uses of a component is common with singleton beans
        if ( componentManager.getConnections() <= 0 )
        {
            synchronized ( this )
            {
                componentManagersByComponent.remove( component );
            }
        }
    }

    public void removeComponentRealm( ClassRealm classRealm ) throws PlexusContainerException
    {
        repository.removeComponentRealm( classRealm );

        List<ComponentManager<?>> dispose = new ArrayList<ComponentManager<?>>();
        try
        {
            synchronized ( this )
            {
                for ( Iterator<Entry<Key, ComponentManager<?>>> it = componentManagers.entrySet().iterator(); it.hasNext(); )
                {
                    Entry<Key, ComponentManager<?>> entry = it.next();
                    Key key = entry.getKey();

                    ComponentManager<?> componentManager = entry.getValue();

                    if ( key.realm.equals( classRealm ) )
                    {
                        dispose.add( componentManager );
                        it.remove();
                    }
                    else
                    {
                        componentManager.dissociateComponentRealm( classRealm );
                    }
                }
            }

            // Call dispose callback outside of synchronized lock to avoid deadlocks
            for ( ComponentManager<?> componentManager : dispose )
            {
                componentManager.dispose();
            }
        }
        catch ( ComponentLifecycleException e )
        {
            throw new PlexusContainerException( "Failed to dissociate component realm: " + classRealm.getId(), e );
        }
    }

    private <T> T getComponent( Class<T> type, String role, String roleHint, ComponentDescriptor<T> descriptor )
        throws ComponentLookupException
    {
        ComponentManager<T> componentManager = getComponentManager( type, role, roleHint );
        if ( componentManager == null )
        {
            // we need to create a component manager, but first we must have a descriptor
            if ( descriptor == null )
            {
                descriptor = getComponentDescriptor( type, role, roleHint );
                if ( descriptor == null )
                {
                    throw new ComponentLookupException(
                        "Component descriptor cannot be found in the component repository",
                        role,
                        roleHint );
                }
            }

            componentManager = createComponentManager( descriptor, role, roleHint );
        }

        // Get instance from manager... may result in creation
        try
        {
            T component = componentManager.getComponent();
            synchronized ( this )
            {
                componentManagersByComponent.put( component, componentManager );
            }
            return component;
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be created.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be started.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
        }
    }

    private synchronized <T> ComponentManager<T> getComponentManager( Class<T> type, String role, String roleHint )
    {
        LinkedHashSet<ClassRealm> realms = getSearchRealms();

        // return the component in the first realm
        for ( ClassRealm realm : realms )
        {
            ComponentManager<?> manager = componentManagers.get( new Key( realm, role, roleHint ) );
            if ( manager != null && isAssignableFrom( type, manager.getType() ) )
            {
                return (ComponentManager<T>) manager;
            }
        }
        return null;
    }

    private LinkedHashSet<ClassRealm> getSearchRealms()
    {
        // determine realms to search
        LinkedHashSet<ClassRealm> realms = new LinkedHashSet<ClassRealm>();
        for ( ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader = classLoader.getParent() )
        {
            if ( classLoader instanceof ClassRealm )
            {
                ClassRealm realm = (ClassRealm) classLoader;
                while ( realm != null )
                {
                    realms.add( realm );
                    realm = realm.getParentRealm();
                }
            }
        }

        if ( realms.isEmpty() )
        {
            realms.addAll( container.getClassWorld().getRealms() );
        }

        return realms;
    }

    private <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor,
                                                            String role,
                                                            String roleHint )
        throws ComponentLookupException
    {
        // Get the ComponentManagerFactory
        String instantiationStrategy = descriptor.getInstantiationStrategy();
        if ( instantiationStrategy == null )
        {
            instantiationStrategy = DEFAULT_INSTANTIATION_STRATEGY;
        }
        ComponentManagerFactory componentManagerFactory = componentManagerFactories.get( instantiationStrategy );
        if ( componentManagerFactory == null )
        {
            throw new ComponentLookupException( "Unsupported instantiation strategy: " + instantiationStrategy,
                role,
                roleHint,
                descriptor.getRealm() );
        }

        // Get the LifecycleHandler
        LifecycleHandler lifecycleHandler;
        try
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( descriptor.getLifecycleHandler() );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            throw new ComponentLookupException( "Undefined lifecycle handler: " + descriptor.getLifecycleHandler(),
                role,
                roleHint,
                descriptor.getRealm() );
        }

        // Create the ComponentManager
        ComponentManager<T> componentManager = componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            descriptor,
            role,
            roleHint );

        // Add componentManager to indexes
        synchronized ( this )
        {
            Key key = new Key( descriptor.getRealm(), role, roleHint );
            componentManagers.put( key, componentManager );
        }

        return componentManager;
    }

    private static class Key implements Comparable<Key>
    {
        private final ClassRealm realm;
        private final String role;
        private final String roleHint;
        private final int hashCode;

        private Key( ClassRealm realm, String role, String roleHint )
        {
            this.realm = realm;

            if ( role == null )
            {
                role = "null";
            }
            this.role = role;

            if ( roleHint == null )
            {
                roleHint = "null";
            }
            this.roleHint = roleHint;

            int hashCode;
            hashCode = ( realm != null ? realm.hashCode() : 0 );
            hashCode = 31 * hashCode + role.hashCode();
            hashCode = 31 * hashCode + roleHint.hashCode();
            this.hashCode = hashCode;
        }

        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            Key key = (Key) o;

            return !( realm != null ? !realm.equals( key.realm ) : key.realm != null ) &&
                role.equals( key.role ) &&
                roleHint.equals( key.roleHint );

        }

        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            return realm + "/" + role + "/" + roleHint;
        }

        public int compareTo( Key o )
        {
            int value;
            if ( realm != null )
            {
                value = realm.getId().compareTo( o.realm.getId() );
            }
            else
            {
                value = o.realm == null ? 0 : 1;
            }

            if ( value == 0 )
            {
                value = role.compareTo( o.role );
                if ( value == 0 )
                {
                    value = roleHint.compareTo( o.roleHint );
                }
            }
            return value;
        }
    }
}
