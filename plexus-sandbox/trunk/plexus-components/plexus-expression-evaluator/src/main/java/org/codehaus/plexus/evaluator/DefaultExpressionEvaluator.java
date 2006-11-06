package org.codehaus.plexus.evaluator;

import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DefaultExpressionEvaluator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.evaluator.ExpressionEvaluator" 
 *                   role-hint="default"
 *                   instantiation-strategy="per-lookup"
 */
public class DefaultExpressionEvaluator
    implements ExpressionEvaluator
{
    private List expressionSources;

    public DefaultExpressionEvaluator()
    {
        expressionSources = new ArrayList();
    }

    public void addExpressionSource( ExpressionSource source )
    {
        expressionSources.add( source );
    }

    public String expand( String str )
        throws EvaluatorException
    {
        return recursiveExpand( str, new ArrayList() );
    }

    private String recursiveExpand( String str, List seenExpressions )
        throws EvaluatorException
    {
        if ( StringUtils.isEmpty( str ) )
        {
            // Empty string. Fail fast.
            return str;
        }

        if ( str.indexOf( "${" ) < 0 )
        {
            // Contains no potential expressions.  Fail fast.
            return str;
        }

        if ( this.expressionSources.isEmpty() )
        {
            throw new EvaluatorException( "Unable to expand expressions with empty ExpressionSource list." );
        }

        Pattern pat = Pattern.compile( "(?:[^$]|^)(\\$\\{[^}]*\\})" );
        Matcher mat = pat.matcher( str );
        int offset = 0;
        String expression;
        String value;
        StringBuffer expanded = new StringBuffer();

        while ( mat.find( offset ) )
        {
            expression = mat.group( 1 );

            if ( seenExpressions.contains( expression ) )
            {
                throw new EvaluatorException( "A recursive cycle has been detected with expression " + expression + "." );
            }

            seenExpressions.add( expression );

            expanded.append( str.substring( offset, mat.start( 1 ) ) );
            value = findValue( expression );
            if ( value != null )
            {
                String resolvedValue = recursiveExpand( value, seenExpressions );
                expanded.append( resolvedValue );
            }
            else
            {
                expanded.append( expression );
            }
            offset = mat.end( 1 );
        }

        expanded.append( str.substring( offset ) );

        if ( expanded.indexOf( "$$" ) >= 0 )
        {
            // Special case for escaped content.
            return expanded.toString().replaceAll( "\\$\\$", "\\$" );
        }
        else
        {
            // return expanded
            return expanded.toString();
        }
    }

    private String findValue( String expression )
    {
        String newExpression = expression.trim();
        if ( newExpression.startsWith( "${" ) && newExpression.endsWith( "}" ) )
        {
            newExpression = newExpression.substring( 2, newExpression.length() - 1 );
        }

        if ( StringUtils.isEmpty( newExpression ) )
        {
            return null;
        }

        String value = null;
        Iterator it = this.expressionSources.iterator();
        while ( it.hasNext() )
        {
            ExpressionSource source = (ExpressionSource) it.next();
            value = source.getExpressionValue( newExpression );
            if ( value != null )
            {
                return value;
            }
        }
        return null;
    }

    public List getExpressionSourceList()
    {
        return this.expressionSources;
    }

    public boolean removeExpressionSource( ExpressionSource source )
    {
        return this.expressionSources.remove( source );
    }
}
