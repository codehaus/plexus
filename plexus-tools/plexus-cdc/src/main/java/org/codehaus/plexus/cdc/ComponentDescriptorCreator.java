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
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.cdc.gleaner.ComponentGleaningStrategy;
import org.codehaus.plexus.cdc.gleaner.DefaultPlexusComponentGleaningStrategy;
import org.codehaus.plexus.cdc.gleaner.ImplComponentGleaningStrategy;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSet;
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

    private Properties properties = new Properties();

    private String destDir;

    private JavaSource[] javaSources;

    private List componentGleaningStrategies;

    private ClassLoader classLoader;

    private String classPath;

    public ComponentDescriptorCreator()
    {
        componentGleaningStrategies = new ArrayList();
    }

    public void setBasedir( String basedir )
    {
        this.basedir = basedir;
    }

    // ${project.properties} is a hashmap in maven
    public void setProperties( Map map )
    {
        if ( properties != null )
        {
            properties = new Properties();

            properties.putAll( map );
        }
    }

    public void setDestDir( String destDir )
    {
        this.destDir = destDir;
    }

    public void setClassPath( String classPath )
    {
        this.classPath = classPath;
    }

    public void execute()
        throws Exception
    {
        initialize();

        MavenModelParser mavenModelParser = new MavenModelParser( new File( basedir, "project.xml"), properties );

        List componentDependencies = convertDependencies( mavenModelParser.getDependencies() );

        JavaDocBuilder builder = new JavaDocBuilder();

        String sourceDirectoryName = mavenModelParser.getSourceDirectory();

        if ( sourceDirectoryName != null )
        {
            File sourceDirectory = new File( basedir, sourceDirectoryName );
    
            if ( !sourceDirectory.isDirectory() )
            {
                builder.addSourceTree( sourceDirectory );
            }
            else
            {
                System.err.println( "<sourceDirectory> must be a directory." );
            }
        }
        else
        {
            System.err.println( "Missing <sourceDirectory> element." );
        }

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

        ComponentSet componentSet = new ComponentSet();

        componentSet.setComponents( componentDescriptors );

        componentSet.setDependencies( componentDependencies );

        PlexusXStream xstream = new PlexusXStream();

        String components = xstream.toXML( componentSet );

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

        FileWriter writer = new FileWriter( new File( f, "components.xml" ) );

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
        classLoader = new URLClassLoader( new URL[]{new File( "target/test-classes/" ).toURL(),
                                                    new File( "target/classes/" ).toURL()} );

        registerComponentGleaningStrategy( new DefaultPlexusComponentGleaningStrategy( classLoader ) );

        registerComponentGleaningStrategy( new ImplComponentGleaningStrategy() );
    }

    private List convertDependencies( List dependencies )
    {
        List componentDependencies = new ArrayList();

        for ( Iterator i = dependencies.iterator(); i.hasNext(); )
        {
            MavenModelParser.Dependency d = (MavenModelParser.Dependency) i.next();

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

    public static void main( String[] args )
        throws Exception
    {
        String basedir = args[0];

        ComponentDescriptorCreator cdc = new ComponentDescriptorCreator();

        cdc.setBasedir( basedir );

        cdc.execute();
    }
}

