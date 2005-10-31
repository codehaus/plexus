package org.codehaus.bacon.component;

public interface ComponentAttribute
    extends CompositionSource
{
    String getExpression();
    
    String getType();
}
