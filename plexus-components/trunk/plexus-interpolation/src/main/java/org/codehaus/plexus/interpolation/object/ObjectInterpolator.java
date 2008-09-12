package org.codehaus.plexus.interpolation.object;

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

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;

/**
 * Traverses an object graph and uses an {@link Interpolator} instance to resolve any String values in the
 * graph. 
 * 
 * @author jdcasey
 */
public interface ObjectInterpolator
{
    
    /**
     * Traverse the object graph from the given starting point and interpolate 
     * any Strings found in that graph using the given {@link Interpolator}.
     * 
     * @param target The starting point of the object graph to traverse
     * @param interpolator The {@link Interpolator} used to resolve any Strings encountered during traversal.
     */
    void interpolate( Object target, Interpolator interpolator )
        throws InterpolationException;
    
    /**
     * Traverse the object graph from the given starting point and interpolate 
     * any Strings found in that graph using the given {@link Interpolator}.
     * 
     * @param target The starting point of the object graph to traverse
     * @param interpolator The {@link Interpolator} used to resolve any Strings encountered during traversal.
     * @param recursionInterceptor The {@link RecursionInterceptor} used to detect cyclical expressions in the graph
     */
    void interpolate( Object target, Interpolator interpolator, RecursionInterceptor recursionInterceptor )
        throws InterpolationException;

    /**
     * Returns true if the last interpolation execution generated warnings.
     */
    boolean hasWarnings();
    
    /**
     * Retrieve the {@link List} of warnings ({@link ObjectInterpolationWarning}
     * instances) generated during the last interpolation execution.
     */
    List getWarnings();

}
