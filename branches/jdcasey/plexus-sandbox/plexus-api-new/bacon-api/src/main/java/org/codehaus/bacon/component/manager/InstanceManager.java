package org.codehaus.bacon.component.manager;

import java.util.Map;

import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.factory.InstantiationException;
import org.codehaus.bacon.session.SessionKey;

public interface InstanceManager
{
    
    Object getInstance( ComponentDescriptor descriptor, SessionKey sessionKey, Map context, boolean nativeToContainer )
        throws InstantiationException;

}
