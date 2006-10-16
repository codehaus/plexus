package org.codehaus.bacon.component.injector.java;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.codehaus.bacon.component.InjectionDescriptor;
import org.codehaus.bacon.component.injection.ComponentInjectionException;
import org.codehaus.bacon.component.injection.ComponentInjector;
import org.codehaus.bacon.component.language.java.JavaLanguageConstants;
import org.codehaus.bacon.component.util.ognl.ClassLoaderResolver;

public class JavaComponentInjector
    implements ComponentInjector
{
    public static final String LANGUAGE = "java";

    public void inject( Object instance, Map valuesByCompositionSource, ClassLoader containerLoader )
        throws ComponentInjectionException
    {
        if ( valuesByCompositionSource != null && !valuesByCompositionSource.isEmpty() )
        {
            for ( Iterator it = valuesByCompositionSource.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                InjectionDescriptor source = (InjectionDescriptor) entry.getKey();
                Object value = entry.getValue();

                String injectionStrategy = source.getInjectionStrategy();

                String injectionTarget = source.getInjectionTarget();

                if ( JavaLanguageConstants.FIELD_INJECTION_STRATEGY.equals( injectionStrategy ) )
                {
                    try
                    {
                        Field field = instance.getClass().getField( injectionTarget );

                        boolean accessible = field.isAccessible();
                        if ( !accessible )
                        {
                            field.setAccessible( true );
                        }

                        field.set( instance, value );
                        field.setAccessible( accessible );
                    }
                    catch ( SecurityException e )
                    {
                        throw new ComponentInjectionException( "Security is restricted for field: " + injectionTarget
                            + " in: " + instance.getClass() + ". Error: " + e.getMessage(), e );
                    }
                    catch ( NoSuchFieldException e )
                    {
                        throw new ComponentInjectionException( "Field: " + injectionTarget + " in: " + instance.getClass()
                            + " could not be found.", e );
                    }
                    catch ( IllegalAccessException e )
                    {
                        throw new ComponentInjectionException( "Illegal access for field: " + injectionTarget + " in: "
                            + instance.getClass() + ". Error: " + e.getMessage(), e );
                    }
                }
                else
                {
                    try
                    {
                        Ognl.createDefaultContext(instance, new ClassLoaderResolver( containerLoader ) );
                        Ognl.setValue( injectionTarget, instance, value );
                    }
                    catch ( OgnlException e )
                    {
                        throw new ComponentInjectionException( "Error setting value of: " + injectionTarget
                            + " on root object: " + instance.getClass() + ". Error: " + e.getMessage(), e );
                    }
                }
            }
        }
    }

}
