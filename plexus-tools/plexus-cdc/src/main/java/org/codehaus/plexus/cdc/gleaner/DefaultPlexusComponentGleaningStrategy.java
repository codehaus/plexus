package org.codehaus.plexus.cdc.gleaner;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;

/**
 * role = Foo
 * implementatin = DefaultFoo
 *
 * public class DefaultFoo
 *     implements Foo
 * {
 * }
 *
 * role = Valve
 * implementation = FirstValve
 * role-hint = first
 *
 * public class FirstValve
 *     implements Valve
 * {
 * }
 *
 * role = Valve
 * imlementation = SecondValve
 * role-hint = second
 *
 * public class SceondValve
 *     implements Valve
 * {
 * }
 *
 * role = Valve
 * implementation = UserAddAction
 * role-hint = add-user
 *
 * public class AddUserAction
 *     implements Action
 * {
 * }
 *
 * role = ComponentA
 * implementation = DefaultComponentA
 * requirement = ComponentB
 * requirement = ComponentC
 *
 * public class DefaultComponentA
 *     implements ComponentA
 * {
 *     private ComponentB componentB;
 *
 *     private ComponentC componentC;
 *
 *     \** @default 10000 *\
 *     private int port;
 *
 *     \** @default localhost *\
 *     private int host;
 * }
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 *
 * @todo use OCL or some other simple language for configuration constraints.
 */
public class DefaultPlexusComponentGleaningStrategy
    implements ComponentGleaningStrategy
{
    private ClassLoader classLoader;

    public DefaultPlexusComponentGleaningStrategy( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public ComponentDescriptor gleanComponent( JavaClass javaClass )
    {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "configuration" );

        String className = javaClass.getName();

        // Consider the use of the word Mock for in use classes
        // for mock testing so they aren't considered components for
        // use as per usual within the container.

        if ( className.indexOf( "Mock" ) >= 0 )
        {
            return componentDescriptor;
        }

        if ( className.startsWith( "Abstract" ) || javaClass.isAbstract() )
        {
            return componentDescriptor;
        }

        Type[] types = javaClass.getImplements();

        for ( int i = 0; i < types.length; i++ )
        {
            String role = types[i].getValue();

            String roleName = role.substring( role.lastIndexOf( "." ) + 1 );

            if ( className.equals( "Default" + roleName ) )
            {
                componentDescriptor.setRole( role );

                componentDescriptor.setImplementation( javaClass.getFullyQualifiedName() );

                break;
            }
            else if ( className.endsWith( roleName ) )
            {
                componentDescriptor.setRole( role );

                componentDescriptor.setImplementation( javaClass.getFullyQualifiedName() );

                String roleHint = className.substring( 0, className.length() - roleName.length() );

                roleHint = addAndDeHump( roleHint );

                componentDescriptor.setRoleHint( roleHint );

                break;
            }
        }

        if ( componentDescriptor.getRole() == null )
        {
            return componentDescriptor;
        }

        // Look for requirements

        JavaField[] fields = javaClass.getFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            String classType = field.getType().getValue();

            if ( isPrimitive( classType ) )
            {
                DocletTag defaultValueTag = field.getTagByName( "default" );

                if ( defaultValueTag != null )
                {
                    XmlPlexusConfiguration defaultValue = new XmlPlexusConfiguration( field.getName() );

                    defaultValue.setValue( defaultValueTag.getValue() );

                    configuration.addChild( defaultValue );
                }
            }
            else
            {
                Class clazz = loadClass( classType );

                if ( clazz.isInterface() )
                {
                    ComponentRequirement requirement = new ComponentRequirement();

                    requirement.setRole( classType );

                    componentDescriptor.addRequirement( requirement );
                }
            }
/*
            else
            {
                // See if we can get away without compiling.

                ComponentRequirement requirement = new ComponentRequirement();

                requirement.setRole( classType );

                componentDescriptor.addRequirement( requirement );
            }
*/
        }

        /*
        if ( configuration.getChildCount() > 0 )
        {
            componentDescriptor.setConfiguration( configuration );
        }
        */

        return componentDescriptor;
    }

    private Class[] primitiveClasses = new Class[] {
        String.class,
        Boolean.class,   Boolean.TYPE,
        Byte.class,      Byte.TYPE,
        Character.class, Character.TYPE,
        Short.class,     Short.TYPE,
        Integer.class,   Integer.TYPE,
        Long.class,      Long.TYPE,
        Float.class,     Float.TYPE,
        Double.class,    Double.TYPE,
    };

    private boolean isPrimitive( String type )
    {
        for ( int i = 0; i < primitiveClasses.length; i++ )
        {
            if ( type.equals( primitiveClasses[i].getName() ) )
            {
                return true;
            }
        }

        return false;
    }

    private String addAndDeHump( String view )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < view.length(); i++ )
        {
            if ( i != 0 && Character.isUpperCase( view.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( view.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }

    private Class loadClass( String className )
    {
        try
        {
            return classLoader.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            return null;
        }
    }
}
