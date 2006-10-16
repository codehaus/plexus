package org.codehaus.bacon.component.injection;

import java.util.Map;

public interface ComponentInjector
{
    
    void inject( Object instance, Map valuesByCompositionSource, ClassLoader containerLoader )
        throws ComponentInjectionException;

}
