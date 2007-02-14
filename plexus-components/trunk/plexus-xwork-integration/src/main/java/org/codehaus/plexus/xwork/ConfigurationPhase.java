package org.codehaus.plexus.xwork;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
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
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.ServletContext;

/**
 * Configure a component using the servlet expression evaluator.
 *
 * @todo this is not an ideal way to do this - it would be good for the expression evaluation parts of Plexus
 * to be customisable without modifying the lifecycle
 * @deprecated
 */
public class ConfigurationPhase
    extends AbstractPhase
{
    public static final String DEFAULT_CONFIGURATOR_ID = "basic";

    public void execute( Object component, ComponentManager manager, ClassRealm realm )
        throws PhaseExecutionException
    {
        try
        {
            ComponentDescriptor descriptor = manager.getComponentDescriptor();

            String configuratorId = descriptor.getComponentConfigurator();

            if ( StringUtils.isEmpty( configuratorId ) )
            {
                configuratorId = DEFAULT_CONFIGURATOR_ID;
            }

            ComponentConfigurator componentConfigurator = (ComponentConfigurator) manager.getContainer().lookup(
                ComponentConfigurator.ROLE, configuratorId, realm );

            if ( manager.getComponentDescriptor().hasConfiguration() )
            {
                ServletContext servletContext =
                    (ServletContext) manager.getContainer().getContext().get( ServletContext.class.getName() );

                componentConfigurator.configureComponent( component,
                                                          manager.getComponentDescriptor().getConfiguration(),
                                                          new ServletExpressionEvaluator( servletContext ), realm );
            }
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
        catch ( ContextException e )
        {
            throw new PhaseExecutionException( "Unable to auto-configure component", e );
        }
    }
}
