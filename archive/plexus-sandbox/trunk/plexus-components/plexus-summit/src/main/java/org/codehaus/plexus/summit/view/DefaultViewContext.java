package org.codehaus.plexus.summit.view;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Default <code>ViewContext</code> implementation.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultViewContext
    implements ViewContext
{
    /**
     * Collection of values to be rendered against.
     */
    private HashMap context;

    public DefaultViewContext()
    {
        context = new HashMap();
    }

    public void putAll( Map map )
    {
        context.putAll( map );
    }

    public void put( String key, Object value )
    {
        context.put( key, value );
    }

    public Object get( String key )
    {
        return context.get( key );
    }

    /**
     * @see org.codehaus.plexus.summit.view.ViewContext#remove(java.lang.String)
     */
    public Object remove( String key )
    {
        return context.remove( key );
    }
}
