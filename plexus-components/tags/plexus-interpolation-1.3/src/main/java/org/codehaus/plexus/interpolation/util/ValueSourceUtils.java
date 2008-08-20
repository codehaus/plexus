package org.codehaus.plexus.interpolation.util;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import org.codehaus.plexus.interpolation.ValueSource;

import java.util.Collection;
import java.util.Iterator;

/**
 * Utility methods shared by multiple {@link ValueSource} implementations.
 *
 * @author jdcasey
 * @version $Id$
 */
public final class ValueSourceUtils
{

    private ValueSourceUtils()
    {
    }

    /**
     * If the expression starts with one of the provided prefixes, trim that prefix
     * and return the remaining expression. If it doesn't start with a provided
     * prefix, and the allowUnprefixedExpressions flag is true, then return the
     * expression unchanged; if the flag is false, return null. Finally, if the
     * original expression is null, return null without attempting to process it.
     *
     * @param expression The expression to trim
     * @param possiblePrefixes The list of possible expression prefixes to trim
     * @param allowUnprefixedExpressions Whether to return the expression if it
     * doesn't start with one of the prefixes. If true, simply return the
     * original expression; if false, return null.
     *
     * @return The trimmed expression, or null. See the behavior of
     * allowUnprefixedExpressions in this method for more detail.
     */
    public static String trimPrefix( String expression, Collection possiblePrefixes, boolean allowUnprefixedExpressions )
    {
        if ( expression == null )
        {
            return null;
        }

        String realExpr = null;
        for ( Iterator it = possiblePrefixes.iterator(); it.hasNext(); )
        {
            String prefix = (String) it.next();
            if ( expression.startsWith( prefix ) )
            {
                realExpr = expression.substring( prefix.length() );
                if ( realExpr.startsWith( "." ) )
                {
                    realExpr = realExpr.substring( 1 );
                }
                break;
            }
        }

        if ( realExpr == null && allowUnprefixedExpressions )
        {
            realExpr = expression;
        }

        return realExpr;
    }

}
