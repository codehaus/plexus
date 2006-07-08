package org.codehaus.plexus.component.repository.synthesizer;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentConfigurationDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentConfigurationFieldDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.ReflectionUtils;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * TODO: This synthesizer only knows how to create a descriptor from a Java class. Should possibly handle bsh, jython etc.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id:$
 */
public class DefaultComponentConfigurationDescriptorSynthesizer
    extends AbstractLogEnabled
    implements ComponentConfigurationDescriptorSynthesizer
{
    // ----------------------------------------------------------------------
    // ComponentConfigurationDescriptorSynthesizer Implementation
    // ----------------------------------------------------------------------

    public ComponentConfigurationDescriptor synthesizeDescriptor( ClassRealm classRealm,
                                                                  ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException, ComponentImplementationNotFoundException
    {
        classRealm.display();

        ComponentConfigurationDescriptor descriptor = new ComponentConfigurationDescriptor();

        descriptor.setFields( new ArrayList() );

        // ----------------------------------------------------------------------
        // Load the class to do the reflection magic on
        // ----------------------------------------------------------------------

        String implementation = componentDescriptor.getImplementation();

        if ( StringUtils.isEmpty( implementation ) )
        {
            return null;
        }

        ClassLoader cl = classRealm.getClassLoader();

        Class klass = loadClass( cl, implementation );

        // ----------------------------------------------------------------------
        // Find all setters
        // ----------------------------------------------------------------------

        List setters = ReflectionUtils.getSetters( klass );

        for ( Iterator it = setters.iterator(); it.hasNext(); )
        {
            Method method = (Method) it.next();

            ComponentConfigurationFieldDescriptor fieldDescriptor = new ComponentConfigurationFieldDescriptor();

            String name = method.getName().substring( 3 );
            name = StringUtils.lowercaseFirstLetter( name );
            fieldDescriptor.setName( name );

            fieldDescriptor.setInjectionMethod( "setter" );

            Class setterType = ReflectionUtils.getSetterType( method );
            fieldDescriptor.setType( setterType.getPackage() + "." + setterType.getName() );

            descriptor.getFields().add( fieldDescriptor );
        }

        // ----------------------------------------------------------------------
        // Find all private fields
        // ----------------------------------------------------------------------

        List fields = ReflectionUtils.getFieldsIncludingSuperclasses( klass );

        for ( Iterator it = fields.iterator(); it.hasNext(); )
        {
            Field field = (Field) it.next();

            ComponentConfigurationFieldDescriptor fieldDescriptor = new ComponentConfigurationFieldDescriptor();

            fieldDescriptor.setName( field.getName() );

            fieldDescriptor.setInjectionMethod( "private" );

            fieldDescriptor.setType( field.getClass().getName() );

            descriptor.getFields().add( fieldDescriptor );
        }

        return descriptor;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Class loadClass( ClassLoader cl, String implementation )
        throws ComponentImplementationNotFoundException
    {
        try
        {
            return cl.loadClass( implementation );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ComponentImplementationNotFoundException( implementation );
        }
    }
}
