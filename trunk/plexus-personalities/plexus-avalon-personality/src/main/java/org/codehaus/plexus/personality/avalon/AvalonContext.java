package org.codehaus.plexus.personality.avalon;

import java.io.File;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * A wrapper for the plexus context.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 6, 2004
 */
public class AvalonContext
    implements Context
{
    org.codehaus.plexus.context.Context context;
    
    public AvalonContext( org.codehaus.plexus.context.Context context )
    {
        this.context = context;
        
        try
		{
			context.put( "app.home", new File((String)context.get("plexus.home")) );
		}
		catch (Exception e)
		{
		    throw new RuntimeException( "Couldn't find a home directory.", e );
		}
    }
    
    /**
     * @see org.apache.avalon.framework.context.Context#get(java.lang.Object)
     */
    public Object get(Object key) throws ContextException
    {
        try
        {
            return context.get(key);
        }
        catch (org.codehaus.plexus.context.ContextException e)
        {
            throw new ContextException( e.getMessage(), e.getCause() );
        }
    }

}
