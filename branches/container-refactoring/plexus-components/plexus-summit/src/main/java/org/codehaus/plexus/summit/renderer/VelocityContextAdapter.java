package org.codehaus.plexus.summit.renderer;

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
