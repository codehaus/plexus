package org.codehaus.bacon.component.basic;

import org.codehaus.bacon.component.ComponentAttribute;

public class BasicComponentAttribute
    extends BasicCompositionSource
    implements ComponentAttribute
{

    private String expression;

    private String type;

    public String getExpression()
    {
        return expression;
    }
    
    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

}
