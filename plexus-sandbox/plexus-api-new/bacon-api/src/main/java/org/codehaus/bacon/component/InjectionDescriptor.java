package org.codehaus.bacon.component;

public interface InjectionDescriptor
{
    
    boolean isRequired();
    
    String getId();
    
    String getInjectionStrategy();
    
    String getInjectionTarget();
    
}
