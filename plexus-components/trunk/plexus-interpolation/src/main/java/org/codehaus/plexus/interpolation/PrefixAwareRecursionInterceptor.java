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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * {@link RecursionInterceptor} implementation that provides support for expressions
 * with multiple synonyms, such as project.build.directory == pom.build.directory ==
 * build.directory in Maven's POM.
 *
 * @author jdcasey
 */
public class PrefixAwareRecursionInterceptor
    implements RecursionInterceptor
{

    public static final String DEFAULT_START_TOKEN = "\\$\\{";

    public static final String DEFAULT_END_TOKEN = "\\}";

    private Stack nakedExpressions = new Stack();

    private final Collection possiblePrefixes;

    private String endToken = DEFAULT_END_TOKEN;

    private String startToken = DEFAULT_START_TOKEN;

    private boolean watchUnprefixedExpressions = true;

    /**
     * Use the specified expression prefixes to detect synonyms, and specify whether
     * unprefixed expressions can be considered synonyms.
     *
     * @param possiblePrefixes The collection of expression prefixes supported
     * @param watchUnprefixedExpressions Whether to consider unprefixed expressions as synonyms
     */
    public PrefixAwareRecursionInterceptor( Collection possiblePrefixes, boolean watchUnprefixedExpressions )
    {
        this.possiblePrefixes = possiblePrefixes;
        this.watchUnprefixedExpressions = watchUnprefixedExpressions;
    }

    /**
     * Use the specified expression prefixes to detect synonyms. Consider
     * unprefixed expressions synonyms as well.
     *
     * @param possiblePrefixes The collection of expression prefixes supported
     */
    public PrefixAwareRecursionInterceptor( Collection possiblePrefixes )
    {
        this.possiblePrefixes = possiblePrefixes;
    }

    public boolean hasRecursiveExpression( String expression )
    {
        String realExpr = ValueSourceUtils.trimPrefix( expression, possiblePrefixes, watchUnprefixedExpressions );
        if ( realExpr != null )
        {
            return nakedExpressions.contains( realExpr );
        }

        return false;
    }

    public void expressionResolutionFinished( String expression )
    {
        nakedExpressions.pop();
    }

    public void expressionResolutionStarted( String expression )
    {
        String realExpr = ValueSourceUtils.trimPrefix( expression, possiblePrefixes, watchUnprefixedExpressions );
        nakedExpressions.push( realExpr );
    }

    /**
     * When an expression is determined to be a recursive reference, this method
     * returns the sublist of tracked expressions that participate in this cycle.
     * Otherwise, if the expression isn't present in the in-process stack, return
     * {@link Collections#EMPTY_LIST}. Also, if the expression doesn't have a matched
     * prefix from this interceptor's list, and unprefixed expressions aren't allowed
     * then return {@link Collections#EMPTY_LIST}.
     */
    public List getExpressionCycle( String expression )
    {
        String expr = ValueSourceUtils.trimPrefix( expression, possiblePrefixes, watchUnprefixedExpressions );

        if ( expr == null )
        {
            return Collections.EMPTY_LIST;
        }

        int idx = nakedExpressions.indexOf( expression );
        if ( idx < 0 )
        {
            return Collections.EMPTY_LIST;
        }
        else
        {
            return nakedExpressions.subList( idx, nakedExpressions.size() );
        }
    }

}
