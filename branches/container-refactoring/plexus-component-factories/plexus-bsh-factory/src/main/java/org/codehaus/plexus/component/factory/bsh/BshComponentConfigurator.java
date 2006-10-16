package org.codehaus.plexus.component.factory.bsh;

import bsh.EvalError;
import bsh.Interpreter;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @todo not happy that this has to be different to the object with fields configurator - should only need to redefine the "setValue" method
 */
public class BshComponentConfigurator
    extends AbstractComponentConfigurator
{

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Interpreter interpreter = ( (BshComponent) component ).getInterpreter();

        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            Class type = Object.class;

            String implementation = childConfiguration.getAttribute( "implementation", null );

            if ( implementation != null )
            {
                try
                {
                    type = containerRealm.loadClass( implementation );

                }
                catch ( ClassNotFoundException e )
                {
                    String msg = "Class name which was explicitly given in configuration using 'implementation' attribute: '" +
                        implementation + "' cannot be loaded";

                    throw new ComponentConfigurationException( msg, e );
                }
            }

            ConfigurationConverter converter = converterLookup.lookupConverterForType( type );

            Object value = converter.fromConfiguration( converterLookup, childConfiguration, type, component.getClass(),
                                                        containerRealm.getClassLoader(), expressionEvaluator, listener );

            if ( value != null )
            {
                try
                {
                    interpreter.set( elementName, value );
                }
                catch ( EvalError evalError )
                {
                    throw new ComponentConfigurationException( "Unable to evaluate beanshell", evalError );
                }
            }
        }

    }


}
