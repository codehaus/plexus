package org.codehaus.bacon.component.factory;

import org.codehaus.bacon.component.ComponentDescriptor;

public interface InstanceFactory
{

    Object instantiate( ComponentDescriptor descriptor, ClassLoader componentLoader )
        throws InstantiationException;
    
}
