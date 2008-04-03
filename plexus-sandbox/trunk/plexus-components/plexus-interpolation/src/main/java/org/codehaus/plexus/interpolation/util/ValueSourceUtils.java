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

import java.util.Collection;
import java.util.Iterator;

public final class ValueSourceUtils
{

    private ValueSourceUtils()
    {
    }

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
