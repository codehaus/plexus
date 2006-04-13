package org.codehaus.bacon.component.factory.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.codehaus.bacon.component.ComponentDescriptor;
import org.codehaus.bacon.component.factory.InstanceFactory;
import org.codehaus.bacon.component.factory.InstantiationException;

public class JavaInstanceFactory
    implements InstanceFactory
{

    private static final Class[] EMPTY_CLASSES = new Class[0];

    private static final Object[] EMPTY_INSTANCES = new Object[0];

    public Object instantiate( ComponentDescriptor descriptor, List constructionParameters,
                              ClassLoader componentLoader )
        throws InstantiationException
    {
        Class[] parameterClasses;
        Object[] parameters;
        if ( constructionParameters != null )
        {
            int paramCount = constructionParameters.size();

            parameterClasses = new Class[paramCount];
            parameters = new Object[paramCount];

            for ( int i = 0; i < paramCount; i++ )
            {
                Object parameter = (Object) constructionParameters.get( i );

                parameterClasses[i] = parameter.getClass();
                parameters[i] = parameter;
            }
        }
        else
        {
            parameterClasses = EMPTY_CLASSES;
            parameters = EMPTY_INSTANCES;
        }

        String className = descriptor.getImplementation();

        Object instance = null;
        try
        {
            Class clazz = componentLoader.loadClass( className );

            Constructor[] constructors = clazz.getConstructors();

            if ( constructors != null )
            {
                for ( int i = 0; i < constructors.length; i++ )
                {
                    Constructor constructor = constructors[i];

                    Class[] paramTypes = constructor.getParameterTypes();

                    int paramCount = paramTypes.length;

                    boolean foundConstructor = true;
                    if ( paramCount == parameterClasses.length )
                    {
                        parameterCheck: for ( int j = 0; j < paramCount; j++ )
                        {
                            Class paramClass = paramTypes[j];
                            Class checkClass = parameterClasses[j];

                            if ( !paramClass.isAssignableFrom( checkClass ) )
                            {
                                foundConstructor = false;
                                break parameterCheck;
                            }
                        }
                    }
                    
                    if ( foundConstructor )
                    {
                        try
                        {
                            instance = constructor.newInstance( parameters );
                        }
                        catch ( IllegalAccessException e )
                        {
                            throw new InstantiationException( "Error accessing constructor for: " + className + ". Reason: " + e.getMessage(), e );
                        }
                        catch ( InvocationTargetException e )
                        {
                            Throwable cause = e.getTargetException();
                            throw new InstantiationException( "Error occurred while invoking constructor for: " + className + ". Reason: " + cause.getMessage(), cause );
                        }
                        catch ( java.lang.InstantiationException e )
                        {
                            throw new InstantiationException( "Error instantiating: " + className + ". Reason: " + e.getMessage(), e );
                        }
                    }
                }
            }
            
            if ( instance == null )
            {
                throw new InstantiationException( "Cannot find matching constructor for: " + className + " with parameters: " + constructionParameters );
            }
        }
        catch ( ClassNotFoundException e )
        {
            throw new InstantiationException( "Cannot find component class: " + className, e );
        }

        return instance;
    }

}
