package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;

/**
 * If the expression matches, simply return the response object.
 * @since 1.12
 */
public class SingleResponseValueSource
    implements ValueSource
{
    
    private final String expression;
    private final Object response;

    public SingleResponseValueSource( String expression, Object response )
    {
        this.expression = expression;
        this.response = response;
    }

    public void clearFeedback()
    {
    }

    public List getFeedback()
    {
        return Collections.EMPTY_LIST;
    }

    public Object getValue( String expression )
    {
        if ( this.expression.equals( expression ) )
        {
            return response;
        }
        
        return null;
    }

}
