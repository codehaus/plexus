package org.codehaus.plexus.cdc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentGleaner
{
    public static final String PLEXUS_COMPONENT_TAG = "plexus.component";

    // Qdox doesn't keep a map of short name to JavaClass
    private Map componentCache;

    public ComponentDescriptor glean( JavaDocBuilder builder, JavaClass javaClass )
    {
        String packageName = javaClass.getPackage();

        DocletTag tag = javaClass.getTagByName( PLEXUS_COMPONENT_TAG );

        if ( tag == null )
        {
            return null;
        }

        String className = javaClass.getName();

        boolean isManager = false;

        if ( className.endsWith( "Manager" ) )
        {
            isManager = true;
        }

        boolean isDefault = false;

        if ( className.startsWith( "Default" ) )
        {
            isDefault = true;
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        String version = tag.getNamedParameter( "version" );

        if ( version != null )
        {
            componentDescriptor.setVersion( version );
        }

        String role = tag.getNamedParameter( "role" );

        if ( role != null )
        {
            componentDescriptor.setRole( role );
        }
        else
        {
            if ( isManager )
            {
                // org.codehaus.foo.PluginManager
                // org.codehaus.foo.DefaultPluginManager

                componentDescriptor.setRole( packageName + "." + javaClass.getName().substring( 7 ) );
            }
            else if ( isDefault )
            {
                componentDescriptor.setRole( packageName + "." + javaClass.getName().substring( 7 ) );
            }
        }

        String roleHint = tag.getNamedParameter( "roleHint" );

        if ( roleHint != null )
        {
            componentDescriptor.setRoleHint( roleHint );
        }
        else
        {
            if ( !isManager && !isDefault )
            {
                findRoleHint( componentDescriptor, javaClass, javaClass.getName() );
            }
        }

        componentDescriptor.setImplementation( javaClass.getFullyQualifiedName() );

        DocletTag[] tags = javaClass.getTagsByName( "requirement" );

        if ( tags != null )
        {
            for ( int i = 0; i < tags.length; i++ )
            {
                ComponentRequirement requirement = new ComponentRequirement();

                String requirementRole = tag.getNamedParameter( "role" );

                requirement.setRole( requirementRole );

                componentDescriptor.addRequirement( requirement );
            }
        }

        // ----------------------------------------------------------------------
        // Handle the requirements for the manager
        // ----------------------------------------------------------------------

        if ( isManager )
        {
            // DefaultPluginManager
            //
            // So we need the goop between Default and Manager ... Plugin
            // So lookup the Plugin class and use that to create the Map
            // requirement.
            //
            //!! We need to look for the presence of the field, we might have
            // a manager that uses a container to lookup per lookup components.

            String s = className.substring( 7 );

            s = s.substring( 0, s.length() - 7 );

            JavaClass managed = findClassByName( builder, s );

            if ( managed != null )
            {
                ComponentRequirement requirement = new ComponentRequirement();

                requirement.setRole( managed.getFullyQualifiedName() );

                String fieldName = uncapitalise( managed.getName() );

                if ( fieldName.endsWith( "y" ) )
                {
                    fieldName = fieldName.substring( fieldName.length() - 1 ) + "ies";
                }
                else if ( fieldName.endsWith( "s" ) )
                {
                    fieldName = fieldName + "es";
                }
                else
                {
                    fieldName = fieldName + "s";
                }

                requirement.setFieldName( fieldName );

                componentDescriptor.addRequirement( requirement );
            }
            else
            {
                // The interface for what this manager is supposed to be managing
                // cannot be found which is not a good thing. There are warnings about
                // component interfaces not having a ROLE field.

                System.out.println( "Check the errors to make sure the interface of the managed class has a ROLE field." );

                System.out.println( "This may be why the interface could not be found because it was not recorded as a component interface." );
            }
        }

        findRequirements( javaClass, componentDescriptor, builder );

        return componentDescriptor;
    }

    private JavaClass findClassByName( JavaDocBuilder builder, String name )
    {
        // We are trying to cache classes that are components and
        // interfaces for components.
        //
        // Component classes are designated with the @component tag
        // Component interfaces contain a field named ROLE.

        if ( componentCache == null )
        {
            componentCache = new HashMap();

            JavaSource[] javaSources = builder.getSources();

            for ( int i = 0; i < javaSources.length; i++ )
            {
                JavaClass f = getJavaClass( javaSources[i] );

                if ( f.getTagByName( "component" ) != null )
                {
                    componentCache.put( f.getName(), f );
                }
                else if ( f.isInterface() )
                {
                    boolean hasRoleField = false;

                    JavaField[] fields = f.getFields();

                    for ( int j = 0; j < fields.length; j++ )
                    {
                        if ( fields[j].getName().equals( "ROLE" ) )
                        {
                            hasRoleField = true;

                            break;
                        }
                    }

                    if ( hasRoleField )
                    {
                        componentCache.put( f.getName(), f );
                    }
                    else
                    {
                        System.out.println( f.getFullyQualifiedName() + " is an interface but doesn't have a ROLE field!" );

                        System.out.println( "If this is a plexus component interface it should have a ROLE field." );
                    }
                }
            }
        }

        return (JavaClass) componentCache.get( name );
    }

    private void findRoleHint( ComponentDescriptor cd, JavaClass javaClass, String startingClassName )
    {
        String roleHint = null;

        String className = startingClassName;

        Type[] types = javaClass.getImplements();

        for ( int i = 0; i < types.length; i++ )
        {
            String interfaceName = types[i].getValue();

            String roleName = interfaceName.substring( interfaceName.lastIndexOf( "." ) + 1 );

            // org.codehaus.foo.Sink
            // org.codehaus.bar.BigSink

            if ( className.endsWith( roleName ) )
            {
                roleHint = className.substring( 0, className.length() - roleName.length() );

                roleHint = addAndDeHump( roleHint );

                cd.setRoleHint( roleHint );

                cd.setRole( interfaceName );
            }
        }

        // ----------------------------------------------------------------------
        // We back track up the hierarchy until we find an implementation that
        // matches the naming convention of the class in question. The default
        // plexus naming conventions are assumed.
        // ----------------------------------------------------------------------

        // org.codehaus.foo.Sink
        // ^
        // |
        // org.codehaus.foo.AbstractSink
        // ^
        // |
        // org.codehaus.foo.AbstractXhtmlSink
        // ^
        // |
        // org.codehaus.bar.CodehausXhtmlSink

        JavaClass jc = javaClass.getSuperJavaClass();

        if ( jc != null )
        {
            types = javaClass.getImplements();

            findRoleHint( cd, jc, startingClassName );
        }
    }

    private JavaClass getJavaClass( JavaSource javaSource )
    {
        return javaSource.getClasses()[0];
    }

    private String addAndDeHump( String view )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < view.length(); i++ )
        {
            if ( i != 0 &&
                Character.isUpperCase( view.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( view.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }

    private String uncapitalise( String str )
    {
        if ( str == null )
        {
            return null;
        }
        else if ( str.length() == 0 )
        {
            return "";
        }
        else
        {
            return new StringBuffer( str.length() )
                .append( Character.toLowerCase( str.charAt( 0 ) ) )
                .append( str.substring( 1 ) )
                .toString();
        }
    }

    private void findRequirements( JavaClass javaClass, ComponentDescriptor componentDescriptor,
                                   JavaDocBuilder builder )
    {
        JavaField[] fields = javaClass.getFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            if ( field.getTagByName( "ignore" ) != null )
            {
                continue;
            }

            String classType = field.getType().getValue();

            // If this is not a primitive field then we will attempt
            // to look for an interface without our set of sources
            // that is a component and try to set it as a requirement
            //
            // Right now we are not doing anything to search out cases
            // where the requirement is external. We will need to build
            // up a little database of some kind that can be queried for
            // all the available components. But this will do for now as
            // the requirement can be set explicity.

            String roleName = classType.substring( classType.lastIndexOf( "." ) + 1 );

            JavaClass jc = findClassByName( builder, roleName );

            if ( jc != null )
            {
                ComponentRequirement cr = new ComponentRequirement();

                cr.setRole( classType );

                componentDescriptor.addRequirement( cr );
            }
        }

        if ( javaClass.getSuperJavaClass() != null )
        {
            findRequirements( javaClass.getSuperJavaClass(), componentDescriptor, builder );
        }
    }
}
