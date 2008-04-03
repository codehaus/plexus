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
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public PrefixAwareRecursionInterceptor( Collection possiblePrefixes, boolean watchUnprefixedExpressions )
    {
        this.possiblePrefixes = possiblePrefixes;
        this.watchUnprefixedExpressions = watchUnprefixedExpressions;
    }

    public PrefixAwareRecursionInterceptor( Collection possiblePrefixes )
    {
        this.possiblePrefixes = possiblePrefixes;
    }

    public PrefixAwareRecursionInterceptor( String startToken, String endToken, Collection possiblePrefixes, boolean watchUnprefixedExpressions )
    {
        this.startToken = startToken;
        this.endToken = endToken;
        this.possiblePrefixes = possiblePrefixes;
        this.watchUnprefixedExpressions = watchUnprefixedExpressions;
    }

    public PrefixAwareRecursionInterceptor( String startToken, String endToken, Collection possiblePrefixes )
    {
        this.startToken = startToken;
        this.endToken = endToken;
        this.possiblePrefixes = possiblePrefixes;
    }

    public boolean hasRecursiveExpression( String value )
    {
        Pattern expressionPattern = Pattern.compile( startToken + "(.+?)" + endToken );
        Matcher matcher = expressionPattern.matcher( value );
        while( matcher.find() )
        {
            String realExpr = ValueSourceUtils.trimPrefix( matcher.group( 1 ), possiblePrefixes, watchUnprefixedExpressions );

            if ( nakedExpressions.contains( realExpr ) )
            {
                return true;
            }
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

}
