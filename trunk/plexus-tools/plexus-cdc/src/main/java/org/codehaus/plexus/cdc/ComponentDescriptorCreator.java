package org.codehaus.plexus.cdc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.cdc.gleaner.ComponentGleaningStrategy;
import org.codehaus.plexus.cdc.gleaner.DefaultPlexusComponentGleaningStrategy;
import org.codehaus.plexus.cdc.gleaner.ImplComponentGleaningStrategy;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;

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
 *
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

            if ( componentDescriptor.getRole() != null )
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

    public void registerComponentGleaningStrategy( ComponentGleaningStrategy componentGleaningStrategy )
    {
        componentGleaningStrategies.add( componentGleaningStrategy );
    }

    // Private

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

        classLoader = new URLClassLoader( new URL[]{new File( basedir, "target/test-classes/" ).toURL(),
                                                    new File( basedir, "target/classes/" ).toURL()} );

        registerComponentGleaningStrategy( new DefaultPlexusComponentGleaningStrategy( classLoader ) );

        registerComponentGleaningStrategy( new ImplComponentGleaningStrategy() );
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
        ComponentDescriptor componentDescriptor = null;

        JavaClass javaClass = getJavaClass( javaSource );

        for ( Iterator i = componentGleaningStrategies.iterator(); i.hasNext(); )
        {
            ComponentGleaningStrategy strategy = (ComponentGleaningStrategy) i.next();

            componentDescriptor = strategy.gleanComponent( javaClass );

            if ( componentDescriptor.getRole() != null )
            {
                break;
            }
        }

        DocletTag tag;

        if ( componentDescriptor == null )
        {
            componentDescriptor = new ComponentDescriptor();
        }

        tag = javaClass.getTagByName( "component.version" );

        if ( tag != null )
        {
            componentDescriptor.setVersion( tag.getValue() );
        }

        tag = javaClass.getTagByName( "component.role" );

        if ( tag != null )
        {
            componentDescriptor.setRole( tag.getValue() );
        }

        tag = javaClass.getTagByName( "component.roleHint" );

        if ( tag != null )
        {
            componentDescriptor.setRoleHint( tag.getValue() );
        }

        DocletTag[] tags = javaClass.getTagsByName( "component.requirement" );

        if ( tag != null )
        {
            for ( int i = 0; i < tags.length; i++ )
            {
                ComponentRequirement requirement = new ComponentRequirement();

                requirement.setRole( tags[i].getValue() );

                componentDescriptor.addRequirement( requirement );
            }
        }

        return componentDescriptor;
    }

    private JavaClass getJavaClass( JavaSource javaSource )
    {
        return javaSource.getClasses()[0];
    }
}
