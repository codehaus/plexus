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

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Simplest implementation of a {@link RecursionInterceptor}, which checks whether
 * the existing interpolation effort is already attempting to resolve an exact
 * expression, but has not finished. This will not catch synonym expressions, as
 * are found in Maven (${project.build.directory}, ${pom.build.directory}, and
 * ${build.directory} are synonyms).
 *
 * @author jdcasey
 * @version $Id$
 */
public class SimpleRecursionInterceptor
    implements RecursionInterceptor
{

    private Stack expressions = new Stack();

    /**
     * {@inheritDoc}
     */
    public void expressionResolutionFinished( String expression )
    {
        expressions.pop();
    }

    /**
     * {@inheritDoc}
     */
    public void expressionResolutionStarted( String expression )
    {
        expressions.push( expression );
    }

    /**
     * Check whether the current expression is already present in the in-process
     * stack.
     */
    public boolean hasRecursiveExpression( String expression )
    {
        return expressions.contains( expression );
    }

    /**
     * When an expression is determined to be a recursive reference, this method
     * returns the sublist of tracked expressions that participate in this cycle.
     * Otherwise, if the expression isn't present in the in-process stack, return
     * {@link Collections#EMPTY_LIST}.
     */
    public List getExpressionCycle( String expression )
    {
        int idx = expressions.indexOf( expression );
        if ( idx < 0 )
        {
            return Collections.EMPTY_LIST;
        }
        else
        {
            return expressions.subList( idx, expressions.size() );
        }
    }

}
