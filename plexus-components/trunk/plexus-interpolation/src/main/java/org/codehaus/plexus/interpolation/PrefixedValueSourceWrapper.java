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

import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

import java.util.Collections;
import java.util.List;

/**
 * {@link ValueSource} implementation which simply wraps another value source,
 * and trims any of a set of possible expression prefixes before delegating the
 * modified expression to be resolved by the real value source.
 *
 * @author jdcasey
 * @version $Id$
 */
public class PrefixedValueSourceWrapper
    implements FeedbackEnabledValueSource, QueryEnabledValueSource
{

    private final ValueSource valueSource;

    private final List possiblePrefixes;

    private boolean allowUnprefixedExpressions;

    private String lastExpression;

    /**
     * Wrap the given value source, but first trim the given prefix from any
     * expressions before they are passed along for resolution. If an expression
     * doesn't start with the given prefix, do not resolve it.
     *
     * @param valueSource The {@link ValueSource} to wrap.
     * @param prefix The expression prefix to trim.
     */
    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       String prefix )
    {
        this.valueSource = valueSource;
        possiblePrefixes = Collections.singletonList( prefix );
    }

    /**
     * Wrap the given value source, but first trim the given prefix from any
     * expressions before they are passed along for resolution. If an expression
     * doesn't start with the given prefix and the allowUnprefixedExpressions flag
     * is set to true, simply pass the expression through to the nested value source
     * unchanged. If this flag is false, only allow resolution of those expressions
     * that start with the specified prefix.
     *
     * @param valueSource The {@link ValueSource} to wrap.
     * @param prefix The expression prefix to trim.
     * @param allowUnprefixedExpressions Flag telling the wrapper whether to
     * continue resolving expressions that don't start with the prefix it tracks.
     */
    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       String prefix,
                                       boolean allowUnprefixedExpressions )
    {
        this.valueSource = valueSource;
        possiblePrefixes = Collections.singletonList( prefix );
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }

    /**
     * Wrap the given value source, but first trim one of the given prefixes from any
     * expressions before they are passed along for resolution. If an expression
     * doesn't start with one of the given prefixes, do not resolve it.
     *
     * @param valueSource The {@link ValueSource} to wrap.
     * @param possiblePrefixes The List of expression prefixes to trim.
     */
    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       List possiblePrefixes )
    {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
    }

    /**
     * Wrap the given value source, but first trim one of the given prefixes from any
     * expressions before they are passed along for resolution. If an expression
     * doesn't start with the given prefix and the allowUnprefixedExpressions flag
     * is set to true, simply pass the expression through to the nested value source
     * unchanged. If this flag is false, only allow resolution of those expressions
     * that start with the specified prefix.
     *
     * @param valueSource The {@link ValueSource} to wrap.
     * @param possiblePrefixes The List of expression prefixes to trim.
     * @param allowUnprefixedExpressions Flag telling the wrapper whether to
     * continue resolving expressions that don't start with one of the prefixes it tracks.
     */
    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       List possiblePrefixes,
                                       boolean allowUnprefixedExpressions )
    {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }

    /**
     * Uses {@link ValueSourceUtils#trimPrefix(String, java.util.Collection, boolean)} to
     * get the trimmed expression. If this expression is null (because the original
     * expression was null, or because the expression is unprefixed and unprefixed
     * expressions are not allowed here), then return null; otherwise, return the
     * nested {@link ValueSource#getValue(String)} result.
     */
    public Object getValue( String expression )
    {
        lastExpression = ValueSourceUtils.trimPrefix( expression, possiblePrefixes, allowUnprefixedExpressions );

        if ( lastExpression == null )
        {
            return null;
        }

        return valueSource.getValue( lastExpression );
    }

    /**
     * If the nested {@link ValueSource} implements {@link FeedbackEnabledValueSource},
     * then return that source's feedback list. Otherwise, return {@link Collections#EMPTY_LIST}.
     */
    public List getFeedback()
    {
        return ( valueSource instanceof FeedbackEnabledValueSource )
                        ? ( (FeedbackEnabledValueSource) valueSource ).getFeedback()
                        : Collections.EMPTY_LIST;
    }

    /**
     * If the nested {@link ValueSource} implements {@link QueryEnabledValueSource},
     * then return that source's last expression. Otherwise, return the last expression
     * that was processed by the wrapper itself.
     */
    public String getLastExpression()
    {
        return ( valueSource instanceof QueryEnabledValueSource )
                        ? ( (QueryEnabledValueSource) valueSource ).getLastExpression()
                        : lastExpression;
    }

    /**
     * If the nested {@link ValueSource} implements {@link FeedbackEnabledValueSource},
     * then clear that source's feedback list.
     */
    public void clearFeedback()
    {
        if ( valueSource instanceof FeedbackEnabledValueSource )
        {
            ((FeedbackEnabledValueSource) valueSource).clearFeedback();
        }
    }

}
