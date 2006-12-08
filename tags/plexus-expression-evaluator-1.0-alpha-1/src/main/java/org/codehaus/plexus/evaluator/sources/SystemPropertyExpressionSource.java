package org.codehaus.plexus.evaluator.sources;

import org.codehaus.plexus.evaluator.ExpressionSource;

/**
 * SystemPropertyExpressionSource 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.evaluator.ExpressionSource"
 *                   role-hint="sysprops"
 */
public class SystemPropertyExpressionSource
    implements ExpressionSource
{
    public String getExpressionValue( String expression )
    {
        try
        {
            return System.getProperty( expression );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
