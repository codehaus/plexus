package org.codehaus.plexus.cdc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//!! Manager might not use a Map. Could use a container for per-lookup
//!! Need to walk up through super classes looking for requirements
//!! Need a database of all components as everything is not always available
//   in the current source tree.

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

    private MavenProject mavenProject;

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

        ComponentGleaner gleaner = new ComponentGleaner();

        for ( int i = 0; i < javaSources.length; i++ )
        {
            JavaClass javaClass = getJavaClass( javaSources[i] );

            ComponentDescriptor componentDescriptor = gleaner.glean( builder, javaClass );

            if ( componentDescriptor != null && !javaClass.isAbstract() )
            {
                componentDescriptors.add( componentDescriptor );
            }
        }

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        componentSetDescriptor.setComponents( componentDescriptors );

        componentSetDescriptor.setDependencies( componentDependencies );

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

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        FileWriter writer = new FileWriter( outputFile );

        XMLWriter w = new PrettyPrintXMLWriter( writer );

        w.startElement( "component-set" );

        w.startElement( "components" );

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            w.startElement( "component" );

            ComponentDescriptor cd = (ComponentDescriptor) i.next();

            element( w, "role", cd.getRole() );

            element( w, "role-hint", cd.getRoleHint() );

            element( w, "implementation", cd.getImplementation() );

            element( w, "instantiation-strategy", cd.getInstantiationStrategy() );

            // ----------------------------------------------------------------------
            // Configuration
            // ----------------------------------------------------------------------


            // ----------------------------------------------------------------------
            // Requirements
            // ----------------------------------------------------------------------

            if ( cd.getRequirements() != null && cd.getRequirements().size() != 0 )
            {
                w.startElement( "requirements" );

                for ( Iterator j = cd.getRequirements().iterator(); j.hasNext(); )
                {
                    ComponentRequirement cr = (ComponentRequirement) j.next();

                    w.startElement( "requirement" );

                    element( w, "role", cr.getRole() );

                    element( w, "role-hint", cr.getRoleHint() );

                    element( w, "field-name", cr.getFieldName() );

                    w.endElement();
                }

                w.endElement();
            }

            w.endElement();
        }

        w.endElement();

        writeDependencies( w, componentSetDescriptor );

        w.endElement();

        writer.close();

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

    }

    public void writeDependencies( XMLWriter w, ComponentSetDescriptor pomDom )
        throws Exception
    {

        w.startElement( "dependencies" );

        List deps = pomDom.getDependencies();

        if ( deps != null )
        {
            for ( int i = 0; i < deps.size(); i++ )
            {
                writeDependencyElement( (ComponentDependency) deps.get( i ), w );
            }
        }

        w.endElement();
    }

    private void writeDependencyElement( ComponentDependency dependency, XMLWriter w )
        throws Exception
    {
        w.startElement( "dependency" );

        String groupId = dependency.getGroupId();

        if ( groupId == null )
        {
            throw new Exception( "Missing dependency: 'groupId'." );
        }

        element( w, "groupId", groupId );

        String artifactId = dependency.getArtifactId();

        if ( artifactId == null )
        {
            throw new Exception( "Missing dependency: 'artifactId'." );
        }

        element( w, "artifactId", artifactId );

        String type = dependency.getType();

        if ( type != null )
        {
            element( w, "type", type );
        }

        String version = dependency.getVersion();

        if ( version == null )
        {
            throw new Exception( "Missing dependency: 'version'." );
        }

        element( w, "version", version );

        w.endElement();
    }

    public void element( XMLWriter w, String name, String value )
    {
        if ( value == null )
        {
            return;
        }

        w.startElement( name );

        w.writeText( value );

        w.endElement();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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

    private JavaClass getJavaClass( JavaSource javaSource )
    {
        return javaSource.getClasses()[0];
    }
}
