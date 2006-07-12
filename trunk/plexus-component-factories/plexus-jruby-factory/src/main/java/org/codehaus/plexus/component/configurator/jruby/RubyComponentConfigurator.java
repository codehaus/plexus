package org.codehaus.plexus.component.configurator.jruby;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * TODO: This will back up a Java interface with a RubyObject  
 * 
 * @author eredmond
 */
public class RubyComponentConfigurator
    extends AbstractComponentConfigurator
{

    /**
     * A JRuby valid component is either a plain bean with setters starting with "set",
     * or containing a method named "set" which takes a first String.class parameter, and
     * Object.class second (dynamic bean).
     * 
     * Can currently only set String values.
     */
    public void configureComponent( Object component, PlexusConfiguration configuration,
                                   ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                   ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        ConfigurationConverter converter = new ClassRealmConverter( containerRealm );
        converterLookup.registerConverter( converter );

        int items = configuration.getChildCount();

        HashMap setMethodMap = new HashMap();
        Method plainSet = null;
        Method[] methods = component.getClass().getMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            String methodName = methods[i].getName();
            Class[] paramTypes = methods[i].getParameterTypes();
            if ( methodName.startsWith( "set" ) && paramTypes.length == 1 )
            {
                setMethodMap.put( methodName.substring( 3 ), methods[i] );
            }
            else if ( methodName.equals( "set" ) && paramTypes.length == 2 && paramTypes[0].equals( String.class )
                && paramTypes[1].equals( Object.class ) )
            {
                plainSet = methods[i];
            }
        }

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            Class setterParamType = String.class;

            ConfigurationConverter setterTypeConverter = converterLookup.lookupConverterForType( setterParamType );

            Object value = setterTypeConverter.fromConfiguration( converterLookup, childConfiguration, setterParamType,
                                                                  component.getClass(),
                                                                  containerRealm.getClassLoader(), expressionEvaluator,
                                                                  listener );

            if ( value != null )
            {
                if ( setterParamType == null )
                {
                    throw new ComponentConfigurationException( "No setter found" );
                }

                if ( listener != null )
                {
                    listener.notifyFieldChangeUsingSetter( elementName, value, component );
                }

                try
                {

                    try
                    {
                        Field field = component.getClass().getField( elementName );
                        if ( field != null )
                        {
                            field.set( component, value );
                            continue;
                        }
                    }
                    catch ( NoSuchFieldException e )
                    {
                    }
                    catch ( IllegalAccessException e )
                    {
                    }

                    try
                    {
                        // up case the first char
                        String upElementName = Character.toUpperCase( elementName.charAt( 0 ) )
                            + elementName.substring( 1 );

                        // First attempt to set a method name, then a field, then a "set" method w/ string
                        Method setter = (Method) setMethodMap.get( upElementName );
                        if ( setter != null )
                        {
                            setter.invoke( component, new Object[] { value } );
                            continue;
                        }
                    }
                    catch ( IllegalAccessException e )
                    {
                    }
                    catch ( InvocationTargetException e )
                    {
                    }

                    try
                    {
                        if ( plainSet != null )
                        {
                            plainSet.invoke( component, new Object[] { elementName, value } );
                        }
                    }
                    catch ( IllegalAccessException e )
                    {
                    }
                    catch ( InvocationTargetException e )
                    {
                    }

                }
                catch ( IllegalArgumentException e )
                {
                    throw new ComponentConfigurationException( "Invalid parameter supplied while setting '" + value
                        + "' to " + component.getClass().getName() + "." + elementName + "( "
                        + setterParamType.getClass().getName() + " )", e );
                }

                return;
            }

        }
    }
}
