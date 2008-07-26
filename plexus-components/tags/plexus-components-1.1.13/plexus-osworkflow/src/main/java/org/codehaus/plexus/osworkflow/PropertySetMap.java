package org.codehaus.plexus.osworkflow;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import com.opensymphony.module.propertyset.PropertySet;

import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PropertySetMap
    implements Map
{
    private PropertySet propertySet;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public PropertySetMap( PropertySet propertySet )
    {
        this.propertySet = propertySet;
    }

    // ----------------------------------------------------------------------
    // Map Implementation
    // ----------------------------------------------------------------------

    public int size()
    {
        return propertySet.getKeys().size();
    }

    public void clear()
    {
        for ( Iterator i = propertySet.getKeys().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();

            propertySet.remove( key );
        }
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean containsKey( Object key )
    {
        return propertySet.getKeys().contains( key );
    }

    public boolean containsValue( Object value )
    {
        // TODO: Implement
        throw new RuntimeException( "Not supported" );
    }

    public Collection values()
    {
        // TODO: Implement
        throw new RuntimeException( "Not supported" );
    }

    public void putAll( Map t )
    {
        // Assert that all the keys are String objects before adding them
        for ( Iterator i = t.keySet().iterator(); i.hasNext(); )
        {
            keyToString( i.next() );
        }

        for ( Iterator it = t.entrySet().iterator(); it.hasNext(); )
        {
            Entry entry = (Entry) it.next();

            propertySet.setObject( entry.getKey().toString(), entry.getValue() );
        }
    }

    public Set entrySet()
    {
        // TODO: Implement
        throw new RuntimeException( "Not supported" );
    }

    public Set keySet()
    {
        return new HashSet( propertySet.getKeys() );
    }

    public Object get( Object key )
    {
        return propertySet.getObject( keyToString( key ) );
    }

    public Object remove( Object key )
    {
        String s = keyToString( key );

        Object o = propertySet.getObject( s );

        propertySet.remove( s );

        return o;
    }

    public Object put( Object key, Object value )
    {
        String s = keyToString( key );

        Object o = propertySet.getObject( s );

        propertySet.setObject( s, value );

        return o;
    }

    // ----------------------------------------------------------------------
    // Object Overrides
    // ----------------------------------------------------------------------

    public String toString()
    {
        return propertySet.toString();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String keyToString( Object key )
    {
        if ( key instanceof String )
        {
            return key.toString();
        }

        throw new RuntimeException( "All the keys has to be String objects." );
    }
}
