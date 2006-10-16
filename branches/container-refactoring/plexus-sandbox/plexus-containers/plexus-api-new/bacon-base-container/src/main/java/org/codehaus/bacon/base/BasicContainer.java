package org.codehaus.bacon.base;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.ContainerConfiguration;
import org.codehaus.bacon.ContainerException;
import org.codehaus.bacon.LookupException;
import org.codehaus.bacon.base.component.BasicInstanceManager;
import org.codehaus.bacon.base.session.BasicContainerSession;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentSelector;
import org.codehaus.bacon.component.ComponentUtilities;
import org.codehaus.bacon.component.DuplicateComponentDescriptorException;
import org.codehaus.bacon.component.factory.InstantiationException;
import org.codehaus.bacon.component.manager.InstanceManager;
import org.codehaus.bacon.session.ContainerSession;
import org.codehaus.bacon.session.ContainerSessionManager;
import org.codehaus.bacon.session.DuplicateContainerSessionException;
import org.codehaus.bacon.session.SessionKey;

public class BasicContainer
    implements Container
{
    private Container parent;

    private Map children;

    private final String id;

    private final Date creationDate;

    private final Set componentDescriptors;

    private final Set componentSelectors;

    private final URLClassLoader containerLoader;
    
    private final InstanceManager instanceManager;

    private Map componentSelectorsByInterface;

    private Map componentDescriptorsByKey;

    public BasicContainer( String id, ContainerConfiguration configuration )
        throws ContainerException
    {
        this.id = id;
        this.creationDate = new Date();

        List cpElements = configuration.getClasspathElements();

        URL[] cpUrls = new URL[cpElements.size()];

        cpUrls = (URL[]) cpElements.toArray( cpUrls );

        this.containerLoader = URLClassLoader.newInstance( cpUrls );

        this.componentDescriptors = Collections.unmodifiableSet( configuration.getComponentDescriptors() );
        this.componentSelectors = Collections.unmodifiableSet( configuration.getComponentSelectors() );
        
        this.instanceManager = new BasicInstanceManager( configuration.getLanguagePacksByKey(), this, containerLoader );

        createComponentDescriptorMap();

        createComponentSelectorMap();
    }

    public BasicContainer( String id, ContainerConfiguration configuration, ClassLoader parentClassLoader )
        throws ContainerException
    {
        this.id = id;
        this.creationDate = new Date();

        List cpElements = configuration.getClasspathElements();

        URL[] cpUrls = new URL[cpElements.size()];

        cpUrls = (URL[]) cpElements.toArray( cpUrls );

        this.containerLoader = URLClassLoader.newInstance( cpUrls, parentClassLoader );

        this.componentDescriptors = configuration.getComponentDescriptors();
        this.componentSelectors = configuration.getComponentSelectors();
        
        this.instanceManager = new BasicInstanceManager( configuration.getLanguagePacksByKey(), this, containerLoader );

        createComponentDescriptorMap();

        createComponentSelectorMap();
    }

    public Object lookup( String ifc, String instance, SessionKey sessionKey, Map context )
        throws LookupException, ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( ifc, instance );

        ComponentDescriptor descriptor = (ComponentDescriptor) componentDescriptorsByKey.get( key );

        ContainerSession session = getContainerSession( sessionKey );

        boolean nativeToContainer = true;
        if ( descriptor == null )
        {
            String inst = instance;

            if ( inst == null || inst.trim().length() < 1 )
            {
                inst = ComponentDescriptor.NO_INSTANCE_NAME;
            }

            ComponentSelector selector = new AdHocComponentSelector( ifc, inst );

            descriptor = session.getComponentDescriptor( ifc, selector );
            nativeToContainer = false;
        }

        Object result = null;

        if ( descriptor != null )
        {
            result = getComponentInstance( descriptor, sessionKey, context, nativeToContainer );
        }
        else if ( parent != null )
        {
            result = parent.lookup( ifc, instance, sessionKey, context );
        }

        return result;
    }

    public Object lookup( String ifc, SessionKey sessionKey, Map context )
        throws LookupException, ContainerException
    {
        ComponentSelector selector = (ComponentSelector) componentSelectorsByInterface.get( ifc );

        ContainerSession session = getContainerSession( sessionKey );

        if ( selector == null )
        {
            selector = session.getComponentSelector( ifc );
        }

        if ( selector == null )
        {
            selector = new AdHocComponentSelector( ifc, ComponentDescriptor.NO_INSTANCE_NAME );
        }

        ComponentDescriptor descriptor = null;

        List instanceNames = selector.getSelectedInstancesInOrder();

        for ( Iterator it = instanceNames.iterator(); it.hasNext(); )
        {
            String instanceName = (String) it.next();

            String key = ComponentUtilities.createDescriptorKey( ifc, instanceName );

            descriptor = (ComponentDescriptor) componentDescriptorsByKey.get( key );

            if ( descriptor != null )
            {
                break;
            }
        }

        boolean nativeToContainer = true;
        
        if ( descriptor == null )
        {
            descriptor = session.getComponentDescriptor( ifc, selector );
            nativeToContainer = false;
        }

        Object result = null;

        if ( descriptor != null )
        {
            result = getComponentInstance( descriptor, sessionKey, context, nativeToContainer );
        }
        else if ( parent != null )
        {
            result = parent.lookup( ifc, sessionKey, context );
        }

        return result;
    }

    private Object getComponentInstance( ComponentDescriptor descriptor, SessionKey sessionKey, Map context, boolean nativeToContainer )
        throws InstantiationException
    {
        return instanceManager.getInstance( descriptor, sessionKey, context, nativeToContainer );
    }

    private ContainerSession getContainerSession( SessionKey sessionKey )
        throws DuplicateContainerSessionException
    {
        ContainerSession session = ContainerSessionManager.instance().get( sessionKey, id );

        if ( session == null )
        {
            session = new BasicContainerSession( sessionKey, this );

            ContainerSessionManager.instance().register( session );
        }

        return session;
    }

    public void registerComponent( ComponentDescriptor descriptor, SessionKey sessionKey )
        throws DuplicateComponentDescriptorException, ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );

        if ( componentDescriptorsByKey.containsKey( key ) )
        {
            throw new DuplicateComponentDescriptorException( "Container: " + id
                + " already contains a component matching descriptor: " + descriptor );
        }

        ContainerSession session = getContainerSession( sessionKey );

        session.registerComponentDescriptor( descriptor );
    }

    public void registerSelector( ComponentSelector selector, SessionKey sessionKey )
        throws ContainerException
    {
        ContainerSession session = getContainerSession( sessionKey );

        session.registerComponentSelector( selector );
    }

    public void setParentContainer( Container parent )
    {
        this.parent = parent;
    }

    public Container getParentContainer()
    {
        return parent;
    }

    public void addChildContainer( Container child )
    {
        synchronized ( this )
        {
            if ( children == null )
            {
                children = new HashMap();
            }

            children.put( child.getContainerId(), child );
        }
    }

    public Container getChildContainer( String childId )
    {
        return (Container) ( children == null ? null : children.get( childId ) );
    }

    public String getContainerId()
    {
        return id;
    }

    public boolean containsComponent( ComponentDescriptor descriptor, SessionKey sessionKey )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );

        boolean result = componentDescriptorsByKey.containsKey( key );

        if ( !result && parent != null )
        {
            result = parent.containsComponent( descriptor, sessionKey );
        }

        if ( !result )
        {
            ContainerSession session = getContainerSession( sessionKey );
            result = session.containsComponentDescriptor( descriptor );
        }

        return result;
    }

    public boolean containsComponent( String ifc, String instance, SessionKey sessionKey )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( ifc, instance );

        boolean result = componentDescriptorsByKey.containsKey( key );

        if ( !result && parent != null )
        {
            result = parent.containsComponent( ifc, instance, sessionKey );
        }

        if ( !result )
        {
            ContainerSession session = ContainerSessionManager.instance().get( sessionKey, id );
            result = session.containsComponentDescriptor( ifc, instance );
        }

        return result;
    }

    public boolean containsComponent( String ifc, SessionKey sessionKey )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( ifc );

        boolean result = componentDescriptorsByKey.containsKey( key );

        if ( !result && parent != null )
        {
            result = parent.containsComponent( ifc, sessionKey );
        }

        if ( !result )
        {
            ContainerSession session = getContainerSession( sessionKey );

            ComponentSelector selector = new AdHocComponentSelector( ifc, ComponentDescriptor.NO_INSTANCE_NAME );

            result = session.containsComponentDescriptor( ifc, selector );
        }

        return result;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    private void createComponentDescriptorMap()
    {
        this.componentDescriptorsByKey = new HashMap();

        if ( componentDescriptors != null && !componentDescriptors.isEmpty() )
        {
            for ( Iterator it = componentDescriptors.iterator(); it.hasNext(); )
            {
                ComponentDescriptor descriptor = (ComponentDescriptor) it.next();

                String key = ComponentUtilities.createDescriptorKey( descriptor );

                componentDescriptorsByKey.put( key, descriptor );
            }
        }
        
        this.componentDescriptorsByKey = Collections.unmodifiableMap( componentDescriptorsByKey );
    }

    private void createComponentSelectorMap()
    {
        this.componentSelectorsByInterface = new HashMap();

        if ( componentSelectors != null && !componentSelectors.isEmpty() )
        {
            for ( Iterator it = componentSelectors.iterator(); it.hasNext(); )
            {
                ComponentSelector selector = (ComponentSelector) it.next();

                componentSelectorsByInterface.put( selector.getInterfaceName(), selector );
            }
        }
        
        this.componentSelectorsByInterface = Collections.unmodifiableMap( componentSelectorsByInterface );
    }

    private static class AdHocComponentSelector
        implements ComponentSelector
    {

        private final List instanceNames;

        private final String interfaceName;

        public AdHocComponentSelector( String interfaceName, String instanceName )
        {
            this( interfaceName, Collections.singletonList( instanceName ) );
        }

        public AdHocComponentSelector( String interfaceName, List instanceNames )
        {
            this.interfaceName = interfaceName;

            List instNames = new ArrayList( instanceNames );

            this.instanceNames = Collections.unmodifiableList( instNames );
        }

        public String getInterfaceName()
        {
            return interfaceName;
        }

        public List getSelectedInstancesInOrder()
        {
            return instanceNames;
        }

    }
}
