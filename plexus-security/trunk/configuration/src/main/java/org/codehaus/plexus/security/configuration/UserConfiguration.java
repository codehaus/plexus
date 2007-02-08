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

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.codehaus.plexus.evaluator.DefaultExpressionEvaluator;
import org.codehaus.plexus.evaluator.EvaluatorException;
import org.codehaus.plexus.evaluator.ExpressionEvaluator;
import org.codehaus.plexus.evaluator.sources.SystemPropertyExpressionSource;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConfigurationFactory
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.configuration.UserConfiguration"
 */
public class UserConfiguration
    extends AbstractLogEnabled
    implements Initializable
{
    public static final String ROLE = UserConfiguration.class.getName();

    private static final String DEFAULT_CONFIG_RESOURCE = "org/codehaus/plexus/security/config-defaults.properties";

    /**
     * @plexus.configuration
     */
    private List configs;

    private CombinedConfiguration configuration = new CombinedConfiguration();

    public void initialize()
        throws InitializationException
    {
        ExpressionEvaluator evaluator = new DefaultExpressionEvaluator();
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        if ( configs == null )
        {
            configs = new ArrayList();
        }

        if ( !configs.contains( DEFAULT_CONFIG_RESOURCE ) )
        {
            configs.add( DEFAULT_CONFIG_RESOURCE );
        }

        Iterator it = configs.iterator();
        while ( it.hasNext() )
        {
            String configName = (String) it.next();
            try
            {
                configName = evaluator.expand( configName );
            }
            catch ( EvaluatorException e )
            {
                getLogger().warn( "Unable to resolve configuration name: " + e.getMessage(), e );
            }
            getLogger().info(
                "Attempting to find configuration [" + configName + "] (resolved to [" + configName + "])" );

            try
            {
                PropertiesConfiguration userConfig = new PropertiesConfiguration( configName );

                configuration.addConfiguration( userConfig );
            }
            catch ( ConfigurationException e )
            {
                throw new InitializationException( "Unable to load configuration " + configs + " : " + e.getMessage(),
                                                   e );
            }
        }
        configuration.addConfiguration( new SystemConfiguration() );

        if ( getLogger().isDebugEnabled() )
        {
            StringBuffer dbg = new StringBuffer();
            dumpState( dbg );
            getLogger().debug( dbg.toString() );
        }
    }

    public StringBuffer dumpState( StringBuffer sb )
    {
        sb.append( "Configuration Dump." );
        Iterator it = configuration.getKeys();
        while ( it.hasNext() )
        {
            String key = (String) it.next();
            sb.append( "\n\"" ).append( key ).append( "\" = \"" ).append( configuration.getProperty( key ) ).append(
                "\"" );
        }
        return sb;
    }

    public String getString( String key )
    {
        return configuration.getString( key );
    }

    public int getInt( String key )
    {
        return configuration.getInt( key );
    }

    public boolean getBoolean( String key )
    {
        return configuration.getBoolean( key );
    }

    public int getInt( String key, int defaultValue )
    {
        return configuration.getInt( key, defaultValue );
    }

    public String getString( String key, String defaultValue )
    {
        return configuration.getString( key, defaultValue );
    }
}
