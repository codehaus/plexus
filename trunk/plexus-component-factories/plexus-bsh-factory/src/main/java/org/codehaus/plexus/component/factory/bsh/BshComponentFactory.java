package org.codehaus.plexus.component.factory.bsh;

import bsh.Interpreter;

import java.io.File;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.classworlds.ClassRealm;


public class BshComponentFactory
    extends AbstractComponentFactory
{
    private String bshHome;

    private String bshPath;

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm realm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        Object component = null;

        try
        {
            Interpreter interp = new Interpreter();

            String name =  componentDescriptor.getImplementation() + ".bsh";
                
            component = interp.source( new File( bshPath, name  ).getPath() );
        }
        catch ( Exception e )
        {
            throw new ComponentInstantiationException( e.getMessage() );
        }

        return component;
    }
}
