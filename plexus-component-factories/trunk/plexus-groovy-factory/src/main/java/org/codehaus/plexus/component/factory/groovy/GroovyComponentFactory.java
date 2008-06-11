package org.codehaus.plexus.component.factory.groovy;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyResourceLoader;

import java.net.URL;

/**
 * Factory for loading components implemented in <a href="http://groovy.codehaus.org">Groovy</a>.
 *
 * @version $Id$
 */
public class GroovyComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance(ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container)
        throws ComponentInstantiationException
    {
        assert componentDescriptor != null;
        assert classRealm != null;

        try {
            return loadGroovyObject(componentDescriptor.getImplementation(), classRealm);
        }
        catch (Exception e) {
            throw new ComponentInstantiationException("Failed to create Groovy component: " + e.getMessage(), e);
        }
    }

    public GroovyObject loadGroovyObject(final GroovyCodeSource source, final ClassLoader classLoader, final GroovyResourceLoader resourceLoader)
        throws Exception
    {
        assert source != null;
        assert classLoader != null;
        assert resourceLoader != null;

        GroovyClassLoader loader = new GroovyClassLoader(classLoader);

        // Set the resource loader to allow peers to be loaded
        loader.setResourceLoader(resourceLoader);

        Class type = loader.parseClass(source);
        return (GroovyObject)type.newInstance();
    }

    public GroovyObject loadGroovyObject(final String className, final ClassLoader classLoader, final GroovyResourceLoader resourceLoader)
        throws Exception
    {
        assert className != null;
        assert classLoader != null;
        assert resourceLoader != null;

        URL source = resourceLoader.loadGroovySource(className);
        if (source == null) {
            throw new ComponentInstantiationException("Missing source for: " + className);
        }

        return loadGroovyObject(new GroovyCodeSource(source), classLoader, resourceLoader);
    }

    public GroovyObject loadGroovyObject(final String className, final ClassLoader classLoader) throws Exception {
        assert className != null;
        assert classLoader != null;

        return loadGroovyObject(className, classLoader, new GroovyResourceLoaderImpl(classLoader));
    }

    public String getId()
    {
        return "groovy";
    }
}
