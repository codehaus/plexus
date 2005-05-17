package org.codehaus.plexus.component.factory.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * BeanShell component factory.
 *
 * @author <a href="mailto:jason@maven.org">Jason Van Zyl</a>
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 */
public class BshComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        String impl = componentDescriptor.getImplementation() + ".bsh";
        if ( !impl.startsWith( "/" ) )
        {
            impl = "/" + impl;
        }

        URL scriptLocation = classRealm.getResource( impl );

        if ( scriptLocation == null )
        {
            StringBuffer buf = new StringBuffer( "Cannot find: " + impl + " in classpath:" );
            for ( int i = 0; i < classRealm.getConstituents().length; i++ )
            {
                URL constituent = classRealm.getConstituents()[i];
                buf.append( "\n   [" + i + "]  " + constituent );
            }
            throw new ComponentInstantiationException( buf.toString() );
        }

        Object result = null;

        try
        {
            Interpreter interp = new Interpreter();

            String file = null;
            if ( scriptLocation.getProtocol().equals( "file" ) )
            {
                file = scriptLocation.getFile();
            }
            else
            {
                File f = File.createTempFile( "plexus", "bsh" );
                f.deleteOnExit();
                // TODO: need to try/finally and close?
                IOUtil.copy( scriptLocation.openStream(), new FileWriter( f ) );
                file = f.getAbsolutePath();
            }

            result = interp.source( file );
        }
        catch ( EvalError evalError )
        {
            throw new ComponentInstantiationException( "Cannot build component for: " +
                                                       componentDescriptor.getComponentKey() +
                                                       "; unable to read BeanShell script", evalError );
        }
        catch ( FileNotFoundException e )
        {
            classRealm.display();
            throw new ComponentInstantiationException( "Cannot build component for: " +
                                                       componentDescriptor.getComponentKey() +
                                                       "; unable to read BeanShell script", e );
        }
        catch ( IOException e )
        {
            throw new ComponentInstantiationException( "Cannot build component for: " +
                                                       componentDescriptor.getComponentKey() +
                                                       "; unable to read BeanShell script", e );
        }

        return result;
    }
}
