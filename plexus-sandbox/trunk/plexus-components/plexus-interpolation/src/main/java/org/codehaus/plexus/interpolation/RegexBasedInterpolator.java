package org.codehaus.plexus.interpolation;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import org.codehaus.plexus.interpolation.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @version $Id$
 */
public class RegexBasedInterpolator
    implements Interpolator
{

    private String startRegex;

    private String endRegex;

    private List valueSources;

    public RegexBasedInterpolator()
    {
        valueSources = new ArrayList();
    }

    /**
     * @param startRegex start of the regular expression to use
     * @param endRegex end of the regular expression to use
     * @since 1.5
     */
    public RegexBasedInterpolator (String startRegex, String endRegex)
    {
        this();
        this.startRegex = startRegex;
        this.endRegex = endRegex;
    }

    public RegexBasedInterpolator( List valueSources )
    {
        this.valueSources = new ArrayList( valueSources );
    }

    public void addValueSource( ValueSource valueSource )
    {
        valueSources.add( valueSource );
    }

    public void removeValuesSource( ValueSource valueSource )
    {
        valueSources.remove( valueSource );
    }

    public String interpolate( String input,
                               String thisPrefixPattern,
                               RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        if ( recursionInterceptor == null )
        {
            recursionInterceptor = new SimpleRecursionInterceptor();
        }

        if ( thisPrefixPattern.length() == 0 )
        {
            thisPrefixPattern = null;
        }

        String result = input;

        int realExprGroup = 2;
        Pattern expressionPattern = null;
        if (startRegex != null || endRegex != null)
        {
            expressionPattern = Pattern.compile( startRegex + thisPrefixPattern + endRegex );
        }
        else if ( thisPrefixPattern != null )
        {
            expressionPattern = Pattern.compile( "\\$\\{(" + thisPrefixPattern + ")?(.+?)\\}" );
        }
        else
        {
            expressionPattern = Pattern.compile( "\\$\\{(.+?)\\}" );
            realExprGroup = 1;
        }

        Matcher matcher = expressionPattern.matcher( result );

        while ( matcher.find() )
        {
            String wholeExpr = matcher.group( 0 );
            String realExpr = matcher.group( realExprGroup );

            if ( realExpr.startsWith( "." ) )
            {
                realExpr = realExpr.substring( 1 );
            }

            recursionInterceptor.expressionResolutionStarted( realExpr );

            Object value = null;
            for ( Iterator it = valueSources.iterator(); it.hasNext() && value == null; )
            {
                ValueSource vs = (ValueSource) it.next();

                value = vs.getValue( realExpr );
            }

            if ( value != null && recursionInterceptor.hasRecursiveExpression( value.toString() ) )
            {
                throw new InterpolationException( "Expression: \'" + wholeExpr + "\' references itself.", wholeExpr );
            }

            if ( value != null )
            {
                result = StringUtils.replace( result, wholeExpr, String.valueOf( value ) );
                // could use:
                // result = matcher.replaceFirst( stringValue );
                // but this could result in multiple lookups of stringValue, and replaceAll is not correct behaviour
                matcher.reset( result );
            }

            recursionInterceptor.expressionResolutionFinished( realExpr );
        }

        return result;
    }

    public List getFeedback()
    {
        List messages = new ArrayList();
        for ( Iterator it = valueSources.iterator(); it.hasNext(); )
        {
            ValueSource vs = (ValueSource) it.next();
            if ( vs instanceof FeedbackEnabledValueSource )
            {
                List feedback = ((FeedbackEnabledValueSource) vs).getFeedback();
                if ( feedback != null && !feedback.isEmpty() )
                {
                    messages.addAll( feedback );
                }
            }
        }

        return messages;
    }

    public void clearFeedback()
    {
        for ( Iterator it = valueSources.iterator(); it.hasNext(); )
        {
            ValueSource vs = (ValueSource) it.next();
            if ( vs instanceof FeedbackEnabledValueSource )
            {
                ((FeedbackEnabledValueSource) vs).clearFeedback();
            }
        }
    }

    public String interpolate( String input,
                               String thisPrefixPattern )
        throws InterpolationException
    {
        return interpolate( input, thisPrefixPattern, null );
    }

    public String interpolate( String input )
        throws InterpolationException
    {
        return interpolate( input, null, null );
    }

    public String interpolate( String input,
                               RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        return interpolate( input, null, recursionInterceptor );
    }

}
