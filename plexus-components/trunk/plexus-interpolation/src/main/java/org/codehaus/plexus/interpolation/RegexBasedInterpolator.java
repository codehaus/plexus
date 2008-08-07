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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expansion of the original RegexBasedInterpolator, found in plexus-utils, this
 * interpolator provides options for setting custom prefix/suffix regex parts,
 * and includes a {@link RecursionInterceptor} parameter in its interpolate(..)
 * call, to allow the detection of cyclical expression references.
 *
 * @version $Id$
 */
public class RegexBasedInterpolator
    implements Interpolator
{

    private String startRegex;

    private String endRegex;

    private List valueSources = new ArrayList();
    
    private List postProcessors = new ArrayList();
    
    private boolean reusePatterns = false;
    
    public static final String DEFAULT_REGEXP = "\\$\\{(.+?)\\}";
    
    /**
     * the key is the regex the value is the Pattern 
     * At the class construction time the Map will contains the default Pattern
     */
    private Map compiledPatterns = new HashMap();
    
    /**
     * Setup a basic interpolator.
     * <br/>
     * <b>NOTE:</b> You will have to call
     * {@link RegexBasedInterpolator#addValueSource(ValueSource)} at least once
     * if you use this constructor!
     */
    public RegexBasedInterpolator()
    {
        compiledPatterns.put( DEFAULT_REGEXP, Pattern.compile( DEFAULT_REGEXP ) );
    }
    
    /**

     * @param reusePatterns already compiled patterns will be reused
     */
    public RegexBasedInterpolator( boolean reusePatterns )
    {
        this();
        this.reusePatterns = reusePatterns;
    }    

    /**
     * Setup an interpolator with no value sources, and the specified regex pattern
     * prefix and suffix in place of the default one.
     * <br/>
     * <b>NOTE:</b> You will have to call
     * {@link RegexBasedInterpolator#addValueSource(ValueSource)} at least once
     * if you use this constructor!
     *
     * @param startRegex start of the regular expression to use
     * @param endRegex end of the regular expression to use
     */
    public RegexBasedInterpolator (String startRegex, String endRegex)
    {
        this();
        this.startRegex = startRegex;
        this.endRegex = endRegex;
    }

    /**
     * Setup a basic interpolator with the specified list of value sources.
     *
     * @param valueSources The list of value sources to use
     */
    public RegexBasedInterpolator( List valueSources )
    {
        this();
        this.valueSources.addAll( valueSources );
    }

    /**
     * Setup an interpolator with the specified value sources, and the specified
     * regex pattern prefix and suffix in place of the default one.
     *
     * @param startRegex start of the regular expression to use
     * @param endRegex end of the regular expression to use
     * @param valueSources The list of value sources to use
     */
    public RegexBasedInterpolator (String startRegex, String endRegex, List valueSources )
    {
        this();
        this.startRegex = startRegex;
        this.endRegex = endRegex;
        this.valueSources.addAll( valueSources );
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

    /**
     * Attempt to resolve all expressions in the given input string, using the
     * given pattern to first trim an optional prefix from each expression. The
     * supplied recursion interceptor will provide protection from expression
     * cycles, ensuring that the input can be resolved or an exception is
     * thrown.
     *
     * @param input The input string to interpolate
     *
     * @param thisPrefixPattern An optional pattern that should be trimmed from
     *                          the start of any expressions found in the input.
     *
     * @param recursionInterceptor Used to protect the interpolation process
     *                             from expression cycles, and throw an
     *                             exception if one is detected.
     */
    public String interpolate( String input,
                               String thisPrefixPattern,
                               RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        if ( recursionInterceptor == null )
        {
            recursionInterceptor = new SimpleRecursionInterceptor();
        }

        if ( thisPrefixPattern != null && thisPrefixPattern.length() == 0 )
        {
            thisPrefixPattern = null;
        }

        int realExprGroup = 2;
        Pattern expressionPattern = null;
        if ( startRegex != null || endRegex != null )
        {
            if ( thisPrefixPattern == null )
            {
                expressionPattern = getPattern( startRegex + endRegex );
                realExprGroup = 1;
            }
            else
            {
                expressionPattern = getPattern( startRegex + thisPrefixPattern + endRegex );
            }

        }
        else if ( thisPrefixPattern != null )
        {
            expressionPattern = getPattern( "\\$\\{(" + thisPrefixPattern + ")?(.+?)\\}" );
        }
        else
        {
            expressionPattern = getPattern( DEFAULT_REGEXP );
            realExprGroup = 1;
        }

        return interpolate( input, recursionInterceptor, expressionPattern, realExprGroup );
    }
    
    private Pattern getPattern( String regExp )
    {
        if ( !reusePatterns )
        {
            return Pattern.compile( regExp );
        }
        // FIXME here we are not really Thread safe        
        if ( compiledPatterns.containsKey( regExp ) )
        {
            return (Pattern) compiledPatterns.get( regExp );
        }

        Pattern pattern = Pattern.compile( regExp );
        compiledPatterns.put( regExp, pattern );
        return pattern;
    }

    /**
     * Entry point for recursive resolution of an expression and all of its
     * nested expressions.
     *
     * @todo Ensure unresolvable expressions don't trigger infinite recursion.
     */
    private String interpolate( String input,
                                RecursionInterceptor recursionInterceptor,
                                Pattern expressionPattern,
                                int realExprGroup )
        throws InterpolationException
    {
        String result = input;

        Matcher matcher = expressionPattern.matcher( result );

        while ( matcher.find() )
        {
            String wholeExpr = matcher.group( 0 );
            String realExpr = matcher.group( realExprGroup );

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

            Object value = null;
            for ( Iterator it = valueSources.iterator(); it.hasNext() && value == null; )
            {
                ValueSource vs = (ValueSource) it.next();

                value = vs.getValue( realExpr );
            }

            if ( value != null )
            {
                value = interpolate( String.valueOf( value ), recursionInterceptor, expressionPattern, realExprGroup );
                
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

    /**
     * See {@link RegexBasedInterpolator#interpolate(String, String, RecursionInterceptor)}.
     * <br/>
     * This method triggers the use of a {@link SimpleRecursionInterceptor}
     * instance for protection against expression cycles.
     *
     * @param input The input string to interpolate
     *
     * @param thisPrefixPattern An optional pattern that should be trimmed from
     *                          the start of any expressions found in the input.
     */
    public String interpolate( String input,
                               String thisPrefixPattern )
        throws InterpolationException
    {
        return interpolate( input, thisPrefixPattern, null );
    }

    /**
     * See {@link RegexBasedInterpolator#interpolate(String, String, RecursionInterceptor)}.
     * <br/>
     * This method triggers the use of a {@link SimpleRecursionInterceptor}
     * instance for protection against expression cycles. It also leaves empty the
     * expression prefix which would otherwise be trimmed from expressions. The
     * result is that any detected expression will be resolved as-is.
     *
     * @param input The input string to interpolate
     */
    public String interpolate( String input )
        throws InterpolationException
    {
        return interpolate( input, null, null );
    }

    /**
     * See {@link RegexBasedInterpolator#interpolate(String, String, RecursionInterceptor)}.
     * <br/>
     * This method leaves empty the expression prefix which would otherwise be
     * trimmed from expressions. The result is that any detected expression will
     * be resolved as-is.
     *
     * @param input The input string to interpolate
     *
     * @param recursionInterceptor Used to protect the interpolation process
     *                             from expression cycles, and throw an
     *                             exception if one is detected.
     */
    public String interpolate( String input,
                               RecursionInterceptor recursionInterceptor )
        throws InterpolationException
    {
        return interpolate( input, null, recursionInterceptor );
    }

    public boolean isReusePatterns()
    {
        return reusePatterns;
    }

    public void setReusePatterns( boolean reusePatterns )
    {
        this.reusePatterns = reusePatterns;
    }

}
