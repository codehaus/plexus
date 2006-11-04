package org.codehaus.bacon.base.session;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.ContainerException;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentSelector;
import org.codehaus.bacon.component.ComponentUtilities;
import org.codehaus.bacon.component.DuplicateComponentDescriptorException;
import org.codehaus.bacon.component.DuplicateComponentInstanceException;
import org.codehaus.bacon.session.ContainerSession;
import org.codehaus.bacon.session.SessionKey;

public class BasicContainerSession
    implements ContainerSession
{

    private final Container container;
    private final SessionKey sessionKey;
    private final Date created;
    
    private Map selectors = new HashMap();
    private Map components = new HashMap();
    private Map instances = new HashMap();

    public BasicContainerSession( SessionKey sessionKey, Container container )
    {
        this.sessionKey = sessionKey;
        this.container = container;
        this.created = new Date();
    }

    public String getContainerId()
    {
        return container.getContainerId();
    }

    public SessionKey getSessionKey()
    {
        return sessionKey;
    }

    public ComponentDescriptor getComponentDescriptor( String interfaceName, ComponentSelector selector )
    {
        ComponentDescriptor descriptor = null;
        
        List instanceNames = selector.getSelectedInstancesInOrder();
        if ( instanceNames == null || instanceNames.isEmpty() )
        {
            instanceNames = Collections.singletonList( ComponentDescriptor.NO_INSTANCE_NAME );
        }
        
        for ( Iterator it = instanceNames.iterator(); it.hasNext(); )
        {
            String instanceName = (String) it.next();
            
            String key = ComponentUtilities.createDescriptorKey( interfaceName, instanceName );
            
            descriptor = (ComponentDescriptor) components.get( key );
            
            if ( descriptor != null )
            {
                break;
            }
        }
        
        return descriptor;
    }

    public ComponentSelector getComponentSelector( String ifc )
    {
        return (ComponentSelector) selectors.get( ifc );
    }

    public boolean containsComponentDescriptor( String ifc, String instance )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( ifc, instance );
        
        return components.containsKey( key );
    }

    public boolean containsComponentDescriptor( String ifc, ComponentSelector selector )
        throws ContainerException
    {
        boolean result = false;
        
        List instanceNames = selector.getSelectedInstancesInOrder();
        for ( Iterator it = instanceNames.iterator(); it.hasNext(); )
        {
            String instance = (String) it.next();
            
            String key = ComponentUtilities.createDescriptorKey( ifc, instance );
            
            result = components.containsKey( key );
            
            if ( result )
            {
                break;
            }
        }
        
        return result;
    }

    public boolean containsComponentDescriptor( ComponentDescriptor descriptor )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );
        
        return components.containsKey( key );
    }

    public void registerComponentDescriptor( ComponentDescriptor descriptor )
        throws DuplicateComponentDescriptorException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );
        
        if ( components .containsKey( key ) )
        {
            throw new DuplicateComponentDescriptorException( "Cannot re-register component for: " + descriptor );
        }
        else
        {
            components.put( key, descriptor );
        }
    }

    public void registerComponentSelector( ComponentSelector selector )
    {
        selectors.put( selector.getInterfaceName(), selector );
    }

    public boolean isOpen()
    {
        return sessionKey.isOpen();
    }

    public Date getLastAccessed()
    {
        return sessionKey.getLastAccessed();
    }

    public Date getCreationDate()
    {
        return created;
    }

    public void registerComponentInstance( ComponentDescriptor descriptor, Object instance )
        throws DuplicateComponentInstanceException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );
        
        if ( instances.containsKey( key ) )
        {
            throw new DuplicateComponentInstanceException( "Cannot register new instance of: " + descriptor + ". An instance already exists in the session." );
        }
        else
        {
            instances.put( key, instance );
        }
    }

    public boolean containsComponentInstance( ComponentDescriptor descriptor )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );
        
        return instances.containsKey( key );
    }

    public Object getComponentInstance( ComponentDescriptor descriptor )
        throws ContainerException
    {
        String key = ComponentUtilities.createDescriptorKey( descriptor );
        
        return instances.get( key );
    }

}
