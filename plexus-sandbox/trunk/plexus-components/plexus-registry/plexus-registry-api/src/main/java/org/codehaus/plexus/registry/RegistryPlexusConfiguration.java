package org.codehaus.plexus.registry;

/*
 * Copyright 2007, Brett Porter
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
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
     * The value associated with the entry.
     */
    private final String value;

    /**
     * List of children (with a depth of 1) in the registry.
     */
    private PlexusConfiguration[] children;

    private static final PlexusConfiguration[] PLEXUS_CONFIGURATION = new PlexusConfiguration[0];

    public RegistryPlexusConfiguration( Registry registry )
    {
        this.registry = registry;

        this.name = "configuration";

        this.value = null;
    }

    public RegistryPlexusConfiguration( Registry registry, String name )
    {
        this.registry = registry;

        this.name = name;

        this.value = null;
    }

    public RegistryPlexusConfiguration( String name, String value )
    {
        this.registry = null;

        this.name = name;

        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
        throws PlexusConfigurationException
    {
        return value;
    }

    public String getValue( String defaultValue )
    {
        return value != null ? value : defaultValue;
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
        PlexusConfiguration config = null;
        if ( registry != null )
        {
            String value = registry.getString( name );
            if ( value != null )
            {
                config = new RegistryPlexusConfiguration( name, value );
            }
            else
            {
                Registry registry = this.registry.getSubset( name );
                if ( registry != null )
                {
                    config = new RegistryPlexusConfiguration( registry, name );
                }
            }
        }
        return config;
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
        // TODO: this is a hack for Maps. We rely on nothing else using this - we should really have a different converter

        Properties properties = registry.getProperties( "" );

        int count = 0;
        PlexusConfiguration[] children = new RegistryPlexusConfiguration[properties.size()];
        for ( Iterator i = properties.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            children[count] = new RegistryPlexusConfiguration( key, properties.getProperty( key ) );
            count++;
        }
        return children;
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        // TODO: this is a hack for Properties. Should really change the converter when using this configurator.
        if ( "property".equals( name ) )
        {
            Properties properties = registry.getProperties( "" );

            int count = 0;
            PlexusConfiguration[] children = new XmlPlexusConfiguration[properties.size()];
            for ( Iterator i = properties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                PlexusConfiguration c = new XmlPlexusConfiguration( "property" );
                c.addChild( createCongifuration( "name", key ) );
                c.addChild( createCongifuration( "value", properties.getProperty( key ) ) );

                children[count] = c;
                count++;
            }
            return children;
        }
        else
        {
            PlexusConfiguration[] children = getChildren();
            List subset = new ArrayList();
            for ( int i = 0; i < children.length; i++ )
            {
                if ( children[i].getName().equals( name ) )
                {
                    subset.add( children[i] );
                }
            }
            return (PlexusConfiguration[]) subset.toArray( PLEXUS_CONFIGURATION );
        }
    }

    private static XmlPlexusConfiguration createCongifuration( String name, String value )
    {
        XmlPlexusConfiguration plexusConfiguration = new XmlPlexusConfiguration( name );
        plexusConfiguration.setValue( value );
        return plexusConfiguration;
    }

    public void addChild( PlexusConfiguration plexusConfiguration )
    {
        throw new UnsupportedOperationException( "Cannot add to the registry via plexus configuration" );
    }

    public int getChildCount()
    {
        return getKeys().size();
    }
}
