package org.codehaus.plexus.interpolation;

import java.util.List;

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
 * Logs expressions before resolution is attempted, and clears them
 * after resolution is complete (or, fails all strategies). In between,
 * if the value of an expression contains more expressions, RecursionInterceptor
 * implementations ensure that those expressions don't reference an expression
 * which is in the process of being resolved. If that happens, the expression
 * references are cyclical, and would othewise result in an infinite loop.
 */
public interface RecursionInterceptor
{

    /**
     * Log the intention to start resolving the given expression. This signals
     * the interceptor to start tracking that expression to make sure it doesn't
     * come up again until after it has been resolved (or, fails to resolve).
     *
     * @param expression The expression to be resolved.
     */
    void expressionResolutionStarted( String expression );

    /**
     * Signal to the interceptor that the all efforts to resolve the given
     * expression have completed - whether successfully or not is irrelevant -
     * and that the expression should not be tracked for recursion any longer.
     *
     * @param expression The expression to stop tracking.
     */
    void expressionResolutionFinished( String expression );

    /**
     * Check whether the given value contains an expression that is currently
     * being tracked by this interceptor. If so, that expression is still in
     * the process of being resolved, and this constitutes an expression cycle.
     *
     * @param value The value to check for expression cycles.
     * @return True if the value contains tracked expressions; false otherwise.
     */
    boolean hasRecursiveExpression( String value );

    /**
     * @return The list of expressions that participate in the cycle caused by
     * the given expression.
     */
    List getExpressionCycle( String expression );

}
