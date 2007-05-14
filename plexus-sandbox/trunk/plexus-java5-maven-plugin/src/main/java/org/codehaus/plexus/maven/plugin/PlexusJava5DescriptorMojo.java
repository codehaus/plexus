/*
 * The MIT License
 *
 * Copyright (c) 2007, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.codehaus.plexus.maven.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.cdc.ComponentDescriptorCreatorException;
import org.codehaus.plexus.cdc.ComponentDescriptorWriter;
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
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.outputDirectory}
     */
    private File classesDirectory;

    /**
     * @parameter expression="${project.compileClasspathElements}
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * @parameter expression="${component.org.codehaus.plexus.cdc.ComponentDescriptorWriter}"
     * @readonly
     */
    private ComponentDescriptorWriter writer;

    /**
     * @parameter
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project.build.directory}/generated-resources/plexus-cdc/"
     */
    private File outputDirectory;

    /**
     * @parameter expression="META-INF/plexus/components.xml"
     * @required
     */
    private String fileName;

    public void execute()
        throws MojoExecutionException
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

        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( "URLS: \n" + urls.toString().replaceAll( ",", "\n  " ) );
        }

        ClassLoader cl = new URLClassLoader( urls.toArray( new URL[urls.size()] ), getClass().getClassLoader() );

        ComponentSetDescriptor cset = new ComponentSetDescriptor();

        getLog().debug( "Scanning " + scanner.getIncludedFiles().length + " classes" );

        for ( String file : scanner.getIncludedFiles() )
        {
            ComponentDescriptor desc = scan( cl, file
                .substring( 0, file.lastIndexOf( ".class" ) ).replace( '\\', '.' ).replace( '/', '.' ) );
            if ( desc != null )
            {
                cset.addComponentDescriptor( desc );
                getLog().info( "Found component " + desc.getImplementation() );
            }
        }

        Resource resource = new Resource();
        resource.setDirectory( outputDirectory.getAbsolutePath() );
        resource.setIncludes( Collections.EMPTY_LIST );
        resource.setExcludes( Collections.EMPTY_LIST );

        project.addResource( resource );

        File outputFile = new File( outputDirectory, fileName );

        if ( !outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs() )
        {
            throw new MojoExecutionException( "Cannot create directory " + outputFile.getParent() );
        }

        try
        {
            writer.writeDescriptorSet( new FileWriter( outputFile ), cset, containerDescriptor );
        }
        catch ( ComponentDescriptorCreatorException e )
        {
            throw new MojoExecutionException( "Error while writing descriptor", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "IO error while writing descriptor", e );
        }
    }

    private ComponentDescriptor scan( ClassLoader cl, String className )
        throws MojoExecutionException
    {
        Class<?> c;

        try
        {
            c = cl.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "Error scanning class " + className, e );
        }

        Component componentAnnotation = c.getAnnotation( Component.class );
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

            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "  Component found: " + desc.getHumanReadableKey() );
            }

            for ( Class<?> cur = c; !cur.isAssignableFrom( Object.class ); cur = cur.getSuperclass() )
            {
                scan( cur, desc );
            }

            return desc;
        }
        else
        {
            getLog().debug( "  Not a component: " + c.getName() );
            return null;
        }
    }

    private void scan( Class<?> cur, ComponentDescriptor desc )
    {
        for ( Field f : cur.getDeclaredFields() )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "    Scanning field " + f );
            }

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

                if ( reqAnnotation.role().isAssignableFrom( Object.class ) )
                {
                    req.setRole( f.getType().getName() );
                }
                else
                {
                    req.setRole( reqAnnotation.role().getName() );
                }
                req.setRoleHint( reqAnnotation.hint() );
                req.setFieldName( f.getName() );
                req.setFieldMappingType( f.getType().getName() );

                XmlPlexusConfiguration reqConfig = new XmlPlexusConfiguration( "configuration" );

                for ( Configuration config : reqAnnotation.configuration() )
                {
                    XmlPlexusConfiguration c = new XmlPlexusConfiguration( config.key() );
                    String value = "";
                    for ( String v : config.value() )
                    {
                        value += ( value.length() == 0 ? "" : "," ) + v;
                    }
                    c.setValue( value );

                    reqConfig.addChild( c );
                }

                // TODO: currently plexus does not support configuring requirements, AFAIK.
                // Perhaps redeclare the component here and add configuration? For now,
                // that's impossible because we don't know the implementation..
                // What I'd like to do is:
                // req.setConfiguration( reqConfig );

                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "      Requirement: " + req.getHumanReadableKey() );
                }

            }
        }
    }
}
