package org.codehaus.plexus.component.configurator.converters;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractConfigurationConverter
    implements ConfigurationConverter
{
    private static final String IMPLEMENTATION = "implementation";

    // ----------------------------------------------------------------------
    // ConfigurationConverter Implementation
    // ----------------------------------------------------------------------

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        return fromConfiguration( converterLookup, configuration, type, baseType, classLoader, expressionEvaluator,
                                  null );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * We will check if user has provided a hint which class should be used for given field.
     * So we will check if something like <foo implementation="com.MyFoo"> is present in configuraion.
     * If 'implementation' hint was provided we will try to load correspoding class
     * If we are unable to do so error will be reported
     */
    protected Class getClassForImplementationHint( Class type, PlexusConfiguration configuration,
                                                   ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        Class retValue = type;

        String implementation = configuration.getAttribute( IMPLEMENTATION, null );

        if ( implementation != null )
        {
            try
            {
                retValue = classLoader.loadClass( implementation );
            }
            catch ( ClassNotFoundException e )
            {
                String msg = "Class name which was explicitly given in configuration using 'implementation' " +
                    "attribute: '" + implementation + "' cannot be loaded";

                throw new ComponentConfigurationException( msg, e );
            }
        }

        return retValue;
    }

    protected Class getImplementationClass( Class type, Class baseType, PlexusConfiguration configuration, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        // if there's an implementation hint, try that.

        Class childType = getClassForImplementationHint( null, configuration, classLoader );

        if ( childType != null )
        {
            return childType;
        }

        // try using the fieldname to determine the implementation.

        String configEntry = configuration.getName();

        String name = fromXML( configEntry );

        // First, see whether the fieldname might be a fully qualified classname

        if ( name.indexOf( '.' ) > 0 )
        {
            try
            {
                return classLoader.loadClass( name );
            }
            catch ( ClassNotFoundException e )
            {
                // not found, continue processing
            }
        }

        // Next, try to find a class in the package of the object we're configuring

        String className = constructClassName( baseType, name );

        Exception lastException;

        try
        {
            return classLoader.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            // the guessed class does not exist. Store exception for later use. Continue processing.
            lastException = e;
        }

        // if the given type is not null, just return that

        if ( type != null )
        {
            return type;
        }
        else
        {
            // type is only null if we have a Collection and this method is called
            // for the compound type of that collection.

            if ( configuration.getChildCount() == 0 )
            {
                // If the configuration has no children but only text, try a String.
                // TODO: If we had generics we could try that instead - or could the component descriptor list an impl?
                return String.class;
            }
            else
            {
                // there are no options left. Our best guess is that the fieldname
                // indicates a class in the component's package, so report that.

                throw new ComponentConfigurationException( "Error loading class '" + className + "'", lastException );
            }
        }
    }

    /**
     * Constructs a classname from a class and a fieldname.
     * For example, baseType is 'package.Component',
     * field is 'someThing', then it constructs 'package.SomeThing'.
     *
     */
    private String constructClassName( Class baseType, String name )
    {
        String baseTypeName = baseType.getName();

        // Some classloaders don't create Package objects for classes
        // so we have to resort to slicing up the class name

        int lastDot = baseTypeName.lastIndexOf( '.' );

        String className;

        if ( lastDot == -1 )
        {
            className = name;
        }
        else
        {
            String basePackage = baseTypeName.substring( 0, lastDot );

            className = basePackage + "." + StringUtils.capitalizeFirstLetter( name );
        }

        return className;
    }

    protected Class loadClass( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        try
        {
            return classLoader.loadClass( classname );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentConfigurationException( "Error loading class '" + classname + "'", e );
        }
    }

    protected Object instantiateObject( String classname, ClassLoader classLoader )
        throws ComponentConfigurationException
    {
        return instantiateObject( loadClass( classname, classLoader ) );
    }

    protected Object instantiateObject( Class clazz )
        throws ComponentConfigurationException
    {
        try
        {
            return clazz.newInstance();
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException( "Class '" + clazz.getName() + "' cannot be instantiated", e );
        }
        catch ( InstantiationException e )
        {
            throw new ComponentConfigurationException( "Class '" + clazz.getName() + "' cannot be instantiated", e );
        }
    }

    // first-name --> firstName
    protected String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    protected String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }

    protected Object fromExpression( PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                     Class type )
        throws ComponentConfigurationException
    {
        Object v = fromExpression( configuration, expressionEvaluator );
        if ( v != null )
        {
            if ( !type.isAssignableFrom( v.getClass() ) )
            {
                String msg = "Cannot assign configuration entry '" + configuration.getName() + "' to '" + type +
                    "' from '" + configuration.getValue( null ) + "', which is of type " + v.getClass();
                throw new ComponentConfigurationException( configuration, msg );
            }
        }
        return v;
    }

    protected Object fromExpression( PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        Object v = null;
        String value = configuration.getValue( null );
        if ( value != null && value.length() > 0 )
        {
            // Object is provided by an expression
            // This seems a bit ugly... canConvert really should return false in this instance, but it doesn't have the
            //   configuration to know better
            try
            {
                v = expressionEvaluator.evaluate( value );
            }
            catch ( ExpressionEvaluationException e )
            {
                String msg = "Error evaluating the expression '" + value + "' for configuration value '" +
                    configuration.getName() + "'";
                throw new ComponentConfigurationException( configuration, msg, e );
            }
        }
        if ( v == null )
        {
            value = configuration.getAttribute( "default-value", null );
            if ( value != null && value.length() > 0 )
            {
                try
                {
                    v = expressionEvaluator.evaluate( value );
                }
                catch ( ExpressionEvaluationException e )
                {
                    String msg = "Error evaluating the expression '" + value + "' for configuration value '" +
                        configuration.getName() + "'";
                    throw new ComponentConfigurationException( configuration, msg, e );
                }
            }
        }
        return v;
    }
}
