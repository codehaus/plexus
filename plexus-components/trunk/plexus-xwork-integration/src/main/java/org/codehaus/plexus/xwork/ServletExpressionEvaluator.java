package org.codehaus.plexus.xwork;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Looks for expression values from the servlet context, and from system properties.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @todo possibly general purpose somewhere else in plexus, and it should be part of a delegating chain to a pure sysprop evaluator
 * @deprecated
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
