package org.codehaus.plexus.werkflow.ognl;

import org.codehaus.werkflow.simple.ExpressionFactory;
import org.codehaus.werkflow.spi.Expression;

/**
 * Creates OGNL expressions.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class OgnlExpressionFactory
    implements ExpressionFactory
{
    /**
     * @see org.codehaus.werkflow.simple.ExpressionFactory#newExpression(java.lang.String)
     */
    public Expression newExpression(String expr) throws Exception
    {
        return new OgnlExpression(expr);
    }

}
