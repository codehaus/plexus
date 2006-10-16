package org.codehaus.plexus.osworkflow;

/*
 * Copyright 2006 The Apache Software Foundation.
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
 *
 */

import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ActionMap
    implements Map
{
    private Map context;

    private Map transientVars;

    public ActionMap( Map context, Map transientVars )
    {
        this.context = context;

        this.transientVars = transientVars;
    }

    // ----------------------------------------------------------------------
    // Map Implementation
    // ----------------------------------------------------------------------

    public int size()
    {
        return values().size();
    }

    public void clear()
    {
        throw new RuntimeException( "Operation not supported." );
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean containsKey( Object key )
    {
        if ( context.containsKey( key ) )
        {
            return true;
        }

        return transientVars.containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        if ( context.containsValue( value ) )
        {
            return true;
        }

        return transientVars.containsValue( value );
    }

    public Collection values()
    {
        Set set = new HashSet();

        set.addAll( transientVars.values() );
        set.addAll( context.values() );

        return set;
    }

    public void putAll( Map t )
    {
        context.putAll( t );
    }

    public Set entrySet()
    {
        Set set = new HashSet();

        set.addAll( transientVars.values() );
        set.addAll( context.values() );

        return set;
    }

    public Set keySet()
    {
        Set set = new HashSet();

        set.addAll( transientVars.keySet() );
        set.addAll( context.keySet() );

        return set;
    }

    public Object get( Object key )
    {
        Object value = context.get( key );

        if ( value != null )
        {
            return value;
        }

        return transientVars.get( key );
    }

    public Object remove( Object key )
    {
        return context.remove( key );
    }

    public Object put( Object key, Object value )
    {
        return context.put( key, value );
    }
}
