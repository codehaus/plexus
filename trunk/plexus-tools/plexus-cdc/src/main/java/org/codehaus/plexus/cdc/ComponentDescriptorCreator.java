package org.codehaus.plexus.cdc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.JavaField;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * So, in this case it is easy enough to determine the role and the implementation.
 * We could also employ some secondary checks like looking for particular super classes
 * or whatever. We can always use the @tags to be explicit but in most cases we can
 * probably determine the correct component descriptor without requiring @tags.
 *
 * @todo glean configuration information from types of the parameters but also
 * allow OCL type constraints for validation. We'll hook in something simple like
 * regex as for most cases I think some simple regex could catch most problems. I
 * don't want to have to use MSV or something like that which which triple the size
 * of a deployment.
 * <p/>
 * This is for a single project with a single POM, multiple components
 * with all deps in the POM.
 */
public class ComponentDescriptorCreator
{
    private String basedir;

    private String destDir;

    private JavaSource[] javaSources;

    private List componentGleaningStrategies;

    private ClassLoader classLoader;

    private String classPath;

    private MavenProject mavenProject;

    public ComponentDescriptorCreator()
    {
        componentGleaningStrategies = new ArrayList();
    }

    public void setBasedir( String basedir )
    {
        this.basedir = basedir;
    }

    public void setDestDir( String destDir )
    {
        this.destDir = destDir;
    }

    public void setProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    public void execute()
        throws Exception
    {
        initialize();

        List componentDependencies = convertDependencies( mavenProject.getDependencies() );

        JavaDocBuilder builder = new JavaDocBuilder();

        String sourceDirectoryName = mavenProject.getBuild().getSourceDirectory();

        if ( sourceDirectoryName == null )
        {
            System.err.println( "The source directory must be set." );

            return;
        }

        File sourceDirectory = new File( sourceDirectoryName );

        if ( !sourceDirectory.isDirectory() )
        {
            System.err.println( "The source directory must be a directory." );

            return;
        }

        builder.addSourceTree( sourceDirectory );

        javaSources = builder.getSources();

        List componentDescriptors = new ArrayList();

        for ( int i = 0; i < javaSources.length; i++ )
        {
            ComponentDescriptor componentDescriptor = gleanComponent( builder, javaSources[i] );

            if ( componentDescriptor != null )
            {
                componentDescriptors.add( componentDescriptor );
            }
        }

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        componentSetDescriptor.setComponents( componentDescriptors );

        componentSetDescriptor.setDependencies( componentDependencies );

        PlexusXStream xstream = new PlexusXStream();

        String components = xstream.toXML( componentSetDescriptor );

        File f;

        if ( destDir != null )
        {
            f = new File( destDir );
        }
        else
        {
            f = new File( new File( new File( new File( basedir, "target" ), "classes" ), "META-INF" ), "plexus" );
        }

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File outputFile = new File( f, "components.xml" );

        if ( !outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs() )
        {
            throw new Exception( "Could not create parent directories for " + outputFile.getPath() );
        }

        System.out.println( "Writing component descriptor to " + outputFile.getPath() );

        FileWriter writer = new FileWriter( outputFile );

        writer.write( components );

        writer.close();
    }

    private void initialize()
        throws Exception
    {
        if ( mavenProject == null )
        {
            throw new Exception( "The project must be set." );
        }

        if ( basedir == null )
        {
            basedir = mavenProject.getBasedir().getAbsolutePath();
        }
    }

    private List convertDependencies( List dependencies )
    {
        List componentDependencies = new ArrayList();

        for ( Iterator i = dependencies.iterator(); i.hasNext(); )
        {
            Dependency d = (Dependency) i.next();

            ComponentDependency cd = new ComponentDependency();

            cd.setGroupId( d.getGroupId() );

            cd.setArtifactId( d.getArtifactId() );

            cd.setVersion( d.getVersion() );

            componentDependencies.add( cd );
        }

        return componentDependencies;
    }

    private ComponentDescriptor gleanComponent( JavaDocBuilder builder, JavaSource javaSource )
    {
        JavaClass javaClass = getJavaClass( javaSource );

        String packageName = javaClass.getPackage();

        DocletTag tag = javaClass.getTagByName( "component" );

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

        if ( tag != null )
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

        JavaField[] fields = javaClass.getFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[i];

            String classType = field.getType().getValue();

            if ( !isPrimitive( classType ) )
            {
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

                JavaClass jc = (JavaClass) componentCache.get( roleName );

                if ( jc != null )
                {
                    ComponentRequirement cr = new ComponentRequirement();

                    cr.setRole( classType );

                    componentDescriptor.addRequirement( cr );
                }
            }
        }

        return componentDescriptor;
    }

    // Qdox doesn't keep a map of short name to JavaClass
    private Map componentCache;

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

    private Class[] primitiveClasses = new Class[]{
        String.class,
        Boolean.class, Boolean.TYPE,
        Byte.class, Byte.TYPE,
        Character.class, Character.TYPE,
        Short.class, Short.TYPE,
        Integer.class, Integer.TYPE,
        Long.class, Long.TYPE,
        Float.class, Float.TYPE,
        Double.class, Double.TYPE,
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
}
