package org.codehaus.plexus.cdc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
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
            ComponentDescriptor componentDescriptor = gleanComponent( javaSources[i] );

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

    private ComponentDescriptor gleanComponent( JavaSource javaSource )
    {
        JavaClass javaClass = getJavaClass( javaSource );

        DocletTag tag = javaClass.getTagByName( "component" );

        if ( tag == null )
        {
            return null;
        }

        String className = javaClass.getFullyQualifiedName();

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

                String packageName = javaClass.getPackage();

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

        return componentDescriptor;
    }

    private void findRoleHint( ComponentDescriptor cd, JavaClass javaClass, String startingClassName )
    {
        String roleHint = null;

        String className = startingClassName;

        System.out.println( "className = " + className );

        Type[] types = javaClass.getImplements();

        for ( int i = 0; i < types.length; i++ )
        {
            String interfaceName = types[i].getValue();

            String roleName = interfaceName.substring( interfaceName.lastIndexOf( "." ) + 1 );

            System.out.println( "roleName = " + roleName );

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
}
