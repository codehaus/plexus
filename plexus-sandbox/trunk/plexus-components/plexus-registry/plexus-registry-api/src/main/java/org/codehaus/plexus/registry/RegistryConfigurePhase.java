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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Configure Plexus components from the registry.
 * <p/>
 * Fields are configured by looking up the following value from the registry:
 * <code><i>role</i>.<i>roleHint</i>.<i>fieldName</i></code>.
 * <p/>
 * For example: <code>org.codehaus.plexus.jdo.JdoFactory.users.otherProperties.org.jpox.identifier.case=PreserveCase</code>.
 */
public class RegistryConfigurePhase
    extends AbstractPhase
{
    /**
     * The registry component to use.
     */
    private Registry registry;

    public void execute( Object component, ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException
    {
        ComponentDescriptor descriptor = manager.getComponentDescriptor();
        if ( !descriptor.getRole().equals( Registry.class.getName() ) )
        {
            Registry registry = getRegistry( manager, realm );

            String key = descriptor.getRole();
            if ( descriptor.getRoleHint() != null )
            {
                key += "." + descriptor.getRoleHint();
            }

            registry = registry.getSubset( key );

            try
            {
                String configuratorId = descriptor.getComponentConfigurator();

                if ( StringUtils.isEmpty( configuratorId ) )
                {
                    configuratorId = "basic";
                }

                ComponentConfigurator componentConfigurator = (ComponentConfigurator) manager.getContainer().lookup(
                    ComponentConfigurator.ROLE, configuratorId, realm );

                ClassRealm componentRealm = manager.getContainer().getComponentRealm( descriptor.getRealmId() );

                componentConfigurator.configureComponent( component, new RegistryPlexusConfiguration( registry ),
                                                          componentRealm );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException(
                    "Unable to auto-configure component as its configurator could not be found", e );
            }
            catch ( ComponentConfigurationException e )
            {
                throw new PhaseExecutionException( "Unable to auto-configure component", e );
            }
        }
    }

    private synchronized Registry getRegistry( ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException
    {
        if ( registry == null )
        {
            try
            {
                registry = (Registry) manager.getContainer().lookup( Registry.class.getName(), "commons-configuration",
                                                                     realm );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "Unable to configure component as the registry could not be found",
                                                   e );
            }
        }
        return registry;
    }
}
