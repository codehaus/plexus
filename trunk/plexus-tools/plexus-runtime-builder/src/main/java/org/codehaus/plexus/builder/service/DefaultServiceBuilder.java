package org.codehaus.plexus.builder.service;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import org.codehaus.plexus.application.PlexusServiceConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultServiceBuilder
    extends AbstractBuilder
    implements ServiceBuilder
{
    // ----------------------------------------------------------------------
    // ServiceBuilder Implementation
    // ----------------------------------------------------------------------

    public void build( String serviceName, File outputDirectory, File classes,
                       List remoteRepositories, ArtifactRepository localRepository, Set projectArtifacts,
                       File plexusConfigurationFile, File configurationsDirectory, File configurationPropertiesFile )
        throws ServiceBuilderException
    {
        // ----------------------------------------------------------------------
        // Check the parameters
        // ----------------------------------------------------------------------

        if ( serviceName == null || serviceName.trim().length() == 0 )
        {
            throw new ServiceBuilderException( "The application name must be set." );
        }

        if ( configurationPropertiesFile != null && !configurationPropertiesFile.isFile() )
        {
            throw new ServiceBuilderException( "The configuration properties file isn't a file: '" + configurationPropertiesFile.getAbsolutePath() + "'." );
        }

        if ( configurationsDirectory != null && !configurationsDirectory.isDirectory() )
        {
            throw new ServiceBuilderException( "The configurations directory isn't a directory: '" + configurationsDirectory.getAbsolutePath() + "." );
        }

        // ----------------------------------------------------------------------
        // Create directory structure
        // ----------------------------------------------------------------------

        File confDir = mkdir( new File( outputDirectory, PlexusServiceConstants.CONF_DIRECTORY ) );

        File libDir = mkdir( new File( outputDirectory, PlexusServiceConstants.LIB_DIRECTORY ) );

        File classesDir = mkdir( new File( outputDirectory, PlexusServiceConstants.CLASSES_DIRECTORY ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        try
        {
            processConfigurations( confDir, plexusConfigurationFile, configurationPropertiesFile, configurationsDirectory );
        }
        catch ( IOException e )
        {
            throw new ServiceBuilderException( "Error while processing the configurations." );
        }

        // ----------------------------------------------------------------------
        // Find the and filter the dependencies
        // ----------------------------------------------------------------------

        Set artifacts;

        try
        {
            Set excludedArtifacts = new HashSet();

            excludedArtifacts.addAll( getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, true ) );

            excludedArtifacts.addAll( getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, true ) );

            ArtifactFilter filter = new AndArtifactFilter(
                new ScopeExcludeArtifactFilter( Artifact.SCOPE_TEST ),
                new GroupArtifactTypeArtifactFilter( excludedArtifacts ) );

            artifacts = findArtifacts( remoteRepositories, localRepository, projectArtifacts, true, filter );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ServiceBuilderException( "Error while finding dependencies.", e );
        }

        // ----------------------------------------------------------------------
        // Copy the classes
        // ----------------------------------------------------------------------

        try
        {
            FileUtils.copyDirectoryStructure( classes, classesDir );
        }
        catch ( IOException e )
        {
            throw new ServiceBuilderException( "Error while copying the classes.", e );
        }

        // ----------------------------------------------------------------------
        // Copy the dependencies
        // ----------------------------------------------------------------------

        try
        {
            copyArtifacts( outputDirectory, libDir, artifacts );
        }
        catch ( IOException e )
        {
            throw new ServiceBuilderException( "Error while copying dependencies.", e );
        }

        // TODO: Make a META-INF/MANIFEST.MF that includes references to all the jar files
        // in /lib
    }

    public void bundle( File outputFile, File workingDirectory )
        throws ServiceBuilderException
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
            throw new ServiceBuilderException( "Error while creating the service archive.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void processConfigurations( File confDir, File plexusConfigurationFile, File configurationPropertiesFile,
                                        File configurationsDirectory )
        throws ServiceBuilderException, IOException
    {
        // ----------------------------------------------------------------------
        // Copy the main plexus.xml
        // ----------------------------------------------------------------------

        if ( !plexusConfigurationFile.exists() )
        {
            throw new ServiceBuilderException( "The application configuration file doesn't exist: '" + plexusConfigurationFile.getAbsolutePath() + "'." );
        }

        FileUtils.copyFile( plexusConfigurationFile, new File( confDir, PlexusServiceConstants.CONFIGURATION_FILE ) );

        // ----------------------------------------------------------------------
        // Process the configurations
        // ----------------------------------------------------------------------

        if ( configurationsDirectory == null )
        {
            return;
        }

        Properties configurationProperties = new Properties();

        if ( configurationPropertiesFile != null )
        {
            configurationProperties.load( new FileInputStream( configurationPropertiesFile ) );
        }

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( configurationsDirectory );

        List excludes = new ArrayList();

        excludes.add( "**/CVS/**" );

        if ( configurationPropertiesFile != null )
        {
            excludes.add( configurationPropertiesFile.getAbsolutePath() );
        }

        scanner.setExcludes( (String[]) excludes.toArray( new String[ excludes.size() ] ) );

        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        for ( int i = 0; i < files.length; i++ )
        {
            String file = files[i];

            File in = new File( configurationsDirectory, file );

            File out = new File( confDir, files[i] );

            filterCopy( in, out, configurationProperties );
        }
    }
}
