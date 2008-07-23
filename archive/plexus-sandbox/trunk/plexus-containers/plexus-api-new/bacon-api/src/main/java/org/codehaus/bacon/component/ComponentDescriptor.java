package org.codehaus.bacon.component;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ComponentDescriptor
{
    String NO_INSTANCE_NAME = "_anonymous_";
    
    String PER_LOOKUP_INSTANTIATION_STRATEGY = "per-lookup";
    
    String SESSION_SINGLETON_INSTANTIATION_STRATEGY = "session-singleton";
    
    String TRUE_SINGLETON_INSTANTIATION_STRATEGY = "true-singleton";
    
    String getInterfaceName();
    
    String getInstanceName();
    
    String getImplementation();
    
    Set getComponentReferences();
    
    Set getComponentAttributes();
    
    String getInstantiationStrategy();
    
    String getImplementationLanguage();
    
    List getConstructionRequirements();

    Map getComponentReferencesById();

    Map getComponentAttributesById();
}
