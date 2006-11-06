package org.codehaus.plexus.security.configuration;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.plexus.evaluator.DefaultExpressionEvaluator;
import org.codehaus.plexus.evaluator.EvaluatorException;
import org.codehaus.plexus.evaluator.ExpressionEvaluator;
import org.codehaus.plexus.evaluator.ExpressionSource;
import org.codehaus.plexus.evaluator.sources.SystemPropertyExpressionSource;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConfigurationFactory
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.configuration.UserConfiguration"
 */
public class UserConfiguration
    extends CompositeConfiguration
    implements Initializable, LogEnabled, ExpressionSource
{
    public static final String ROLE = UserConfiguration.class.getName();

    private static final String DEFAULT_CONFIG_RESOURCE = "/org/codehaus/plexus/security/config-defaults.properties";

    /**
     * @plexus.requirement role-hint="default"
     */
    private ExpressionEvaluator evaluator;

    /**
     * @plexus.configuration
     */
    private List configs;

    private Logger logger;

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    public Logger getLog()
    {
        return this.logger;
    }

    public void initialize()
        throws InitializationException
    {
        if ( evaluator == null )
        {
            evaluator = (ExpressionEvaluator) new DefaultExpressionEvaluator();
        }

        evaluator.addExpressionSource( this );
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        if ( configs == null )
        {
            configs = new ArrayList();
        }

        if ( !configs.contains( DEFAULT_CONFIG_RESOURCE ) )
        {
            configs.add( DEFAULT_CONFIG_RESOURCE );
        }

        try
        {
            Iterator it = configs.iterator();
            while ( it.hasNext() )
            {
                String configName = (String) it.next();
                String resolvedConfigName = resolveName( configName );
                getLog().info(
                               "Attempting to find configuration [" + configName + "] (resolved to ["
                                   + resolvedConfigName + "])" );

                InputStream is = findConfig( resolvedConfigName );
                if ( is != null )
                {
                    PropertiesConfiguration userConfig = new PropertiesConfiguration();

                    userConfig.load( is );

                    IOUtil.close( is );

                    super.addConfiguration( userConfig );
                }
                else
                {
                    getLog().info( "Non-existant configuration [" + resolvedConfigName + "] not loaded." );
                }
            }
        }
        catch ( ConfigurationException e )
        {
            throw new InitializationException( "Unable to load configuration " + configs + " : " + e.getMessage(), e );
        }
    }

    public Object getProperty( String key )
    {
        Object value = super.getProperty( key );

        if ( value == null )
        {
            return null;
        }

        if ( value instanceof String )
        {
            try
            {
                return evaluator.expand( (String) value );
            }
            catch ( EvaluatorException e )
            {
                getLog().warn( "Unable to expand/evaluate \"" + value + "\": " + e.getMessage(), e );
                return value;
            }
        }

        return value;
    }

    private String resolveName( String name )
    {
        try
        {
            return evaluator.expand( name );
        }
        catch ( EvaluatorException e )
        {
            getLog().warn( "Unable to resolve configuration name: " + e.getMessage(), e );
            return name;
        }
    }

    private InputStream findConfig( String name )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            getLog().warn( "Unable to find empty config." );
            return null;
        }

        // Test for name as resource
        getLog().debug( "Testing [" + name + "] as resource" );
        URL resourceUrl = this.getClass().getResource( name );
        if ( resourceUrl != null )
        {
            try
            {
                getLog().debug( "Found [" + name + "] as resource" );
                return resourceUrl.openStream();
            }
            catch ( IOException e )
            {
                getLog().debug( "Resource [" + name + "] open stream error : " + e.getMessage(), e );
                // Ignore, try different technique.
            }
        }

        // Test for name as url.
        getLog().debug( "Testing [" + name + "] as url" );
        URL nameUrl;
        try
        {
            nameUrl = new URL( name );
            getLog().debug( "Found [" + name + "] as url" );
            return nameUrl.openStream();
        }
        catch ( MalformedURLException e )
        {
            getLog().debug( "URL [" + name + "] is malformed" );
            // Ignore, try different technique.
        }
        catch ( IOException e )
        {
            getLog().debug( "URL [" + name + "] open stream error : " + e.getMessage(), e );
            // Ignore, try different technique.
        }

        // Test for name as file.
        getLog().debug( "Testing [" + name + "] as file" );
        File nameFile = new File( name );
        if ( nameFile.exists() && nameFile.isFile() && nameFile.canRead() )
        {
            getLog().debug( "Found [" + name + "] as file" );
            try
            {
                return new FileInputStream( nameFile );
            }
            catch ( FileNotFoundException e )
            {
                getLog().debug( "File [" + name + "] open stream error : " + e.getMessage(), e );
                // Ignore, try different technique.
            }
        }

        // No other techniques to try.
        getLog().warn( "Unable to find configuration [" + name + "]" );
        return null;
    }

    public String getExpressionValue( String expression )
    {
        if ( StringUtils.equals( "config.default", expression ) )
        {
            return DEFAULT_CONFIG_RESOURCE;
        }

        if ( !super.containsKey( expression ) )
        {
            return null;
        }

        return super.getString( expression );
    }
}
