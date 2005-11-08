package org.codehaus.bacon.component;

public interface ComponentReference
    extends InjectionDescriptor
{
    String CARDINALITY_1_1 = "1:1";
    
    String CARDINALITY_1_M = "1:M";
    
    String CARDINALITY_1_MAP = "1:MAP";
    
    String getInterfaceName();
    
    String getCardinality();
}
