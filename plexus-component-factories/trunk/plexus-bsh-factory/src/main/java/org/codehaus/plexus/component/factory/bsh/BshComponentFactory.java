package org.codehaus.plexus.component.factory.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.IOUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * BeanShell component factory.
 *
 * @author <a href="mailto:jason@maven.org">Jason Van Zyl</a>
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 */
public class BshComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        String impl = componentDescriptor.getImplementation();

        if ( !impl.startsWith( "/" ) )
        {
            impl = "/" + impl;
        }

        URL scriptLocation = classRealm.getResource( impl );

        if ( scriptLocation == null )
        {
            StringBuffer buf = new StringBuffer( "Cannot find: " + impl + " in classpath:" );

            for ( int i = 0; i < classRealm .getURLs().length; i++ )
            {
                URL constituent = classRealm.getURLs()[i];
                buf.append( "\n   [" + i + "]  " + constituent );
            }

            throw new ComponentInstantiationException( buf.toString() );
        }

        Object result = null;

        Reader reader = null;

        try
        {
            Interpreter interp = new Interpreter();

            reader = new InputStreamReader( scriptLocation.openStream() );

            // TODO
            // BeanShell honours the context classloader, which something is setting (erroneously?)
//            interp.setClassLoader( classLoader.getClassLoader() );
            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( classRealm );
            result = interp.eval( reader );
            Thread.currentThread().setContextClassLoader( oldClassLoader );
        }
        catch ( EvalError evalError )
        {
            container.getLogger().info( "Error text: " + evalError.getErrorText() );

            throw new ComponentInstantiationException( "Cannot build component for: " +
                componentDescriptor.getComponentKey() + "; unable to read BeanShell script", evalError );
        }
        catch ( FileNotFoundException e )
        {
            throw new ComponentInstantiationException( "Cannot build component for: " +
                componentDescriptor.getComponentKey() + "; unable to read BeanShell script", e );
        }
        catch ( IOException e )
        {
            throw new ComponentInstantiationException( "Cannot build component for: " +
                componentDescriptor.getComponentKey() + "; unable to read BeanShell script", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        return result;
    }

}
