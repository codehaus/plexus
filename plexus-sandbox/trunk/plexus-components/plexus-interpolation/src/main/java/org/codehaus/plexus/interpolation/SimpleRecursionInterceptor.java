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

import java.util.Iterator;
import java.util.Stack;

public class SimpleRecursionInterceptor
    implements RecursionInterceptor
{

    private Stack expressions = new Stack();

    public void expressionResolutionFinished( String expression )
    {
        expressions.pop();
    }

    public void expressionResolutionStarted( String expression )
    {
        expressions.push( expression );
    }

    public boolean hasRecursiveExpression( String value )
    {
        for ( Iterator it = expressions.iterator(); it.hasNext(); )
        {
            String expr = (String) it.next();
            if ( expr.equals( value ) || expr.indexOf( value ) > -1 )
            {
                return true;
            }
        }

        return false;
    }

}
