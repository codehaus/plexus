package org.codehaus.plexus.cdc.gleaner;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.cdc.gleaner.ComponentGleaningStrategy;

/**
 * role = Foo
 * implementatin = FooImpl
 *
 * public class FooImpl
 *     implements Foo
 * {
 * }
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ImplComponentGleaningStrategy
    implements ComponentGleaningStrategy
{
    public ComponentDescriptor gleanComponent( JavaClass javaClass )
    {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        String className = javaClass.getName();

        // Consider the use of the word Mock for in use classes
        // for mock testing so they aren't considered components for
        // use as per usual within the container.

        if ( className.indexOf( "Mock" ) >= 0 )
        {
            return componentDescriptor;
        }

        Type[] types = javaClass.getImplements();

        for ( int i = 0; i < types.length; i++ )
        {
            String  role = types[i].getValue();

            String roleName = role.substring( role.lastIndexOf( "." ) + 1 );

            if ( className.equals( roleName + "Impl" ) )
            {
                componentDescriptor.setRole( role );

                componentDescriptor.setImplementation( javaClass.getFullyQualifiedName() );

                break;
            }
        }

        return componentDescriptor;
    }
}
