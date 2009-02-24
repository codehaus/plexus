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
 * Used to allow full recursion of value interpolation before applying some rule
 * to the result.
 * @version $Id$
 */
public interface InterpolationPostProcessor
{
    
    /**
     * Given the starting expression and the fully-recursively-interpolated value,
     * perform some post-processing step and return the resulting [possibly different]
     * value, or null if no change was made..
     * 
     * @param expression the original expression
     * @param value the original value after fully recursive interpolation of expression
     * @return the processed value if a change was made; else return null
     */
    Object execute( String expression, Object value );

}
