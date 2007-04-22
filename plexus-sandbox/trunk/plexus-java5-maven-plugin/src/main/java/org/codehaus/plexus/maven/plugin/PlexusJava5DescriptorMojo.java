package org.codehaus.plexus.maven.plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Parameter;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.codehaus.plexus.component.repository.cdc.ComponentRequirement;
import org.codehaus.plexus.component.repository.cdc.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @phase process-classes
 * @goal descriptor
 * @requiresDependencyResolution compile
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
public class PlexusJava5DescriptorMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project.build.outputDirectory}
     */
    private File classesDirectory;

    /**
     * @parameter expression="${project.compileClasspathElements}
     */
    private List<String> classpathElements;

    public void execute()
        throws MojoExecutionException,
            MojoFailureException
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( classesDirectory );
        scanner.setIncludes( new String[] { "**/*.class" } );
        scanner.scan();

        List<URL> urls = new ArrayList<URL>();
        for ( String cpe : classpathElements )
        {
            try
            {
                urls.add( new File( cpe ).toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                getLog().warn( "Cannot convert '" + cpe + "' to URL", e );
            }
        }

        getLog().info( "URLS: \n" + urls.toString().replaceAll( ",", "\n  " ) );
        ClassLoader cl = new URLClassLoader( urls.toArray( new URL[urls.size()] ), getClass().getClassLoader() );

        ComponentSetDescriptor cset = new ComponentSetDescriptor();
        getLog().info( "Scanning " + scanner.getIncludedFiles().length + " classes" );
        for ( String file : scanner.getIncludedFiles() )
        {
            ComponentDescriptor desc = scan( cl, file.substring( 0, file.lastIndexOf( ".class" ) ).replace( '/', '.' ) );
            if ( desc != null )
            {
                cset.addComponentDescriptor( desc );
                getLog().info( "Found component " + desc.getHumanReadableKey() );
            }
        }

        // TODO: write the descriptorset

    }

    private ComponentDescriptor scan( ClassLoader cl, String className )
        throws MojoExecutionException
    {
        Class c;

        try
        {
            c = cl.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "Error scanning class " + className, e );
        }

        getLog().info( "Scanning class " + c.getName() );

        Component componentAnnotation = (Component) c.getAnnotation( Component.class );
        if ( componentAnnotation != null )
        {
            ComponentDescriptor desc = new ComponentDescriptor();
            desc.setImplementation( c.getName() );
            desc.setRole( componentAnnotation.role().getName() );
            desc.setRoleHint( componentAnnotation.hint() );
            desc.setLifecycleHandler( componentAnnotation.lifecycleHandler() );
            desc.setAlias( componentAnnotation.alias() );
            desc.setInstantiationStrategy( componentAnnotation.instantiationStrategy().name().toLowerCase().replace(
                '_',
                '-' ) );
            desc.setComponentType( "plexus" );

            desc.setConfiguration( new XmlPlexusConfiguration( "configuration" ) );

            Class cur = c;
            while ( !Object.class.isAssignableFrom( cur ) )
            {
                scan( cur, desc );
                cur = cur.getSuperclass();
            }

            getLog().info( "  Component found: " + desc.getHumanReadableKey() );

            return desc;
        }
        else
        {
            getLog().info( "  Not a component" );
            return null;
        }
    }

    private void scan( Class cur, ComponentDescriptor desc )
    {
        for ( Field f : cur.getDeclaredFields() )
        {
            Parameter paramAnnotation = f.getAnnotation( Parameter.class );

            if ( paramAnnotation != null )
            {
                String value = "";
                for ( String s : paramAnnotation.value() )
                {
                    value += ( value.length() == 0 ? "" : "," ) + s;
                }

                XmlPlexusConfiguration config = new XmlPlexusConfiguration( f.getName() );
                config.setValue( value );
                desc.getConfiguration().addChild( config );
            }

            Requirement reqAnnotation = f.getAnnotation( Requirement.class );
            if ( reqAnnotation != null )
            {
                ComponentRequirement req = new ComponentRequirement();
                desc.addRequirement( req );

                req.setRole( reqAnnotation.role().getName() );
                req.setRoleHint( reqAnnotation.hint() );
                req.setFieldName( f.getName() );
                req.setFieldMappingType( f.getType().getName() );

                for ( Configuration config : reqAnnotation.configuration() )
                {
                    // TODO: currently plexus does not support configuring requirements.
                }
            }
        }
    }
}
