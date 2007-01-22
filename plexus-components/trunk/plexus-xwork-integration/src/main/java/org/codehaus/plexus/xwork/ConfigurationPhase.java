package org.codehaus.plexus.xwork;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import javax.servlet.ServletContext;

/**
 * Configure a component using the servlet expression evaluator.
 *
 * @todo this is not an ideal way to do this - it would be good for the expression evaluation parts of Plexus
 * to be customisable without modifying the lifecycle
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

            ComponentConfigurator componentConfigurator =
                (ComponentConfigurator) manager.getContainer().lookup( ComponentConfigurator.ROLE, configuratorId );

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
