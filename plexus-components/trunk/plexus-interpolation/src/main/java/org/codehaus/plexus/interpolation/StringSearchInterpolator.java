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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringSearchInterpolator
    implements Interpolator
{

    private Map existingAnswers = new HashMap();

    private List valueSources = new ArrayList();
    
    private List postProcessors = new ArrayList();
   
    private boolean cacheAnswers = false;
    
    public static final String DEFAULT_START_EXPR = "${";
    
    public static final String DEFAULT_END_EXPR = "}";
    
    private String startExpr;
    
    private String endExpr;
    
    private String escapeString;
    
    public StringSearchInterpolator()
    {
        this.startExpr = DEFAULT_START_EXPR;
        this.endExpr = DEFAULT_END_EXPR;
    }

    public StringSearchInterpolator( String startExpr, String endExpr )
    {
        this.startExpr = startExpr;
        this.endExpr = endExpr;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    public void addValueSource( ValueSource valueSource )
    {
        valueSources.add( valueSource );
    }

    /**
     * {@inheritDoc}
     */
    public void removeValuesSource( ValueSource valueSource )
    {
        valueSources.remove( valueSource );
    }

    /**
     * {@inheritDoc}
     */
    public void addPostProcessor( InterpolationPostProcessor postProcessor )
    {
        postProcessors.add( postProcessor );
    }

    /**
     * {@inheritDoc}
     */
    public void removePostProcessor( InterpolationPostProcessor postProcessor )
    {
        postProcessors.remove( postProcessor  );
    }
    
    public String interpolate( String input, String thisPrefixPattern )
        throws InterpolationException
    {
        throw new UnsupportedOperationException( "Regular expressions are not supported in this Interpolator implementation." );
    }

    public String interpolate( String input, String thisPrefixPattern, RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        throw new UnsupportedOperationException( "Regular expressions are not supported in this Interpolator implementation." );
    }

    public String interpolate( String input )
        throws InterpolationException
    {
        return interpolate( input, new SimpleRecursionInterceptor() );
    }

    /**
     * Entry point for recursive resolution of an expression and all of its
     * nested expressions.
     *
     * @todo Ensure unresolvable expressions don't trigger infinite recursion.
     */
    public String interpolate( String input,
                                RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        try
        {
            return interpolate( input, recursionInterceptor, new HashSet() );
        }
        finally
        {
            if ( !cacheAnswers )
            {
                existingAnswers.clear();
            }
        }
    }
    
    private String interpolate( String input, RecursionInterceptor recursionInterceptor, Set unresolvable )
        throws InterpolationException
    {
        StringBuffer result = new StringBuffer( input.length() * 2 );
        
        int startIdx = -1;
        int endIdx = -1;
        while ( ( startIdx = input.indexOf( startExpr, endIdx + 1 ) ) > -1 )
        {
            result.append( input.substring( endIdx + 1, startIdx ) );
            
            endIdx = input.indexOf( endExpr, startIdx + 1 );
            if ( endIdx < 0 )
            {
                break;
            }
            
            String wholeExpr = input.substring( startIdx, endIdx + 1 );
            String realExpr = wholeExpr.substring( 2, wholeExpr.length() - 1 );

            if ( startIdx >= 0 && escapeString != null && escapeString.length() > 0 )
            {
                int startEscapeIdx = startIdx - escapeString.length();
                if ( startEscapeIdx >= 0 )
                {
                    String escape = input.substring( startEscapeIdx, startIdx );
                    if ( escape != null && escapeString.equals( escape ) )
                    {
                        result.append( wholeExpr );
                        result.replace( startEscapeIdx, startEscapeIdx + escapeString.length(), "" );
                        continue;
                    }
                }
            }            
            
            boolean resolved = false;
            if ( !unresolvable.contains( wholeExpr ) )
            {
                if ( realExpr.startsWith( "." ) )
                {
                    realExpr = realExpr.substring( 1 );
                }

                if ( recursionInterceptor.hasRecursiveExpression( realExpr ) )
                {
                    throw new InterpolationCycleException( recursionInterceptor, realExpr, wholeExpr );
                }

                recursionInterceptor.expressionResolutionStarted( realExpr );

                Object value = existingAnswers.get( realExpr );
                Object bestAnswer = null;
                for ( Iterator it = valueSources.iterator(); it.hasNext() && value == null; )
                {
                    ValueSource vs = (ValueSource) it.next();

                    value = vs.getValue( realExpr );
                    
                    if ( value != null && value.toString().indexOf( wholeExpr ) > -1 )
                    {
                        bestAnswer = value;
                        value = null;
                    }
                }
                
                // this is the simplest recursion check to catch exact recursion 
                // (non synonym), and avoid the extra effort of more string 
                // searching.
                if ( value == null && bestAnswer != null )
                {
                    throw new InterpolationCycleException( recursionInterceptor, realExpr, wholeExpr );
                }

                if ( value != null )
                {
                    value = interpolate( String.valueOf( value ), recursionInterceptor, unresolvable );
                    
                    if ( postProcessors != null && !postProcessors.isEmpty() )
                    {
                        for ( Iterator it = postProcessors.iterator(); it.hasNext(); )
                        {
                            InterpolationPostProcessor postProcessor = (InterpolationPostProcessor) it.next();
                            Object newVal = postProcessor.execute( realExpr, value );
                            if ( newVal != null )
                            {
                                value = newVal;
                                break;
                            }
                        }
                    }

                    // could use:
                    // result = matcher.replaceFirst( stringValue );
                    // but this could result in multiple lookups of stringValue, and replaceAll is not correct behaviour
                    result.append( String.valueOf( value ) );
                    resolved = true;
                }
                else
                {
                    unresolvable.add( wholeExpr );
                }
                
                recursionInterceptor.expressionResolutionFinished( realExpr );
            }

            if ( !resolved )
            {
                result.append( wholeExpr );
            }
        }
        
        if ( endIdx == -1 && startIdx > -1 )
        {
            result.append( input.substring( startIdx, input.length() ) );
        }
        else if ( endIdx < input.length() )
        {
            result.append( input.substring( endIdx + 1, input.length() ) );
        }
        
        return result.toString();
    }

    /**
     * Return any feedback messages and errors that were generated - but
     * suppressed - during the interpolation process. Since unresolvable
     * expressions will be left in the source string as-is, this feedback is
     * optional, and will only be useful for debugging interpolation problems.
     *
     * @return a {@link List} that may be interspersed with {@link String} and
     * {@link Throwable} instances.
     */
    public List getFeedback()
    {
        List messages = new ArrayList();
        for ( Iterator it = valueSources.iterator(); it.hasNext(); )
        {
            ValueSource vs = (ValueSource) it.next();
            List feedback = vs.getFeedback();
            if ( feedback != null && !feedback.isEmpty() )
            {
                messages.addAll( feedback );
            }
        }

        return messages;
    }

    /**
     * Clear the feedback messages from previous interpolate(..) calls.
     */
    public void clearFeedback()
    {
        for ( Iterator it = valueSources.iterator(); it.hasNext(); )
        {
            ValueSource vs = (ValueSource) it.next();
            vs.clearFeedback();
        }
    }

    public boolean isCacheAnswers()
    {
        return cacheAnswers;
    }

    public void setCacheAnswers( boolean cacheAnswers )
    {
        this.cacheAnswers = cacheAnswers;
    }
    
    public void clearAnswers()
    {
        existingAnswers.clear();
    }

    public String getEscapeString()
    {
        return escapeString;
    }

    public void setEscapeString( String escapeString )
    {
        this.escapeString = escapeString;
    }

}
