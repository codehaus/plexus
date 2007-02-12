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

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.EventSource;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of the registry component using
 * <a href="http://jakarta.apache.org/commons/configuration">Commons Configuration</a>. The use of Commons Configuration
 * enables a variety of sources to be used, including XML files, properties, JNDI, JDBC, etc.
 * <p/>
 * The component can be configured using the {@link #properties} configuration item, the content of which should take
 * the format of an input to the Commons Configuration
 * <a href="http://jakarta.apache.org/commons/configuration/howto_configurationbuilder.html">configuration
 * builder</a>.
 *
 * @plexus.component role-hint="commons-configuration"
 */
public class CommonsConfigurationRegistry
    extends AbstractLogEnabled
    implements Initializable, Registry
{
    /**
     * The combined configuration instance that houses the registry.
     */
    private Configuration configuration;

    /**
     * The configuration properties for the registry. This should take the format of an input to the Commons
     * Configuration
     * <a href="http://jakarta.apache.org/commons/configuration/howto_configurationbuilder.html">configuration
     * builder</a>.
     *
     * @plexus.configuration
     */
    private PlexusConfiguration properties;

    public CommonsConfigurationRegistry()
    {
        // default constructor for plexus
    }

    private CommonsConfigurationRegistry( Configuration configuration )
    {
        if ( configuration == null )
        {
            throw new NullPointerException( "configuration can not be null" );
        }

        this.configuration = configuration;
    }

    public String dump()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "Configuration Dump." );
        for ( Iterator i = configuration.getKeys(); i.hasNext(); )
        {
            String key = (String) i.next();
            Object value = configuration.getProperty( key );
            buffer.append( "\n\"" ).append( key ).append( "\" = \"" ).append( value ).append( "\"" );
        }
        return buffer.toString();
    }

    public boolean isEmpty()
    {
        return configuration.isEmpty();
    }

    public Registry getSubset( String key )
    {
        return new CommonsConfigurationRegistry( configuration.subset( key ) );
    }

    public List getList( String key )
    {
        return configuration.getList( key );
    }

    public List getSubsetList( String key )
    {
        List subsets = new ArrayList();

        boolean done = false;
        do
        {
            Registry registry = getSubset( key + "(" + subsets.size() + ")" );
            if ( !registry.isEmpty() )
            {
                subsets.add( registry );
            }
            else
            {
                done = true;
            }
        }
        while ( !done );

        return subsets;
    }

    public Properties getProperties( String key )
    {
        Configuration configuration = this.configuration.subset( key );

        Properties properties = new Properties();
        for ( Iterator i = configuration.getKeys(); i.hasNext(); )
        {
            String property = (String) i.next();
            properties.setProperty( property, configuration.getString( property ) );
        }
        return properties;
    }

    public void save()
        throws RegistryException
    {
        if ( configuration instanceof FileConfiguration )
        {
            FileConfiguration fileConfiguration = (FileConfiguration) configuration;
            try
            {
                fileConfiguration.save();
            }
            catch ( ConfigurationException e )
            {
                throw new RegistryException( e.getMessage(), e );
            }
        }
        else
        {
            throw new UnsupportedOperationException( "Can only save file-based configurations" );
        }
    }

    public void addChangeListener( RegistryListener listener )
    {
        EventSource configuration = (EventSource) this.configuration;

        configuration.addConfigurationListener( new ConfigurationListenerDelegate( listener, this ) );
    }

    public String getString( String key )
    {
        return configuration.getString( key );
    }

    public String getString( String key, String defaultValue )
    {
        return configuration.getString( key, defaultValue );
    }

    public void setString( String key, String value )
    {
        configuration.setProperty( key, value );
    }

    public int getInt( String key )
    {
        return configuration.getInt( key );
    }

    public int getInt( String key, int defaultValue )
    {
        return configuration.getInt( key, defaultValue );
    }

    public void setInt( String key, int value )
    {
        configuration.setProperty( key, new Integer( value ) );
    }

    public boolean getBoolean( String key )
    {
        return configuration.getBoolean( key );
    }

    public boolean getBoolean( String key, boolean defaultValue )
    {
        return configuration.getBoolean( key, defaultValue );
    }

    public void setBoolean( String key, boolean value )
    {
        configuration.setProperty( key, Boolean.valueOf( value ) );
    }

    public void addConfigurationFromResource( String resource )
        throws RegistryException
    {
        addConfigurationFromResource( resource, null );
    }

    public void addConfigurationFromResource( String resource, String prefix )
        throws RegistryException
    {
        CombinedConfiguration configuration = (CombinedConfiguration) this.configuration;
        if ( resource.endsWith( ".properties" ) )
        {
            try
            {
                getLogger().debug( "Loading properties configuration from classloader resource: " + resource );
                configuration.addConfiguration( new PropertiesConfiguration( resource ), null, prefix );
            }
            catch ( ConfigurationException e )
            {
                throw new RegistryException(
                    "Unable to add configuration from resource '" + resource + "': " + e.getMessage(), e );
            }
        }
        else if ( resource.endsWith( ".xml" ) )
        {
            try
            {
                getLogger().debug( "Loading XML configuration from classloader resource: " + resource );
                configuration.addConfiguration( new XMLConfiguration( resource ), null, prefix );
            }
            catch ( ConfigurationException e )
            {
                throw new RegistryException(
                    "Unable to add configuration from resource '" + resource + "': " + e.getMessage(), e );
            }
        }
        else
        {
            throw new RegistryException(
                "Unable to add configuration from resource '" + resource + "': unrecognised type" );
        }
    }

    public void addConfigurationFromFile( File file )
        throws RegistryException
    {
        addConfigurationFromFile( file, null );
    }

    public void addConfigurationFromFile( File file, String prefix )
        throws RegistryException
    {
        CombinedConfiguration configuration = (CombinedConfiguration) this.configuration;
        if ( file.getName().endsWith( ".properties" ) )
        {
            try
            {
                getLogger().debug( "Loading properties configuration from file: " + file );
                configuration.addConfiguration( new PropertiesConfiguration( file ), null, prefix );
            }
            catch ( ConfigurationException e )
            {
                throw new RegistryException(
                    "Unable to add configuration from file '" + file.getName() + "': " + e.getMessage(), e );
            }
        }
        else if ( file.getName().endsWith( ".xml" ) )
        {
            try
            {
                getLogger().debug( "Loading XML configuration from file: " + file );
                configuration.addConfiguration( new XMLConfiguration( file ), null, prefix );
            }
            catch ( ConfigurationException e )
            {
                throw new RegistryException(
                    "Unable to add configuration from file '" + file.getName() + "': " + e.getMessage(), e );
            }
        }
        else
        {
            throw new RegistryException(
                "Unable to add configuration from file '" + file.getName() + "': unrecognised type" );
        }
    }

    public void initialize()
        throws InitializationException
    {
        try
        {
            CombinedConfiguration configuration;
            if ( properties != null )
            {
                // TODO: changing this component to have a different configurator might be more appropriate?
                StringWriter w = new StringWriter();
                printConfiguration( properties, new PrintWriter( w ) );

                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                getLogger().debug( "Loading configuration into commons-configuration: " + w.toString() );
                builder.load( new StringReader( w.toString() ) );
                configuration = builder.getConfiguration( false );
            }
            else
            {
                getLogger().debug( "Creating a default configuration - no configuration was provided" );
                configuration = new CombinedConfiguration();
            }

            configuration.addConfiguration( new SystemConfiguration() );

            this.configuration = configuration;
        }
        catch ( ConfigurationException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
    }

    private static void printConfiguration( PlexusConfiguration configuration, PrintWriter writer )
        throws PlexusConfigurationException
    {
        printConfiguration( configuration, "", writer );
    }

    private static void printConfiguration( PlexusConfiguration configuration, String indent, PrintWriter writer )
        throws PlexusConfigurationException
    {
        writer.print( indent + "<" + configuration.getName() );
        String[] attrs = configuration.getAttributeNames();
        for ( int i = 0; i < attrs.length; i++ )
        {
            writer.print( " " + attrs[i] + "=\"" + configuration.getAttribute( attrs[i] ) + "\"" );
        }
        if ( configuration.getChildCount() > 0 )
        {
            writer.print( ">" );
            for ( int i = 0; i < configuration.getChildCount(); i++ )
            {
                writer.println();
                printConfiguration( configuration.getChild( i ), indent + "  ", writer );
            }
            writer.println( "</" + configuration.getName() + ">" );
        }
        else if ( configuration.getValue() != null )
        {
            writer.print( ">" );
            writer.print( configuration.getValue() );
            writer.println( "</" + configuration.getName() + ">" );
        }
        else
        {
            writer.println( "/>" );
        }
    }

    public void setProperties( PlexusConfiguration properties )
    {
        this.properties = properties;
    }

    public Registry getSection( String name )
    {
        CombinedConfiguration combinedConfiguration = (CombinedConfiguration) configuration;
        Configuration configuration = combinedConfiguration.getConfiguration( name );
        return new CommonsConfigurationRegistry( configuration );
    }
}
