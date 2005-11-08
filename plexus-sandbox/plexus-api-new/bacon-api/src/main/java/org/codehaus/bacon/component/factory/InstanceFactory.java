package org.codehaus.bacon.component.factory;

import java.util.List;

import org.codehaus.bacon.component.ComponentDescriptor;

public interface InstanceFactory
{

    Object instantiate( ComponentDescriptor descriptor, List constructionParameters, ClassLoader componentLoader )
        throws InstantiationException;
    
}
