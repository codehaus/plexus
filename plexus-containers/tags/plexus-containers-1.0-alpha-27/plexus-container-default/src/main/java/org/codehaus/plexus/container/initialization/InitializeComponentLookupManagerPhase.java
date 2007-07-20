package org.codehaus.plexus.container.initialization;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import org.codehaus.plexus.ComponentLookupManager;
import org.codehaus.plexus.MutableComponentLookupManager;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentLookupManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-lookup-manager" );

        setupCoreComponent( ComponentLookupManager.ROLE, configurator, c, context.getContainer() );

        ComponentLookupManager lookupManager = context.getContainer().getComponentLookupManager();

        if ( lookupManager instanceof MutableComponentLookupManager )
        {
            ( (MutableComponentLookupManager) lookupManager ).setContainer( context.getContainer() );
        }

    }
}
