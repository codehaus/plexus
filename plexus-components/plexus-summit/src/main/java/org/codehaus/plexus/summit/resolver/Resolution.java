package org.codehaus.plexus.summit.resolver;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.summit.view.View;

/**
 * <p>A <code>Resolution</code> is the result of target view processing that occurs
 * within a {@link org.codehaus.plexus.summit.resolver.Resolver}. </p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class Resolution
{
    private Map resolution;

    public Resolution()
    {
        resolution = new HashMap();
    }

    public void put( Object key, Object value )
    {
        resolution.put( key, value );
    }

    public Object get( Object key )
    {
        return resolution.get( key );
    }

    public View getView( String key )
    {
        return (View) get( key );
    }
}
