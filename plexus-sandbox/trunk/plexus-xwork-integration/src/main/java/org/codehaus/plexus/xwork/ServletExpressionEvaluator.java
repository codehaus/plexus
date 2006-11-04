package org.codehaus.plexus.xwork;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Looks for expression values from the servlet context, and from system properties.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @todo possibly general purpose somewhere else in plexus, and it should be part of a delegating chain to a pure sysprop evaluator
 */
public class ServletExpressionEvaluator
    implements ExpressionEvaluator
{
    private final ServletContext servletContext;

    public ServletExpressionEvaluator( ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    private String stripTokens( String expression )
    {
        String newExpression = expression;
        if ( newExpression.startsWith( "${" ) && newExpression.indexOf( "}" ) == newExpression.length() - 1 )
        {
            newExpression = newExpression.substring( 2, newExpression.length() - 1 );
        }
        return newExpression;
    }

    public Object evaluate( String expression )
        throws ExpressionEvaluationException
    {
        // Heavily borrowed from Maven PluginParameterExpressionEvaluator
        // Should be a common class somewhere

        String value = null;

        if ( expression != null )
        {
            String newExpression = stripTokens( expression );
            if ( newExpression.equals( expression ) )
            {
                int index = expression.indexOf( "${" );
                if ( index >= 0 )
                {
                    int lastIndex = expression.indexOf( "}", index );
                    if ( lastIndex >= 0 )
                    {
                        String retVal = expression.substring( 0, index );

                        if ( index > 0 && expression.charAt( index - 1 ) == '$' )
                        {
                            retVal += expression.substring( index + 1, lastIndex + 1 );
                        }
                        else
                        {
                            retVal += evaluate( expression.substring( index, lastIndex + 1 ) );
                        }

                        retVal += evaluate( expression.substring( lastIndex + 1 ) );
                        return retVal;
                    }
                }

                // Was not an expression
                if ( newExpression.indexOf( "$$" ) > -1 )
                {
                    return newExpression.replaceAll( "\\$\\$", "\\$" );
                }
                else
                {
                    return newExpression;
                }
            }

            value = servletContext.getInitParameter( newExpression );
            if ( value == null )
            {
                value = System.getProperty( newExpression );
            }

            if ( value != null )
            {
                int exprStartDelimiter = value.indexOf( "${" );

                if ( exprStartDelimiter >= 0 )
                {
                    if ( exprStartDelimiter > 0 )
                    {
                        value = value.substring( 0, exprStartDelimiter ) +
                            evaluate( value.substring( exprStartDelimiter ) );
                    }
                    else
                    {
                        value = (String) evaluate( value.substring( exprStartDelimiter ) );
                    }
                }
            }
        }

        return value;
    }

    public File alignToBaseDirectory( File file )
    {
        return file;
    }
}
