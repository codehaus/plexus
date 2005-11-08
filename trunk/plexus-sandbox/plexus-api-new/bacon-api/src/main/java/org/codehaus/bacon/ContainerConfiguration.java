package org.codehaus.bacon;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ContainerConfiguration
{
    
    Set getComponentDescriptors();
    
    Set getComponentSelectors();
    
    List getClasspathElements();
    
    Map getLanguagePacksByKey();

}
