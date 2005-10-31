package org.codehaus.bacon.component.basic.manager;

import org.codehaus.bacon.Container;
import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.ComponentException;
import org.codehaus.bacon.component.manager.InstanceManager;
import org.codehaus.bacon.session.SessionKey;

public abstract class AbstractInstanceManager
    implements InstanceManager
{

    public Object create( ComponentDescriptor descriptor, Container container, SessionKey sessionKey,
                               ClassLoader containerLoader )
        throws ComponentException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
