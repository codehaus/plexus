package org.codehaus.plexus.builder.application;

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
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.CollectionUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultApplicationBuilder
    extends AbstractBuilder
    implements ApplicationBuilder
{
    /** @requirement */
//    private Archiver archiver;
/*
    private String applicationName;

    private File applicationLibDirectory;

    private String configurationsDirectory;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    public void setConfigurationsDirectory( String configurationsDirectory )
    {
        this.configurationsDirectory = configurationsDirectory;
    }
*/
    // ----------------------------------------------------------------------
    // ApplicationBuilder Implementation
    // ----------------------------------------------------------------------

    public void build( String applicationName, File outputFile, File workingDirectory,
                       Set remoteRepositories, ArtifactRepository localRepository, Set projectArtifacts,
                       File plexusConfigurationFile, File configurationsDirectory, File configurationPropertiesFile )
        throws ApplicationBuilderException
    {
        // ----------------------------------------------------------------------
        // Check the parameters
        // ----------------------------------------------------------------------

        if ( applicationName == null || applicationName.trim().length() == 0 )
        {
            throw new ApplicationBuilderException( "The application name must be set." );
        }

        if ( configurationPropertiesFile == null )
        {
            throw new ApplicationBuilderException( "The configuration properties file must be set." );
        }

        if ( !configurationPropertiesFile.isFile() )
        {
            throw new ApplicationBuilderException( "The configuration properties file isn't a file: '" + configurationPropertiesFile.getAbsolutePath() + "'." );
        }

        if ( configurationsDirectory != null && !configurationsDirectory.isDirectory() )
        {
            throw new ApplicationBuilderException( "The configurations directory isn't a directory: '" + configurationsDirectory.getAbsolutePath() + "." );
        }

        // ----------------------------------------------------------------------
        // Create directory structure
        // ----------------------------------------------------------------------

        File confDir = mkdir( new File( workingDirectory, "conf" ) );

        File libDir = mkdir( new File( workingDirectory, "lib" ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        try
        {
            processConfigurations( confDir, plexusConfigurationFile, configurationPropertiesFile, configurationsDirectory );
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while processing the configurations." );
        }

        // ----------------------------------------------------------------------
        // Find the dependencies
        // ----------------------------------------------------------------------

        Set artifacts;

        Set coreArtifacts;

        try
        {
            artifacts = findArtifacts( remoteRepositories, localRepository, projectArtifacts, true );

            coreArtifacts = findArtifacts( remoteRepositories, localRepository, CORE_ARTIFACTS, false );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ApplicationBuilderException( "Error while finding dependencies.", e );
        }

        artifacts = new HashSet( CollectionUtils.subtract( artifacts, coreArtifacts ) );

        // ----------------------------------------------------------------------
        // Copy the dependencies
        // ----------------------------------------------------------------------

        try
        {
            copyArtifacts( workingDirectory, libDir, artifacts );
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while copying dependencies.", e );
        }

        // ----------------------------------------------------------------------
        // Build the application jar
        // ----------------------------------------------------------------------

        Archiver archiver = new JarArchiver();

        try
        {
            archiver.addDirectory( workingDirectory );

            archiver.setDestFile( outputFile );

            archiver.createArchive();
        }
        catch ( Exception e )
        {
            throw new ApplicationBuilderException( "Error while creating the application archive.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void processConfigurations( File confDir, File plexusConfigurationFile, File configurationPropertiesFile,
                                        File configurationsDirectory )
        throws ApplicationBuilderException, IOException
    {
        // ----------------------------------------------------------------------
        // Copy the main plexus.xml
        // ----------------------------------------------------------------------

        if ( !plexusConfigurationFile.exists() )
        {
            throw new ApplicationBuilderException( "The application configuration file doesn't exist: '" + plexusConfigurationFile.getAbsolutePath() + "'." );
        }

        FileUtils.copyFileToDirectory( plexusConfigurationFile, confDir );

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

        scanner.setExcludes( new String[]{configurationPropertiesFile.getAbsolutePath(), "**/CVS/**"} );

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
/*
    private void copyApplicationDependencies( Set artifacts, File outputDir, File libDir )
        throws IOException
    {
        Iterator it = artifacts.iterator();

        while ( it.hasNext() )
        {
            Artifact artifact = (Artifact) it.next();

            if ( isBootArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, outputDir, libDir );
        }
    }
*/
}
