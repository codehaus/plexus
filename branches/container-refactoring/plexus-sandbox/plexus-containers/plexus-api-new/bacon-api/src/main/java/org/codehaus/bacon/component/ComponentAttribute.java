package org.codehaus.bacon.component;

public interface ComponentAttribute
    extends InjectionDescriptor
{
    String getExpression();
    
    String getType();
}
