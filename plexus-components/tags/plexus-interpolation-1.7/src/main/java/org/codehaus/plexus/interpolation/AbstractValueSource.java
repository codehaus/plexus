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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueSource
    implements ValueSource
{
    
    private final List feedback;
    
    protected AbstractValueSource( boolean usesFeedback )
    {
        if ( usesFeedback )
        {
            feedback = new ArrayList();
        }
        else
        {
            feedback = null;
        }
    }

    public void clearFeedback()
    {
        if ( feedback != null )
        {
            feedback.clear();
        }
    }

    public List getFeedback()
    {
        return feedback;
    }

    protected void addFeedback( String message )
    {
        feedback.add( message );
    }
    
    protected void addFeedback( String message, Throwable cause )
    {
        feedback.add( message );
        feedback.add( cause );
    }
}
