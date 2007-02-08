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
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

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
    implements Registry, Initializable
{
    /**
     * The combined configuration instance that houses the registry.
     */
    private CombinedConfiguration configuration;

    /**
     * The configuration properties for the registry. This should take the format of an input to the Commons
     * Configuration
     * <a href="http://jakarta.apache.org/commons/configuration/howto_configurationbuilder.html">configuration
     * builder</a>.
     *
     * @plexus.configuration
     */
    private Xpp3Dom properties;

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

    public String getString( String key )
    {
        return configuration.getString( key );
    }

    public String getString( String key, String defaultValue )
    {
        return configuration.getString( key, defaultValue );
    }

    public int getInt( String key )
    {
        return configuration.getInt( key );
    }

    public int getInt( String key, int defaultValue )
    {
        return configuration.getInt( key, defaultValue );
    }

    public boolean getBoolean( String key )
    {
        return configuration.getBoolean( key );
    }

    public void addConfigurationFromResource( String resource )
        throws RegistryException
    {
        if ( resource.endsWith( ".properties" ) )
        {
            try
            {
                getLogger().debug( "Loading properties configuration from classloader resource: " + resource );
                configuration.addConfiguration( new PropertiesConfiguration( resource ) );
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
                configuration.addConfiguration( new XMLConfiguration( resource ) );
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
        if ( file.getName().endsWith( ".properties" ) )
        {
            try
            {
                getLogger().debug( "Loading properties configuration from file: " + file );
                configuration.addConfiguration( new PropertiesConfiguration( file ) );
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
                configuration.addConfiguration( new XMLConfiguration( file ) );
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
            if ( properties != null )
            {
                // TODO: changing this to have a different configurator might be more appropriate?
                StringWriter w = new StringWriter();
                Xpp3DomWriter.write( w, properties );

                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                getLogger().debug( "Loading configuration into commons-configuration: " + w.toString() );
                builder.load( new StringReader( w.toString() ) );
                configuration = builder.getConfiguration( true );
            }
            else
            {
                getLogger().debug( "Creating a default configuration - no configuration was provided" );
                configuration = new CombinedConfiguration();
            }

            configuration.addConfiguration( new SystemConfiguration() );
        }
        catch ( ConfigurationException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }
    }
}
