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

/**
 * {@link ValueSource} abstract implementation that wraps another value source.
 * When an expression is resolved, this wrapped source is first used to retrieve
 * the expression's actual value; then, the last expression processed by this
 * source is retrieved, and the two are passed into the abstract method
 * {@link AbstractFunctionValueSourceWrapper#executeFunction(String, Object)}
 * together. The result of this is returned as the resolved value for the second
 * expression.
 * <br/>
 * This allows the first expression to be a function name that modifies the
 * value of the second expression, which is resolved from the wrapped value
 * source.
 * @version $Id$
 */
public abstract class AbstractFunctionValueSourceWrapper
    implements ValueSource
{

    private final ValueSource valueSource;

    /**
     * Construct a new function value source instance, using the supplied {@link ValueSource}
     * to retrieve the input values for the function(s) this class implements.
     *
     * @param valueSource The value source to wrap
     */
    protected AbstractFunctionValueSourceWrapper( ValueSource valueSource )
    {
        this.valueSource = valueSource;
    }

    /**
     * <ol>
     *   <li>Resolve the current expression using the embedded {@link ValueSource}</li>
     *   <li>Retrieve the last expression processed by this value source</li>
     *   <li>Pass the last expression (which should be the function name), along
     *       with the value for the current expression, into the
     *       executeFunction(..) method</li>
     *   <li>Return the result of the executeFunction(..) as the resolved value
     *       for the current expression.</li>
     * </ol>
     */
    public Object getValue( String expression )
    {
        Object value = valueSource.getValue( expression );

        String expr = expression;

        if ( valueSource instanceof QueryEnabledValueSource )
        {
            expr = ((QueryEnabledValueSource) valueSource).getLastExpression();
        }

        return executeFunction( expr, value );
    }

    /**
     * Retrieve the embedded value source.
     */
    protected ValueSource getValueSource()
    {
        return valueSource;
    }

    /**
     * Execute the function referenced in the last-processed expression using the
     * value resolved from the current expression (using the embedded {@link ValueSource}).
     *
     * @param expression The last expression to be processed by this value source.
     * @param value The value for the current expression, resolved by the embedded {@link ValueSource}
     * @return The result of modifying the current expression's value using the function named by the last expression.
     */
    protected abstract Object executeFunction( String expression, Object value );

}
