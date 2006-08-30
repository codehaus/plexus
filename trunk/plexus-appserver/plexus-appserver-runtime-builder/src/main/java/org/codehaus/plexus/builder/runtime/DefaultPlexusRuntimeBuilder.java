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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
    // Properties in the configurator properties file
    // ----------------------------------------------------------------------

    private static final String PROPERTY_APP_NAME = "app.name";

    private static final String PROPERTY_APP_LONG_NAME = "app.long.name";

    private static final String PROPERTY_APP_DESCRIPTION = "app.description";

    private static final String PROPERTY_CLASSWORLDS_VERSION = "classworlds.version";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * @requirement
     */
    protected VelocityComponent velocity;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void build( File workingDirectory, List remoteRepositories, ArtifactRepository localRepository,
                       Set projectArtifacts, Set additionalCoreArtifacts, File containerConfiguration,
                       Properties configurationProperties )
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

            if ( containerConfiguration == null )
            {
                throw new PlexusRuntimeBuilderException( "The plexus configurator file must be set." );
            }

            if ( !containerConfiguration.exists() )
            {
                throw new PlexusRuntimeBuilderException( "The specified plexus configurator file " + "'" +
                    containerConfiguration.getAbsolutePath() + "'" + " doesn't exist." );
            }

            // ----------------------------------------------------------------------
            // Find the artifact lists.
            // ----------------------------------------------------------------------

            Set bootArtifacts;

            Set coreArtifacts;

            try
            {
                bootArtifacts = getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, false );

                coreArtifacts = getCoreArtifacts( projectArtifacts, additionalCoreArtifacts, remoteRepositories,
                                                  localRepository, false );
            }
            catch ( ArtifactResolutionException e )
            {
                throw new PlexusRuntimeBuilderException( "Could not resolve a artifact.", e );
            }

            // ----------------------------------------------------------------------
            // Find and put the classworlds version into the properties
            // ----------------------------------------------------------------------

            String classworldsVersion =
                resolveVersion( "classworlds", "classworlds", projectArtifacts, false, new HashSet() );

            configurationProperties.setProperty( PROPERTY_CLASSWORLDS_VERSION, classworldsVersion );

            // ----------------------------------------------------------------------
            // Build the runtime
            // ----------------------------------------------------------------------

            mkdirs( workingDirectory );

            getLogger().info( "Building runtime in " + workingDirectory.getAbsolutePath() );

            // ----------------------------------------------------------------------
            // Set up the directory structure
            // ----------------------------------------------------------------------

            mkdirs( getAppsDirectory( workingDirectory ) );

            File binDir = mkdirs( new File( workingDirectory, PlexusRuntimeConstants.BIN_DIRECTORY ) );

            File confDir = mkdirs( new File( workingDirectory, PlexusRuntimeConstants.CONF_DIRECTORY ) );

            File coreDir = mkdirs( new File( workingDirectory, PlexusRuntimeConstants.CORE_DIRECTORY ) );

            File bootDir = mkdirs( new File( workingDirectory, PlexusRuntimeConstants.BOOT_DIRECTORY ) );

            mkdirs( new File( workingDirectory, PlexusRuntimeConstants.LOGS_DIRECTORY ) );

            mkdirs( new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY ) );

            mkdirs( new File( workingDirectory, PlexusRuntimeConstants.TEMP_DIRECTORY ) );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            copyArtifacts( workingDirectory, bootDir, bootArtifacts );

            copyArtifacts( workingDirectory, coreDir, coreArtifacts );

            // ----------------------------------------------------------------------
            // We need to separate between the container configurator that you want
            // shared amongst the apps and the appserver configurator.
            // ----------------------------------------------------------------------

            processMainConfiguration( containerConfiguration, configurationProperties, confDir );

            createSystemScripts( binDir, confDir, configurationProperties );

            //processConfigurations();

            javaServiceWrapper( binDir, coreDir, confDir, configurationProperties );
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
            throw new PlexusRuntimeBuilderException( "Error while copying the appserver into the runtime.", e );
        }
    }

    public void addPlexusService( File plexusService, File runtimeDirectory )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            File dir = getServicesDirectory( runtimeDirectory );

            String name = plexusService.getName();

            FileUtils.copyFile( plexusService, new File( dir, name ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while copying the appserver into the runtime.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private File getAppsDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.APPLICATIONS_DIRECTORY );
    }

    private File getServicesDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void createSystemScripts( File binDir, File confDir, Properties configurationProperties )
        throws PlexusRuntimeBuilderException, IOException, CommandLineException
    {
        ClassLoader old = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

        createClassworldsConfiguration( confDir, configurationProperties );

        createLauncherScripts( binDir, configurationProperties );

        Thread.currentThread().setContextClassLoader( old );
    }

    private void createClassworldsConfiguration( File confDir, Properties configurationProperties )
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( confDir, "classworlds.conf" ), true, configurationProperties );
    }

    private void createLauncherScripts( File binDir, Properties configurationProperties )
        throws PlexusRuntimeBuilderException, IOException, CommandLineException
    {
        mergeTemplate( UNIX_LAUNCHER_TEMPLATE, new File( binDir, "plexus.sh" ), false, configurationProperties );

        mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( binDir, "plexus.bat" ), true, configurationProperties );

        executable( new File( binDir, "plexus.sh" ) );
    }

    private void processMainConfiguration( File containerConfiguration, Properties configurationProperties,
                                           File confDir )
        throws IOException
    {
        File out = new File( confDir, "plexus.xml" );

        filterCopy( containerConfiguration, out, configurationProperties );
    }

    private void javaServiceWrapper( File binDir, File coreDir, File confDir, Properties configurationProperties )
        throws IOException, CommandLineException
    {
        File linux = new File( binDir, "linux" );

        File win32 = new File( binDir, "win32" );

        File macosx = new File( binDir, "macosx" );

        File solaris = new File( binDir, "solaris" );

        mkdirs( linux );

        mkdirs( win32 );

        mkdirs( macosx );

        mkdirs( solaris );

        // ----------------------------------------------------------------------
        // Generic parts
        // ----------------------------------------------------------------------

        copyResourceToFile( JSW + "/wrapper.jar", new File( coreDir, "boot/wrapper.jar" ) );

        // ----------------------------------------------------------------------
        // Linux
        // ----------------------------------------------------------------------

        File runSh = new File( binDir, "linux/run.sh" );
        filterCopy( getResourceAsStream( JSW + "/run.sh" ), runSh, configurationProperties );
        executable( runSh );

        copyResource( "linux/wrapper", "linux/wrapper", true, binDir );
        copyResource( "linux/libwrapper.so", "linux/libwrapper.so", false, binDir );
        Properties linuxProps = new Properties();
        linuxProps.setProperty( "library.path", "../../bin/linux" );
        linuxProps.setProperty( "extra.path", "" );
        copyWrapperConf( linux, configurationProperties, linuxProps );

        // ----------------------------------------------------------------------
        // Windows
        // ----------------------------------------------------------------------

        copyResource( "win32/wrapper.exe", "win32/wrapper.exe", true, binDir );
        copyResource( "win32/wrapper.dll", "win32/wrapper.dll", false, binDir );
        copyResource( "win32/run.bat", "win32/run.bat", false, binDir );
        copyResource( "win32/InstallService.bat", "win32/InstallService.bat", false, binDir );
        copyResource( "win32/UninstallService.bat", "win32/UninstallService.bat", false, binDir );

        Properties win32Props = new Properties();
        win32Props.setProperty( "library.path", "../../bin/win32" );
        win32Props.setProperty( "extra.path", ";" );
        copyWrapperConf( win32, configurationProperties, win32Props );

        // ----------------------------------------------------------------------
        // OS X
        // ----------------------------------------------------------------------

        runSh = new File( binDir, "macosx/run.sh" );
        filterCopy( getResourceAsStream( JSW + "/run.sh" ), runSh, configurationProperties );
        executable( runSh );
        copyResource( "macosx/wrapper", "macosx/wrapper", true, binDir );
        copyResource( "macosx/libwrapper.jnilib", "macosx/libwrapper.jnilib", false, binDir );

        Properties osxProps = new Properties();
        osxProps.setProperty( "library.path", "../../bin/macosx" );
        osxProps.setProperty( "extra.path", "" );
        copyWrapperConf( macosx, configurationProperties, osxProps );

        // ----------------------------------------------------------------------
        // Solaris
        // ----------------------------------------------------------------------

        runSh = new File( binDir, "solaris/run.sh" );
        filterCopy( getResourceAsStream( JSW + "/run.sh" ), runSh, configurationProperties );
        executable( runSh );

        copyResource( "solaris/wrapper", "solaris/wrapper", true, binDir );
        copyResource( "solaris/libwrapper.so", "solaris/libwrapper.so", false, binDir );

        Properties solarisProps = new Properties();
        solarisProps.setProperty( "library.path", "../../bin/solaris" );
        solarisProps.setProperty( "extra.path", "" );
        copyWrapperConf( solaris, configurationProperties, solarisProps );
    }

    private void copyWrapperConf( File destDir, Properties configurationProperties, Properties additionalProperties )
        throws IOException
    {
        Properties props = new Properties();

        if ( configurationProperties != null )
        {
            for ( Iterator i = configurationProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                props.setProperty( key, configurationProperties.getProperty( key ) );
            }
        }

        if ( additionalProperties != null )
        {
            for ( Iterator i = additionalProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                props.setProperty( key, additionalProperties.getProperty( key ) );
            }
        }

        filterCopy( getResourceAsStream( JSW + "/wrapper.conf" ), new File( destDir, "wrapper.conf" ), props );
    }

    protected void copyResource( String filename, String resource, boolean makeExecutable, File basedir )
        throws CommandLineException, IOException
    {
        File target = new File( basedir, filename );

        copyResourceToFile( JSW + "/" + resource, target );

        if ( makeExecutable )
        {
            executable( target );
        }
    }

    private void copyResourceToFile( String resource, File target )
        throws IOException
    {
        InputStream is = getResourceAsStream( resource );

        mkdirs( target.getParentFile() );

        OutputStream os = new FileOutputStream( target );

        IOUtil.copy( is, os );

        IOUtil.close( is );

        IOUtil.close( os );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Properties loadConfigurationProperties( File configurationPropertiesFile )
        throws IOException, PlexusRuntimeBuilderException
    {
        Properties properties = new Properties();

        if ( configurationPropertiesFile == null )
        {
            throw new PlexusRuntimeBuilderException( "The runtime builder requires a configurator properties file." );
        }

        properties.load( new FileInputStream( configurationPropertiesFile ) );

        // ----------------------------------------------------------------------
        // Validate that some required properties are present
        // ----------------------------------------------------------------------

        assertHasProperty( properties, PROPERTY_APP_NAME );

        assertHasProperty( properties, PROPERTY_APP_LONG_NAME );

        assertHasProperty( properties, PROPERTY_APP_DESCRIPTION );

        return properties;
    }

    private void assertHasProperty( Properties properties, String key )
        throws PlexusRuntimeBuilderException
    {
        if ( StringUtils.isEmpty( properties.getProperty( key ) ) )
        {
            throw new PlexusRuntimeBuilderException( "Missing configurator property '" + key + "'." );
        }
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    protected void mergeTemplate( String templateName, File outputFileName, boolean dos,
                                  Properties configurationProperties )
        throws IOException, PlexusRuntimeBuilderException
    {
        StringWriter buffer = new StringWriter( 100 * FileUtils.ONE_KB );

        File tmpFile = File.createTempFile( outputFileName.getName(), null );

        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), buffer );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBuilderException( "Missing Velocity template: '" + templateName + "'.", ex );
        }
        catch ( Exception ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception merging the velocity template.", ex );
        }

        FileOutputStream output = new FileOutputStream( tmpFile );

        BufferedReader reader = new BufferedReader( new StringReader( buffer.toString() ) );

        String line;

        while ( ( line = reader.readLine() ) != null )
        {
            output.write( line.getBytes() );

            if ( dos )
            {
                output.write( '\r' );
            }

            output.write( '\n' );
        }

        output.close();

        filterCopy( tmpFile, outputFileName, configurationProperties, "@{", "}@" );
    }
}
