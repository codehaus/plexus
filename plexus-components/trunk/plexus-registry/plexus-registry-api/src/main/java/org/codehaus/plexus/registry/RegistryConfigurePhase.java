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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

import java.util.Iterator;

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

    private ConverterLookup converterLookup;

    public RegistryConfigurePhase()
    {
        converterLookup = new DefaultConverterLookup();
        converterLookup.registerConverter( new RegistryMapConverter() );
        converterLookup.registerConverter( new RegistryCollectionConverter() );
        converterLookup.registerConverter( new RegistryStringConverter() );
        converterLookup.registerConverter( new RegistryIntConverter() );
    }

    public void execute( Object component, ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException
    {
        ComponentDescriptor descriptor = manager.getComponentDescriptor();
        if ( !descriptor.getRole().equals( Registry.class.getName() ) &&
            !descriptor.getRole().equals( ComponentConfigurator.ROLE ) )
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
                ClassRealm componentRealm = manager.getContainer().getComponentRealm( descriptor.getRealmId() );

                configure( component, registry, componentRealm );
            }
            catch ( ComponentConfigurationException e )
            {
                throw new PhaseExecutionException( "Unable to auto-configure component", e );
            }
        }
    }

    private void configure( Object component, Registry registry, ClassRealm componentRealm )
        throws ComponentConfigurationException
    {
        for ( Iterator i = registry.getKeys().iterator(); i.hasNext(); )
        {
            String name = (String) i.next();
            Registry subset = registry.getSubset( name );

            PlexusConfiguration childConfiguration = new RegistryPlexusConfiguration( name, subset, registry );

            RegistryComponentValueSetter valueSetter =
                new RegistryComponentValueSetter( name, component, converterLookup, null );

            valueSetter.configure( childConfiguration, componentRealm, new DefaultExpressionEvaluator() );
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
