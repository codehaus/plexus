package org.codehaus.plexus.builder.runtime;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.codehaus.plexus.application.PlexusRuntimeConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class DefaultPlexusRuntimeBuilder
    extends AbstractBuilder
    implements PlexusRuntimeBuilder
{
    private static String JSW = "jsw";

    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    private final static String UNIX_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus.vm";

    private final static String WINDOWS_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus-bat.vm";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** @requirement */
    protected VelocityComponent velocity;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void build( File workingDirectory,
                       List remoteRepositories,
                       ArtifactRepository localRepository,
                       Set projectArtifacts,
                       File plexusConfiguration,
                       File configurationPropertiesFile )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            // ----------------------------------------------------------------------
            // Assert the parameters
            // ----------------------------------------------------------------------

            if ( workingDirectory == null )
            {
                throw new PlexusRuntimeBuilderException( "The output directory must be specified." );
            }

            if ( localRepository == null )
            {
                throw new PlexusRuntimeBuilderException( "The local Maven repository must be specified." );
            }

            if ( plexusConfiguration == null )
            {
                throw new PlexusRuntimeBuilderException( "The plexus configuration file must be set." );
            }

            if ( !plexusConfiguration.exists() )
            {
                throw new PlexusRuntimeBuilderException( "The specified plexus configuration file '" + plexusConfiguration.getAbsolutePath() + "' doesn't exist." );
            }

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            Properties configurationProperties = new Properties();

            if ( configurationPropertiesFile != null )
            {
                configurationProperties.load( new FileInputStream( configurationPropertiesFile ) );
            }

            // ----------------------------------------------------------------------
            // Find the artifact lists.
            // ----------------------------------------------------------------------

            Set bootArtifacts;

            Set coreArtifacts;

            try
            {
                bootArtifacts = getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, false );

                coreArtifacts = getCoreArtifacts( projectArtifacts, remoteRepositories, localRepository, false );
            }
            catch ( ArtifactResolutionException e )
            {
                throw new PlexusRuntimeBuilderException( "Could not resolve a artifact.", e );
            }

            // ----------------------------------------------------------------------
            // Build the runtime
            // ----------------------------------------------------------------------

            mkdir( workingDirectory );

            getLogger().info( "Building runtime in " + workingDirectory.getAbsolutePath() );

            // ----------------------------------------------------------------------
            // Set up the directory structure
            // ----------------------------------------------------------------------

            mkdir( getAppsDirectory( workingDirectory ) );

            File binDir = mkdir( new File( workingDirectory, PlexusRuntimeConstants.BIN_DIRECTORY ) );

            File confDir = mkdir( new File( workingDirectory, PlexusRuntimeConstants.CONF_DIRECTORY ) );

            File coreDir = mkdir( new File( workingDirectory, PlexusRuntimeConstants.CORE_DIRECTORY ) );

            File bootDir = mkdir( new File( workingDirectory, PlexusRuntimeConstants.BOOT_DIRECTORY ) );

            mkdir( new File( workingDirectory, PlexusRuntimeConstants.LOGS_DIRECTORY ) );

            mkdir( new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY ) );

            mkdir( new File( workingDirectory, PlexusRuntimeConstants.TEMP_DIRECTORY ) );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            copyArtifacts( workingDirectory, bootDir, bootArtifacts );

            copyArtifacts( workingDirectory, coreDir, coreArtifacts );

            // ----------------------------------------------------------------------
            // We need to separate between the container configuration that you want
            // shared amongst the apps and the application configuration.
            // ----------------------------------------------------------------------

            processMainConfiguration( plexusConfiguration, configurationProperties, confDir );

            createSystemScripts( binDir, confDir );

            //processConfigurations();

            javaServiceWrapper( binDir, coreDir, confDir, configurationProperties );

            executable( new File( binDir, "plexus.sh" ) );
        }
        catch ( PlexusRuntimeBuilderException ex )
        {
            throw ex;
        }
        catch ( CommandLineException ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
        catch ( IOException ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
    }

    public void bundle( File outputFile, File workingDirectory )
        throws PlexusRuntimeBuilderException
    {
        Archiver archiver = new JarArchiver();

        try
        {
            archiver.addDirectory( workingDirectory );

            archiver.setDestFile( outputFile );

            archiver.createArchive();
        }
        catch ( Exception e )
        {
            throw new PlexusRuntimeBuilderException( "Error while creating the archive.", e );
        }
    }

    public void addPlexusApplication( File plexusApplication, File runtimeDirectory )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            FileUtils.copyFileToDirectory( plexusApplication, getAppsDirectory( runtimeDirectory ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while copying the application into the runtime.", e );
        }
    }

    public void addPlexusService( File plexusService, File runtimeDirectory )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            File dir = getServicesDirectory( runtimeDirectory );

            String name = plexusService.getName();

            if ( !name.endsWith( ".jar" ) )
            {
                name = name.substring( 0, name.lastIndexOf( "." ) );

                name += ".jar";
            }

            FileUtils.copyFile( plexusService, new File( dir, name ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while copying the application into the runtime.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private File getAppsDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.APPLICATIONS_DIRECTORY);
    }

    private File getServicesDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void createSystemScripts( File binDir, File confDir )
        throws PlexusRuntimeBuilderException, IOException
    {
        ClassLoader old = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

        createClassworldsConfiguration( confDir );

        createLauncherScripts( binDir );

        Thread.currentThread().setContextClassLoader( old );
    }

    private void createClassworldsConfiguration( File confDir )
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( confDir, "classworlds.conf" ) );
    }

    private void createLauncherScripts( File binDir )
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( UNIX_LAUNCHER_TEMPLATE, new File( binDir, "plexus.sh" ) );

        mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( binDir, "plexus.bat" ) );
    }

    private void processMainConfiguration( File plexusConfiguration,
                                           Properties configurationProperties,
                                           File confDir )
        throws IOException
    {
        File out = new File( confDir, "plexus.xml" );

        filterCopy( plexusConfiguration, out, configurationProperties );
    }

    private void javaServiceWrapper( File binDir,
                                     File coreDir,
                                     File confDir,
                                     Properties configurationProperties )
        throws CommandLineException, IOException
    {
        ClassLoader cl = getClass().getClassLoader();

        String[] linux = new String[]
        {
            "linux/libwrapper.so|x",
            "linux/run.sh|x",
            "linux/wrapper|x"
        };

        String[] windows = new String[]
        {
            "windows/InstallService.bat",
            "windows/UninstallService.bat",
            "windows/Wrapper.dll",
            "windows/Wrapper.exe",
            "windows/run.bat"
        };

        InputStream is = cl.getResourceAsStream( JSW + "/wrapper.jar" );

        OutputStream os = new FileOutputStream( new File( coreDir + "/wrapper.jar" ) );

        IOUtil.copy( is, os );

        filterCopy( cl.getResourceAsStream( JSW + "/wrapper.conf" ),
                    new File( confDir, "wrapper.conf" ),
                    configurationProperties );

        copyResources( binDir, cl, linux );

        copyResources( binDir, cl, windows );
    }

    protected void copyResources( File dir, ClassLoader cl, String[] resources )
        throws CommandLineException, IOException
    {
        for ( int i = 0; i < resources.length; i++ )
        {
            String[] s = StringUtils.split( resources[i], "|" );

            String resource = s[0];

            InputStream is = cl.getResourceAsStream( JSW + "/" + resource );

            if ( is == null )
            {
                continue;
            }

            File target = new File( dir, resource );

            mkdir( target.getParentFile() );

            OutputStream os = new FileOutputStream( target );

            IOUtil.copy( is, os );

            IOUtil.close( is );

            IOUtil.close( os );

            if ( s.length == 2 )
            {
                String instructions = s[1];

                if ( instructions.indexOf( "x" ) >= 0 )
                {
                    executable( target );
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    protected void mergeTemplate( String templateName, File outputFileName )
        throws IOException, PlexusRuntimeBuilderException
    {
        FileWriter output = new FileWriter( outputFileName );

        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), output );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBuilderException( "Missing Velocity template: '" + templateName + "'.", ex );
        }
        catch ( Exception ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception merging the velocity template.", ex );
        }

        output.close();
    }
}
