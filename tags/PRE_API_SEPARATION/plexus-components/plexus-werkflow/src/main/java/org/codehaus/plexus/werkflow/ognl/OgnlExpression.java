package org.codehaus.plexus.werkflow.ognl;

import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.codehaus.werkflow.Context;
import org.codehaus.werkflow.spi.Expression;

/**
 * OgnlExpression
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class OgnlExpression
    implements Expression
{

    private String expression;

    public OgnlExpression(String expression)
    {
        this.expression = expression;
    }

    public String getExpression()
    {
        return expression;
    }

    /**
     * @see org.codehaus.werkflow.spi.Expression#evaluate(org.codehaus.werkflow.Context)
     */
    public boolean evaluate(Context c)
    {
        Map context = new HashMap();
        context.put("context", c);
        try
        {
            Boolean b = (Boolean) Ognl.getValue(expression, context,
                    (Object) null);
            return b.booleanValue();
        }
        catch (OgnlException e)
        {
            throw new RuntimeException("Couldn't evaluate expression: " + expression, e);
        }
    }
}
