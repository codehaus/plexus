package org.codehaus.plexus.component.factory.groovy;

import java.io.File;
import java.io.FileInputStream;

import groovy.lang.GroovyClassLoader;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.classworlds.ClassRealm;

public class GroovyComponentFactory
    extends AbstractComponentFactory
{
    private String groovyHome;

    private String groovyPath;

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        Object component = null;

        try
        {
            GroovyClassLoader gcl = new GroovyClassLoader();

            File f = new File( groovyPath, componentDescriptor.getImplementation() + ".groovy" );

            Class clazz = gcl.parseClass( new FileInputStream( f ), "SomeName.groovy");

            component = clazz.newInstance();
        }
        catch ( Exception e )
        {
            throw new ComponentInstantiationException( e.getMessage() );
        }

        return component;
    }
}
