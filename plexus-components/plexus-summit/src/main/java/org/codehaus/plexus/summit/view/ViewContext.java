package org.codehaus.plexus.summit.view;

import java.util.Map;

/**
 * <p>The collection of values that a native tool will use to render
 * against.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ViewContext
{
    public void put( String key, Object value );

    public void putAll( Map map );

    public Object get( String key );

    public Object remove( String key );
}
