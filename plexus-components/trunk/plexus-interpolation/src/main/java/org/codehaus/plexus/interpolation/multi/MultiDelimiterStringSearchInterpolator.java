package org.codehaus.plexus.interpolation.multi;

import org.codehaus.plexus.interpolation.InterpolationCycleException;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiDelimiterStringSearchInterpolator
    implements Interpolator
{

    private static final int MAX_TRIES = 10;
    
    private Map existingAnswers = new HashMap();

    private List valueSources = new ArrayList();

    private List postProcessors = new ArrayList();

    private boolean cacheAnswers = false;

    private LinkedHashSet delimiters = new LinkedHashSet();

    private String escapeString;

    public MultiDelimiterStringSearchInterpolator()
    {
        delimiters.add( DelimiterSpecification.DEFAULT_SPEC );
    }
    
    public MultiDelimiterStringSearchInterpolator addDelimiterSpec( String delimiterSpec )
    {
        delimiters.add( DelimiterSpecification.parse( delimiterSpec ) );
        return this;
    }
    
    public boolean removeDelimiterSpec( String delimiterSpec )
    {
        return delimiters.remove( DelimiterSpecification.parse( delimiterSpec ) );
    }
    
    public MultiDelimiterStringSearchInterpolator withValueSource( ValueSource vs )
    {
        addValueSource( vs );
        return this;
    }
    
    public MultiDelimiterStringSearchInterpolator withPostProcessor( InterpolationPostProcessor postProcessor )
    {
        addPostProcessor( postProcessor );
        return this;
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
        postProcessors.remove( postProcessor );
    }

    public String interpolate( String input, String thisPrefixPattern )
        throws InterpolationException
    {
        return interpolate( input, new SimpleRecursionInterceptor() );
    }

    public String interpolate( String input, String thisPrefixPattern, RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        return interpolate( input, recursionInterceptor );
    }

    public String interpolate( String input )
        throws InterpolationException
    {
        return interpolate( input, new SimpleRecursionInterceptor() );
    }

    /**
     * Entry point for recursive resolution of an expression and all of its nested expressions.
     * 
     * @todo Ensure unresolvable expressions don't trigger infinite recursion.
     */
    public String interpolate( String input, RecursionInterceptor recursionInterceptor )
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
        if ( input == null )
        {
            // return empty String to prevent NPE too
            return "";
        }
        StringBuffer result = new StringBuffer( input.length() * 2 );

        String lastResult = input;
        int tries = 0;
        do
        {
            tries++;
            if ( result.length() > 0 )
            {
                lastResult = result.toString();
                result.setLength( 0 );
            }
            
            int startIdx = -1;
            int endIdx = -1;
            
            DelimiterSpecification selectedSpec = null;
            while( ( selectedSpec = select( input, endIdx ) ) != null )
            {
                String startExpr = selectedSpec.getBegin();
                String endExpr = selectedSpec.getEnd();
                
                startIdx = selectedSpec.getNextStartIndex();
                result.append( input.substring( endIdx + 1, startIdx ) );

                endIdx = input.indexOf( endExpr, startIdx + 1 );
                if ( endIdx < 0 )
                {
                    break;
                }

                String wholeExpr = input.substring( startIdx, endIdx + endExpr.length() );
                String realExpr = wholeExpr.substring( startExpr.length(), wholeExpr.length() - endExpr.length() );

                if ( startIdx >= 0 && escapeString != null && escapeString.length() > 0 )
                {
                    int startEscapeIdx = startIdx == 0 ? 0 : startIdx - escapeString.length();
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

                if ( endIdx > -1 )
                {
                    endIdx += endExpr.length() - 1;
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
        }
        while( !lastResult.equals( result.toString() ) && tries < MAX_TRIES );

        return result.toString();
    }

    private DelimiterSpecification select( String input, int lastEndIdx )
    {
        DelimiterSpecification selected = null;
        
        for ( Iterator it = delimiters.iterator(); it.hasNext(); )
        {
            DelimiterSpecification spec = (DelimiterSpecification) it.next();
            spec.clearNextStart();
            
            if ( selected == null )
            {
                int idx = input.indexOf( spec.getBegin(), lastEndIdx + 1 );
                if ( idx > -1 )
                {
                    spec.setNextStartIndex( idx );
                    selected = spec;
                }
            }
        }
        
        return selected;
    }

    /**
     * Return any feedback messages and errors that were generated - but suppressed - during the interpolation process.
     * Since unresolvable expressions will be left in the source string as-is, this feedback is optional, and will only
     * be useful for debugging interpolation problems.
     * 
     * @return a {@link List} that may be interspersed with {@link String} and {@link Throwable} instances.
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
    
    public MultiDelimiterStringSearchInterpolator setDelimiterSpecs( LinkedHashSet specs )
    {
        delimiters.clear();
        for ( Iterator it = specs.iterator(); it.hasNext(); )
        {
            String spec = (String) it.next();
            delimiters.add( DelimiterSpecification.parse( spec ) );
        }
        
        return this;
    }

}
