package org.codehaus.plexus.evaluator;

/*
 * Copyright 2001-2006 The Codehaus.
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
 * ExpressionEvaluator 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface ExpressionEvaluator
{
    String ROLE = ExpressionEvaluator.class.getName();

    /**
     * Add a source for expression resolution.
     * 
     * @param source the source to add.
     */
    void addExpressionSource( ExpressionSource source );

    /**
     * Evaluate a string, and expand expressions as needed.
     *
     * @param expression the expression
     * @return the value of the expression
     */
    String expand( String str )
        throws EvaluatorException;

    /**
     * Get the List of expression sources.
     * 
     * @return the list of expression sources.
     */
    List getExpressionSourceList();

    /**
     * Remove a specific expression source.
     * 
     * @param source the source to remove.
     * @return true if expression source was removed.
     */
    boolean removeExpressionSource( ExpressionSource source );
}
