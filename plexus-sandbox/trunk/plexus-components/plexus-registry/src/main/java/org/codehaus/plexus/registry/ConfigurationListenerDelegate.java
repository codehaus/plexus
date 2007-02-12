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

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

/**
 * Commons configuration listener that delegates to the given registry listener.
 */
public class ConfigurationListenerDelegate
    implements ConfigurationListener
{
    /**
     * Delegate listener.
     */
    private RegistryListener listener;

    /**
     * Delegate registry.
     */
    private Registry registry;

    public ConfigurationListenerDelegate( RegistryListener listener, Registry registry )
    {
        this.listener = listener;

        this.registry = registry;
    }

    public void configurationChanged( ConfigurationEvent event )
    {
        if ( event.getPropertyName() != null )
        {
            if ( event.isBeforeUpdate() )
            {
                listener.beforeConfigurationChange( registry, event.getPropertyName(), event.getPropertyValue() );
            }
            else
            {
                listener.afterConfigurationChange( registry, event.getPropertyName(), event.getPropertyValue() );
            }
        }
    }
}
