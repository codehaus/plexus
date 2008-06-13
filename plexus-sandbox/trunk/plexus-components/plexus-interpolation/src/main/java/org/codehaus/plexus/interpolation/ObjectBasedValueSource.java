package org.codehaus.plexus.interpolation;

/*
 * Copyright 2007 The Codehaus Foundation.
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
import org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps an object, providing reflective access to the object graph of which the
 * supplied object is the root. Expressions like 'child.name' will translate into
 * 'rootObject.getChild().getName()' for non-boolean properties, and
 * 'rootObject.getChild().isName()' for boolean properties.
 */
public class ObjectBasedValueSource
    implements FeedbackEnabledValueSource
{

    private final Object root;

    private List feedback = new ArrayList();

    /**
     * Construct a new value source, using the supplied object as the root from
     * which to start, and using expressions split at the dot ('.') to navigate
     * the object graph beneath this root.
     */
    public ObjectBasedValueSource( Object root )
    {
        this.root = root;
    }

    /**
     * Split the expression into parts, tokenized on the dot ('.') character. Then,
     * starting at the root object contained in this value source, apply each part
     * to the object graph below this root, using either 'getXXX()' or 'isXXX()'
     * accessor types to resolve the value for each successive expression part.
     * Finally, return the result of the last expression part's resolution.
     * <br/>
     * <b>NOTE:</b> The object-graph nagivation actually takes place via the
     * {@link ReflectionValueExtractor} class.
     */
    public Object getValue( String expression )
    {
        try
        {
            return ReflectionValueExtractor.evaluate( expression, root, false );
        }
        catch ( Exception e )
        {
            feedback.add( "Failed to extract \'" + expression + "\' from: " + root );
            feedback.add( e );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List getFeedback()
    {
        return feedback;
    }

    /**
     * {@inheritDoc}
     */
    public void clearFeedback()
    {
        feedback.clear();
    }

}
