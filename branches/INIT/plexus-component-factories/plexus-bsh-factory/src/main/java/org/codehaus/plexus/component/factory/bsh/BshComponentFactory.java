package org.codehaus.plexus.component.factory.bsh;

import bsh.Interpreter;

import java.io.File;


import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class BshComponentFactory
    extends AbstractComponentFactory
{
    private String bshHome;

    private String bshPath;

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassLoader classLoader )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
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
            throw new InstantiationException( e.getMessage() );
        }

        return component;
    }
}
