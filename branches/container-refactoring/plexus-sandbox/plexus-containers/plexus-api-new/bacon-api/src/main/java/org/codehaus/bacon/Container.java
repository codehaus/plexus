package org.codehaus.bacon;

import java.util.Date;
import java.util.Map;

import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentSelector;
import org.codehaus.bacon.component.DuplicateComponentDescriptorException;
import org.codehaus.bacon.session.SessionKey;

public interface Container
{
    void setParentContainer( Container parent );

    Container getParentContainer();

    void addChildContainer( Container child );

    Container getChildContainer( String childId );

    String getContainerId();
    
    boolean containsComponent( String ifc, String instance, SessionKey sessionKey )
        throws ContainerException;
    
    boolean containsComponent( String ifc, SessionKey sessionKey )
        throws ContainerException;

    boolean containsComponent( ComponentDescriptor descriptor, SessionKey sessionKey )
        throws ContainerException;

    Object lookup( String ifc, String instance, SessionKey sessionKey, Map context )
        throws LookupException, ContainerException;

    Object lookup( String ifc, SessionKey sessionKey, Map context )
        throws LookupException, ContainerException;

    void registerSelector( ComponentSelector selector, SessionKey sessionKey )
        throws ContainerException;

    void registerComponent( ComponentDescriptor descriptor, SessionKey sessionKey )
        throws DuplicateComponentDescriptorException, ContainerException;
    
    Date getCreationDate();
}
