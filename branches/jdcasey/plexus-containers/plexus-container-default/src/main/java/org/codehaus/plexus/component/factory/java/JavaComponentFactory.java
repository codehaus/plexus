package org.codehaus.plexus.component.factory.java;

import java.lang.reflect.Modifier;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classloading.ClassLoaderUtils;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Component Factory for components written in Java Language which have default no parameter constructor
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class JavaComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassLoader classLoader, PlexusContainer container )
        throws ComponentInstantiationException
    {
        Class implementationClass = null;

        try
        {
            String implementation = componentDescriptor.getImplementation();

            implementationClass = classLoader.loadClass( implementation );

            int modifiers = implementationClass.getModifiers();

            if ( Modifier.isInterface( modifiers ) )
            {
                throw new ComponentInstantiationException( "Cannot instanciate implementation '" + implementation + "' because the class is a interface." );
            }

            if ( Modifier.isAbstract( modifiers ) )
            {
                throw new ComponentInstantiationException( "Cannot instanciate implementation '" + implementation + "' because the class is abstract." );
            }

            Object instance = implementationClass.newInstance();

            return instance;
        }
        catch ( InstantiationException e )
        {
            throw makeException( classLoader, componentDescriptor, implementationClass, e );
        }
        catch ( ClassNotFoundException e )
        {
            throw makeException( classLoader, componentDescriptor, implementationClass, e );
        }
        catch( IllegalAccessException e )
        {
            throw makeException( classLoader, componentDescriptor, implementationClass, e );
        }
        catch( LinkageError e )
        {
            throw makeException( classLoader, componentDescriptor, implementationClass, e );
        }
    }

    private ComponentInstantiationException makeException( ClassLoader classLoader, ComponentDescriptor componentDescriptor, Class implementationClass, Throwable e )
    {
        // ----------------------------------------------------------------------
        // Display the realm when there is an error, We should probably return a string here so we
        // can incorporate this into the error message for easy debugging.
        // ----------------------------------------------------------------------

        System.out.println( ClassLoaderUtils.getClassLoaderInfo( classLoader ) );

        String msg = "Could not instanciate component: " + componentDescriptor.getHumanReadableKey();

        return new ComponentInstantiationException( msg, e );
    }
}
