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
 * @version $Id$
 */
public class ObjectBasedValueSource
    implements FeedbackEnabledValueSource
{

    private final Object root;

    private List feedback = new ArrayList();

    public ObjectBasedValueSource( Object root )
    {
        this.root = root;
    }

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

    public List getFeedback()
    {
        return feedback;
    }

    public void clearFeedback()
    {
        feedback.clear();
    }

}
