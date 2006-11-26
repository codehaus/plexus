package org.codehaus.plexus.component.factory.jython;

import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.util.Properties;

public class JythonComponentFactory
    extends AbstractComponentFactory
    implements ComponentFactory
{
    // configuration

    private String pythonHome;

    private String pythonPath;

    private String pythonCachedir;

    // members

    private PySystemState pythonSystemState;

    private Properties pythonSystemStateProperties;

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm realm, PlexusContainer container )
        throws ComponentInstantiationException

    {
        Object component = null;

        if ( pythonHome == null )
            throw new ComponentInstantiationException( "Missing configuration element: 'python-home'" );

        if ( pythonPath == null )
            throw new ComponentInstantiationException( "Missing configuration element: 'python-path'" );

        if ( pythonCachedir == null )
            throw new ComponentInstantiationException( "Missing configuration element: 'python-cachedir'" );

        try
        {
            pythonSystemStateProperties = new Properties();

            pythonSystemStateProperties.setProperty( "python.home", pythonHome );

            pythonSystemStateProperties.setProperty( "python.path", pythonPath );

            pythonSystemStateProperties.setProperty( "python.cachedir", pythonCachedir );

            // This is just the way jython is, don't look at me. You cannot
            // create an instance of the system state without initializing first.
            PySystemState.initialize( pythonSystemStateProperties, null, null );

            pythonSystemState = new PySystemState();

            pythonSystemState.initialize( pythonSystemStateProperties, null, null );

            pythonSystemState.setClassLoader( realm.getClassLoader() );

            Py.setSystemState( pythonSystemState );

            PythonInterpreter interp = new PythonInterpreter( null, pythonSystemState );

            String name = componentDescriptor.getImplementation() + ".py";

            interp.execfile( new File( pythonPath, name  ).getPath() );

            try
            {
                // We create an instance of the screen class from the
                // python script
                interp.exec( "scr = " + componentDescriptor.getImplementation() + "()" );
            }
            catch ( Throwable e )
            {
                throw new Exception( "Cannot create " + name, e );
            }

            // Here we convert the python sceen instance to a java instance.
            component = interp.get( "scr", Object.class );
        }
        catch ( Exception e )
        {
            throw new ComponentInstantiationException( "Cannot instantiate jython component: ", e );
        }

        return component;
    }
}
