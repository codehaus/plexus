package org.codehaus.plexus.registry;

/*
 * Copyright 2007 The Codehaus Foundation.
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

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of the Plexus Configuration interface backed by a registry.
 */
public class RegistryPlexusConfiguration
    implements PlexusConfiguration
{
    /**
     * The backing registry.
     */
    private final Registry registry;

    /**
     * The name associated with this registry entry.
     */
    private final String name;

    /**
     * List of keys (with a depth of 1) in the registry.
     */
    private List keys;

    /**
     * The parent registry.
     */
    private final Registry parent;

    public RegistryPlexusConfiguration( Registry registry )
    {
        this.registry = registry;

        this.name = "configuration";

        this.parent = null;
    }

    public RegistryPlexusConfiguration( String name, Registry registry, Registry parent )
    {
        this.name = name;

        this.registry = registry;

        this.parent = parent;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        throw new UnsupportedOperationException( "getValue() should not be called" );
    }

    public String getValue( String defaultValue )
    {
        return defaultValue;
    }

    public String[] getAttributeNames()
    {
        return null;
    }

    public String getAttribute( String name )
        throws PlexusConfigurationException
    {
        return null;
    }

    public String getAttribute( String name, String defaultValue )
    {
        return defaultValue;
    }

    public PlexusConfiguration getChild( String name )
    {
        Registry registry = this.registry.getSubset( name );

        return new RegistryPlexusConfiguration( name, registry, this.registry );
    }

    public PlexusConfiguration getChild( int i )
    {
        return getChild( (String) getKeys().get( i ) );
    }

    private List getKeys()
    {
        if ( keys == null )
        {
            if ( registry == null )
            {
                keys = Collections.EMPTY_LIST;
            }
            else
            {
                keys = new ArrayList( registry.getKeys() );
            }
        }

        return keys;
    }

    public PlexusConfiguration getChild( String name, boolean createChild )
    {
        if ( createChild )
        {
            throw new UnsupportedOperationException( "Cannot add to the registry via plexus configuration" );
        }
        else
        {
            return getChild( name );
        }
    }

    public PlexusConfiguration[] getChildren()
    {
        List keys = getKeys();
        PlexusConfiguration[] configurations = new PlexusConfiguration[keys.size()];
        int count = 0;
        for ( Iterator i = keys.iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            configurations[count] = getChild( key );
            count++;
        }
        return configurations;
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        throw new UnsupportedOperationException( "getChildren(name) is not supported" );
    }

    public void addChild( PlexusConfiguration plexusConfiguration )
    {
        throw new UnsupportedOperationException( "Cannot add to the registry via plexus configuration" );
    }

    public int getChildCount()
    {
        return getKeys().size();
    }

    public Registry getRegistry()
    {
        return parent;
    }
}
