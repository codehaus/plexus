package org.codehaus.bacon.component.manager;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentException;
import org.codehaus.bacon.session.SessionKey;

public interface InstanceManager
{
    
    Object getComponent( ComponentDescriptor descriptor, Container container, SessionKey sessionKey, ClassLoader containerLoader )
        throws ComponentException;

}
