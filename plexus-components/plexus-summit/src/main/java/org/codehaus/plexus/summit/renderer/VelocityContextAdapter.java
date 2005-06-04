package org.codehaus.plexus.summit.renderer;

import org.apache.velocity.context.AbstractContext;
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * <p>We use the Adapter pattern to create a wrapper around a
 * <code>ViewContext</code> so that Velocity can use it as a
 * Velocity <code>Context</code> to render the view.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class VelocityContextAdapter
    extends AbstractContext
{
    /**
     * <p>View Context instance that we are wrapping.</p>
     */
    private ViewContext context;

    /**
     * Constructor
     */
    public VelocityContextAdapter( ViewContext context )
    {
        this.context = context;
    }

    /**
     * @see AbstractContext#internalGet
     */
    public Object internalGet( String key )
    {
        return context.get( key );
    }

    /**
     * @see AbstractContext#internalPut
     */
    public Object internalPut( String key, Object value )
    {
        context.put( key, value );
        return null;
    }

    /**
     * @see AbstractContext#internalContainsKey
     */
    public boolean internalContainsKey( Object key )
    {
        return false;
        //return context.containsKey( key );
    }

    /**
     * @see AbstractContext#internalGetKeys
     */
    public Object[] internalGetKeys()
    {
        return null;
        //return context.getKeys();
    }

    /**
     * @see AbstractContext#internalRemove
     */
    public Object internalRemove( Object key )
    {
        return null;
        //return context.remove( key );
    }
}
