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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultApplicationBuilder
    extends AbstractBuilder
    implements ApplicationBuilder
{
    // ----------------------------------------------------------------------
    // ApplicationBuilder Implementation
    // ----------------------------------------------------------------------

    public void assemble( String applicationName, File workingDirectory, List remoteRepositories,
                          ArtifactRepository localRepository, Set projectArtifacts, Set additionalCoreArtifacts,
                          Set serviceArtifacts, File applicationConfiguration, File configurationsDirectory,
                          Properties configurationProperties )
        throws ApplicationBuilderException
    {
        // ----------------------------------------------------------------------
        // Check the parameters
        // ----------------------------------------------------------------------

        if ( applicationName == null || applicationName.trim().length() == 0 )
        {
            throw new ApplicationBuilderException( "The appserver name must be set." );
        }

        if ( configurationsDirectory != null && !configurationsDirectory.isDirectory() )
        {
            throw new ApplicationBuilderException(
                "The configurations directory isn't a directory: '" + configurationsDirectory.getAbsolutePath() + "." );
        }

        if ( !applicationConfiguration.exists() )
        {
            throw new ApplicationBuilderException( "The appserver configurator file doesn't exist: '" +
                applicationConfiguration.getAbsolutePath() + "'." );
        }

        File libDir;

        try
        {
            // ----------------------------------------------------------------------
            // Create directory structure
            // ----------------------------------------------------------------------

            File confDir = mkdirs( new File( workingDirectory, PlexusApplicationConstants.CONF_DIRECTORY ) );

            libDir = mkdirs( new File( workingDirectory, PlexusApplicationConstants.LIB_DIRECTORY ) );

            // TODO: why does the application have a logs directory?
            File logsDir = mkdirs( new File( workingDirectory, "logs" ) );

            mkdirs( new File( workingDirectory, "META-INF/plexus" ) );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            new FileWriter( new File( logsDir, "foo" ) ).close();

            processConfigurations( confDir, applicationConfiguration, configurationProperties,
                                   configurationsDirectory );
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while processing the configurations.", e );
        }

        // ----------------------------------------------------------------------
        // Find and filter the artifacts
        // ----------------------------------------------------------------------

        Set artifacts;

        try
        {
            Set excludedArtifacts = new HashSet();

            excludedArtifacts.addAll( getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, true ) );

            excludedArtifacts.addAll( getCoreArtifacts( projectArtifacts, additionalCoreArtifacts, remoteRepositories,
                                                        localRepository, true ) );

            serviceArtifacts = findArtifacts( remoteRepositories, localRepository, serviceArtifacts, true, null );

            excludedArtifacts.addAll( serviceArtifacts );

            excludedArtifacts.addAll( getExcludedArtifacts( projectArtifacts, remoteRepositories, localRepository ) );

            ArtifactFilter filter = new AndArtifactFilter( new ScopeExcludeArtifactFilter( Artifact.SCOPE_TEST ),
                                                           new GroupArtifactTypeArtifactFilter( excludedArtifacts ) );

            artifacts = findArtifacts( remoteRepositories, localRepository, projectArtifacts, true, filter );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ApplicationBuilderException( "Error while finding dependencies.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new ApplicationBuilderException( "Error while finding dependencies.", e );
        }

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
        // Make the generated plexus descriptor
        // ----------------------------------------------------------------------

        try
        {
            File descriptor = new File( workingDirectory, PlexusApplicationConstants.METADATA_FILE );

            FileWriter writer = new FileWriter( descriptor );

            XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer );

            writer.write( System.getProperty( "line.separator" ) );

            xmlWriter.startElement( "plexus-appserver" );

            xmlWriter.startElement( "name" );

            xmlWriter.writeText( applicationName );

            xmlWriter.endElement(); // name

            // TODO: Add a list of all artifacts

            xmlWriter.endElement(); // plexus-appserver

            writer.write( System.getProperty( "line.separator" ) );

            writer.close();
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while writing the appserver descriptor.", e );
        }
    }

    public void bundle( File outputFile, File workingDirectory )
        throws ApplicationBuilderException
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
            throw new ApplicationBuilderException( "Error while creating the appserver archive.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void processConfigurations( File confDir, File applicationConfiguration, Properties configurationProperties,
                                        File configurationsDirectory )
        throws IOException, ApplicationBuilderException
    {
        System.out.println( "configurationProperties = " + configurationProperties );

        // ----------------------------------------------------------------------
        // Process the configurations
        // ----------------------------------------------------------------------

        if ( configurationsDirectory != null )
        {
            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir( configurationsDirectory );

            List excludes = new ArrayList();

            // TODO: centralize this list
            excludes.add( "**/CVS/**" );

            excludes.add( "**/.svn/**" );

            /*
            if ( configurationProperties != null )
            {
                excludes.add( configurationProperties.getAbsolutePath() );
            }
            */

            scanner.setExcludes( (String[]) excludes.toArray( new String[excludes.size()] ) );

            scanner.scan();

            String[] files = scanner.getIncludedFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                String file = files[i];

                File in = new File( configurationsDirectory, file );

                File out = new File( confDir, file );

                File parent = out.getParentFile();

                if ( !parent.isDirectory() && !parent.mkdirs() )
                {
                    throw new ApplicationBuilderException(
                        "Could not make parent directories for " + "'" + out.getAbsolutePath() + "'." );
                }

                filterCopy( in, out, configurationProperties );
            }
        }

        // ----------------------------------------------------------------------
        // Copy the main appserver.xml
        // ----------------------------------------------------------------------

        filterCopy( applicationConfiguration, new File( confDir, PlexusApplicationConstants.CONFIGURATION_FILE ),
                    configurationProperties );
    }
}
