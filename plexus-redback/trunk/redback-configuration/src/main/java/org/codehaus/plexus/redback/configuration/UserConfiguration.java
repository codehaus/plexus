package org.codehaus.plexus.redback.configuration;

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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.evaluator.DefaultExpressionEvaluator;
import org.codehaus.plexus.evaluator.EvaluatorException;
import org.codehaus.plexus.evaluator.ExpressionEvaluator;
import org.codehaus.plexus.evaluator.sources.SystemPropertyExpressionSource;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.registry.Registry;
import org.codehaus.plexus.registry.RegistryException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ConfigurationFactory
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.redback.configuration.UserConfiguration"
 */
public class UserConfiguration
    extends AbstractLogEnabled
    implements Contextualizable, Initializable
{
    public static final String ROLE = UserConfiguration.class.getName();

    private static final String DEFAULT_CONFIG_RESOURCE = "org/codehaus/plexus/redback/config-defaults.properties";

    /**
     * @plexus.configuration
     * @deprecated Please configure the Plexus registry instead
     */
    private List configs;

    private Registry lookupRegistry;

    private static final String PREFIX = "org.codehaus.plexus.redback";

    private Registry registry;

    public void initialize()
        throws InitializationException
    {
        try
        {
            performLegacyInitialization();

            try
            {
                registry.addConfigurationFromResource( DEFAULT_CONFIG_RESOURCE, PREFIX );
            }
            catch ( RegistryException e )
            {
                // Ok, not found in context classloader; try the one in this jar.

                ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
                try
                {

                    Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
                    registry.addConfigurationFromResource( DEFAULT_CONFIG_RESOURCE, PREFIX );
                }
                finally
                {
                    Thread.currentThread().setContextClassLoader( prevCl );
                }
            }
        }
        catch ( RegistryException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }

        lookupRegistry = registry.getSubset( PREFIX );

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( lookupRegistry.dump() );
        }
    }

    private void performLegacyInitialization()
        throws InitializationException,
            RegistryException
    {
        ExpressionEvaluator evaluator = new DefaultExpressionEvaluator();
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        if ( configs == null )
        {
            configs = new ArrayList();
        }

        if ( !configs.isEmpty() )
        {
            // TODO: plexus should be able to do this on it's own.
            getLogger().warn(
                "DEPRECATED: the <configs> elements is deprecated. Please configure the Plexus registry instead" );
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

            registry.addConfigurationFromFile( new File( configName ), PREFIX );
        }
    }

    public String getString( String key )
    {
        return lookupRegistry.getString( key );
    }

    public int getInt( String key )
    {
        return lookupRegistry.getInt( key );
    }

    public boolean getBoolean( String key )
    {
        return lookupRegistry.getBoolean( key );
    }

    public int getInt( String key, int defaultValue )
    {
        return lookupRegistry.getInt( key, defaultValue );
    }

    public String getString( String key, String defaultValue )
    {
        return lookupRegistry.getString( key, defaultValue );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
        try
        {
            // elsewhere, this can be a requirement, but we need this for backwards compatibility
            registry = (Registry) container.lookup( Registry.class.getName(), "commons-configuration" );
        }
        catch ( ComponentLookupException e )
        {
            throw new ContextException( e.getMessage(), e );
        }
    }
}
