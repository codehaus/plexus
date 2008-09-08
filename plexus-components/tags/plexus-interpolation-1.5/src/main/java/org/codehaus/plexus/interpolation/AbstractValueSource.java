package org.codehaus.plexus.interpolation;

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
