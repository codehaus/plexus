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

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Converts a registry into an integer.
 */
public class RegistryIntConverter
    extends AbstractRegistryConverter
{
    public boolean canConvert( Class type )
    {
        return type.equals( int.class ) || type.equals( Integer.class ) || type.equals( short.class ) ||
            type.equals( Short.class ) || type.equals( long.class ) || type.equals( Long.class );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        RegistryPlexusConfiguration registryPlexusConfiguration = (RegistryPlexusConfiguration) configuration;

        return new Integer( registryPlexusConfiguration.getRegistry().getInt( configuration.getName() ) );
    }
}
