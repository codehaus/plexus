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

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * TODO! This should not be needed - add the necessary capability to the other component value setter to merge results with existing values
 */
public class RegistryComponentValueSetter
{
    private Object object;

    private String fieldName;

    private ConverterLookup lookup;

    private Method setter;

    private Class setterParamType;

    private ConfigurationConverter setterTypeConverter;

    private Field field;

    private Class fieldType;

    private ConfigurationConverter fieldTypeConverter;

    private ConfigurationListener listener;

    public RegistryComponentValueSetter( String fieldName, Object object, ConverterLookup lookup )
        throws ComponentConfigurationException
    {
        this( fieldName, object, lookup, null );
    }

    public RegistryComponentValueSetter( String fieldName, Object object, ConverterLookup lookup,
                                         ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        this.fieldName = fieldName;
        this.object = object;
        this.lookup = lookup;
        this.listener = listener;

        if ( object == null )
        {
            throw new ComponentConfigurationException( "Component is null" );
        }

        setter = ReflectionUtils.getSetter( this.fieldName, this.object.getClass() );

        if ( setter != null )
        {
            setterParamType = setter.getParameterTypes()[0];

            try
            {
                setterTypeConverter = this.lookup.lookupConverterForType( setterParamType );
            }
            catch ( ComponentConfigurationException e )
            {
                // ignore, handle later
            }
        }

        field = ReflectionUtils.getFieldByNameIncludingSuperclasses( this.fieldName, this.object.getClass() );

        if ( field != null )
        {
            fieldType = field.getType();

            try
            {
                fieldTypeConverter = this.lookup.lookupConverterForType( fieldType );
            }
            catch ( ComponentConfigurationException e1 )
            {
                // ignore, handle later
            }
        }

        if ( setter == null && field == null )
        {
            throw new ComponentConfigurationException(
                "Cannot find setter nor field in " + object.getClass().getName() + " for '" + fieldName + "'" );
        }

        if ( setterTypeConverter == null && fieldTypeConverter == null )
        {
            throw new ComponentConfigurationException( "Cannot find converter for " + setterParamType.getName() +
                ( fieldType != null && !fieldType.equals( setterParamType ) ? " or " + fieldType.getName() : "" ) );
        }
    }

    private void setValueUsingField( Object value )
        throws ComponentConfigurationException
    {
        String exceptionInfo =
            object.getClass().getName() + "." + field.getName() + "; type: " + value.getClass().getName();

        value = mergeExisting( value, exceptionInfo );

        try
        {
            boolean wasAccessible = field.isAccessible();

            if ( !wasAccessible )
            {
                field.setAccessible( true );
            }

            if ( listener != null )
            {
                listener.notifyFieldChangeUsingReflection( fieldName, value, object );
            }

            field.set( object, value );

            if ( !wasAccessible )
            {
                field.setAccessible( false );
            }
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException( "Cannot access field: " + exceptionInfo, e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ComponentConfigurationException( "Cannot assign value '" + value + "' to field: " + exceptionInfo,
                                                       e );
        }
    }

    private void setValueUsingSetter( Object value )
        throws ComponentConfigurationException
    {
        String exceptionInfo =
            object.getClass().getName() + "." + setter.getName() + "( " + setterParamType.getClass().getName() + " )";

        value = mergeExisting( value, exceptionInfo );

        if ( setterParamType == null || setter == null )
        {
            throw new ComponentConfigurationException( "No setter found" );
        }

        if ( listener != null )
        {
            listener.notifyFieldChangeUsingSetter( fieldName, value, object );
        }

        try
        {
            setter.invoke( object, new Object[]{value} );
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException( "Cannot access method: " + exceptionInfo, e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ComponentConfigurationException(
                "Invalid parameter supplied while setting '" + value + "' to " + exceptionInfo, e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ComponentConfigurationException( "Setter " + exceptionInfo +
                " threw exception when called with parameter '" + value + "': " + e.getTargetException().getMessage(),
                                                       e );
        }
    }

    private Object mergeExisting( Object value, String exceptionInfo )
        throws ComponentConfigurationException
    {
        try
        {
            // TODO: use a getter if present?
            boolean wasAccessible = field.isAccessible();

            if ( !wasAccessible )
            {
                field.setAccessible( true );
            }

            // TODO! this is crude. We should pass the instance to the fromConfiguration so it can decide what to do
            if ( Map.class.isAssignableFrom( value.getClass() ) )
            {
                Map map = (Map) field.get( object );
                if ( map != null )
                {
                    map.putAll( (Map) value );
                    value = map;
                }
            }
            else if ( Collection.class.isAssignableFrom( value.getClass() ) )
            {
                Collection collection = (Collection) field.get( object );
                if ( collection != null )
                {
                    collection.addAll( (Collection) value );
                    value = collection;
                }
            }

            if ( !wasAccessible )
            {
                field.setAccessible( false );
            }
        }
        catch ( IllegalAccessException e )
        {
            throw new ComponentConfigurationException( "Cannot access method: " + exceptionInfo, e );
        }
        return value;
    }

    public void configure( PlexusConfiguration config, ClassLoader cl, ExpressionEvaluator evaluator )
        throws ComponentConfigurationException
    {
        Object value = null;

        // try setter converter + method first

        if ( setterTypeConverter != null )
        {
            try
            {
                value = setterTypeConverter.fromConfiguration( lookup, config, setterParamType, object.getClass(), cl,
                                                               evaluator, listener );

                if ( value != null )
                {
                    setValueUsingSetter( value );
                    return;
                }
            }
            catch ( ComponentConfigurationException e )
            {
                if ( fieldTypeConverter == null ||
                    fieldTypeConverter.getClass().equals( setterTypeConverter.getClass() ) )
                {
                    throw e;
                }
            }
        }

        // try setting field using value found with method
        // converter, if present.

        ComponentConfigurationException savedEx = null;

        if ( value != null )
        {
            try
            {
                setValueUsingField( value );
                return;
            }
            catch ( ComponentConfigurationException e )
            {
                savedEx = e;
            }
        }

        // either no value or setting went wrong. Try
        // new converter.

        value = fieldTypeConverter.fromConfiguration( lookup, config, fieldType, object.getClass(), cl, evaluator,
                                                      listener );

        if ( value != null )
        {
            setValueUsingField( value );
        }
        // FIXME: need this?
        else if ( savedEx != null )
        {
            throw savedEx;
        }
    }
}
