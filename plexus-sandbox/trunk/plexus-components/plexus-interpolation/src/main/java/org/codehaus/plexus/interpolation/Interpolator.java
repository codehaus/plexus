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

import java.util.List;

/**
 * Interpolator interface. Based on existing RegexBasedInterpolator interface.
 *
 * @author cstamas
 */
public interface Interpolator
{

    /**
     * Add a new {@link ValueSource} to the stack used to resolve expressions
     * in this interpolator instance.
     */
    void addValueSource( ValueSource valueSource );

    /**
     * Remove the specified {@link ValueSource} from the stack used to resolve
     * expressions in this interpolator instance.
     */
    void removeValuesSource( ValueSource valueSource );

    /**
     * See {@link Interpolator#interpolate(String, String, RecursionInterceptor)}.
     * <br/>
     * This method triggers the use of a {@link SimpleRecursionInterceptor}
     * instance for protection against expression cycles.
     *
     * @param input The input string to interpolate
     *
     * @param thisPrefixPattern An optional pattern that should be trimmed from
     *                          the start of any expressions found in the input.
     */
    String interpolate( String input,
                        String thisPrefixPattern )
        throws InterpolationException;

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
    String interpolate( String input,
                        String thisPrefixPattern,
                        RecursionInterceptor recursionInterceptor )
        throws InterpolationException;

    /**
     * See {@link Interpolator#interpolate(String, String, RecursionInterceptor)}.
     * <br/>
     * This method triggers the use of a {@link SimpleRecursionInterceptor}
     * instance for protection against expression cycles. It also leaves empty the
     * expression prefix which would otherwise be trimmed from expressions. The
     * result is that any detected expression will be resolved as-is.
     *
     * @param input The input string to interpolate
     */
    String interpolate( String input )
        throws InterpolationException;

    /**
     * See {@link Interpolator#interpolate(String, String, RecursionInterceptor)}.
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
    String interpolate( String input,
                        RecursionInterceptor recursionInterceptor )
        throws InterpolationException;

    /**
     * Return any feedback messages and errors that were generated - but
     * suppressed - during the interpolation process. Since unresolvable
     * expressions will be left in the source string as-is, this feedback is
     * optional, and will only be useful for debugging interpolation problems.
     *
     * @return a {@link List} that may be interspersed with {@link String} and
     * {@link Throwable} instances.
     */
    List getFeedback();

    /**
     * Clear the feedback messages from previous interpolate(..) calls.
     */
    void clearFeedback();
}
