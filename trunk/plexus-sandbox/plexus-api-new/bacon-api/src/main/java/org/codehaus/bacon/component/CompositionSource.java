package org.codehaus.bacon.component;

public interface CompositionSource
{
    
    boolean isRequired();
    
    String getId();
    
    String getInjectionStrategy();
    
    String getInjectionTarget();
    
}
