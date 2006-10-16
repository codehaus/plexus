package org.codehaus.bacon.session;

import java.util.Date;

import org.codehaus.bacon.ContainerException;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentSelector;
import org.codehaus.bacon.component.DuplicateComponentDescriptorException;
import org.codehaus.bacon.component.DuplicateComponentInstanceException;

public interface ContainerSession
{
    
    String getContainerId();
    
    SessionKey getSessionKey();
    
    ComponentDescriptor getComponentDescriptor( String interfaceName, ComponentSelector selector );
    
    ComponentSelector getComponentSelector( String ifc );
    
    boolean containsComponentDescriptor( String ifc, String instance )
        throws ContainerException;

    boolean containsComponentDescriptor( String ifc, ComponentSelector selector )
        throws ContainerException;
    
    boolean containsComponentDescriptor( ComponentDescriptor descriptor )
        throws ContainerException;

    void registerComponentDescriptor( ComponentDescriptor descriptor )
        throws DuplicateComponentDescriptorException;
    
    void registerComponentSelector( ComponentSelector selector );
    
    boolean isOpen();
    
    Date getLastAccessed();
    
    Date getCreationDate();
    
    void registerComponentInstance( ComponentDescriptor descriptor, Object instance )
        throws DuplicateComponentInstanceException;
    
    boolean containsComponentInstance( ComponentDescriptor descriptor )
        throws ContainerException;
    
    Object getComponentInstance( ComponentDescriptor descriptor )
        throws ContainerException;

}
