package org.codehaus.plexus.interpolation;

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
    
    private boolean reusePatterns = false;
    
    private boolean cacheAnswers = false;
    
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
        return interpolate( input, recursionInterceptor, new HashSet() );
    }
    
    private String interpolate( String input, RecursionInterceptor recursionInterceptor, Set unresolvable )
        throws InterpolationException
    {
        StringBuffer result = new StringBuffer( input.length() * 2 );
        
        int startIdx = -1;
        int endIdx = -1;
        while ( ( startIdx = input.indexOf( "${", endIdx + 1 ) ) > -1 )
        {
            result.append( input.substring( endIdx + 1, startIdx ) );
            
            endIdx = input.indexOf( "}", startIdx + 1 );
            if ( endIdx < 0 )
            {
                break;
            }
            
            String wholeExpr = input.substring( startIdx, endIdx + 1 );
            String realExpr = wholeExpr.substring( 2, wholeExpr.length() - 1 );
            
            boolean resolved = false;
            if ( !unresolvable.contains( wholeExpr ) )
            {
                if ( realExpr.startsWith( "." ) )
                {
                    realExpr = realExpr.substring( 1 );
                }

                if ( recursionInterceptor.hasRecursiveExpression( realExpr ) )
                {
                    throw new InterpolationException( "Detected the following recursive expression cycle: "
                                                                      + recursionInterceptor.getExpressionCycle( realExpr ),
                                                      wholeExpr );
                }

                recursionInterceptor.expressionResolutionStarted( realExpr );

                Object value = existingAnswers.get( realExpr );
                for ( Iterator it = valueSources.iterator(); it.hasNext() && value == null; )
                {
                    ValueSource vs = (ValueSource) it.next();

                    value = vs.getValue( realExpr );
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

    /**
     * Clear the feedback messages from previous interpolate(..) calls.
     */
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

    public boolean isReusePatterns()
    {
        return reusePatterns;
    }

    public void setReusePatterns( boolean reusePatterns )
    {
        this.reusePatterns = reusePatterns;
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

}
