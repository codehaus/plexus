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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.builder.runtime.GeneratorTools;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Andrew Williams
 * @version $Id$
 */
public class DefaultApplicationBuilder
    extends AbstractBuilder
    implements ApplicationBuilder
{
    /**
     * @plexus.requirement
     */
    private GeneratorTools tools;

    // ----------------------------------------------------------------------
    // ApplicationBuilder Implementation
    // ----------------------------------------------------------------------

    public void assemble( String applicationName, File outputDirectory, List remoteRepositories,
        ArtifactRepository localRepository, Set projectArtifacts, Set additionalCoreArtifacts, Set serviceArtifacts,
        File appserverXmlFile, File applicationXmlFile, File plexusXmlFile, File configurationsDirectory, Properties configurationProperties )
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
            throw new ApplicationBuilderException( "The configurations directory isn't a directory: '"
                + configurationsDirectory.getAbsolutePath() + "." );
        }

        File libDir, confDir;

        try
        {
            // ----------------------------------------------------------------------
            // Create directory structure
            // ----------------------------------------------------------------------

            confDir = tools.mkdirs( new File( outputDirectory, PlexusApplicationConstants.CONF_DIRECTORY ) );

            libDir = tools.mkdirs( new File( outputDirectory, PlexusApplicationConstants.LIB_DIRECTORY ) );

            // TODO: why does the application have a logs directory?
            File logsDir = tools.mkdirs( new File( outputDirectory, "logs" ) );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            new FileWriter( new File( logsDir, "foo" ) ).close();

            processConfigurations( confDir, appserverXmlFile, configurationProperties, configurationsDirectory );

            if ( applicationXmlFile != null )
            {
                tools.filterCopy( applicationXmlFile, tools.mkParentDirs( new File(
                    outputDirectory,
                    PlexusApplicationConstants.CONFIGURATION_FILE ) ), configurationProperties );
            }

            if ( plexusXmlFile != null )
            {
                tools.filterCopy( plexusXmlFile, tools.mkParentDirs( new File(
                    outputDirectory,
                    PlexusApplicationConstants.PLEXUS_XML_FILE ) ), configurationProperties );
            }

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

            excludedArtifacts.addAll( getCoreArtifacts(
                projectArtifacts,
                additionalCoreArtifacts,
                remoteRepositories,
                localRepository,
                true ) );

            serviceArtifacts = findArtifacts( remoteRepositories, localRepository, serviceArtifacts, true, null );

            excludedArtifacts.addAll( serviceArtifacts );

            excludedArtifacts.addAll( getExcludedArtifacts( projectArtifacts, remoteRepositories, localRepository ) );

            ArtifactFilter filter = new AndArtifactFilter(
                new GroupArtifactTypeArtifactFilter( excludedArtifacts ),
                new AndArtifactFilter(
                    new ScopeExcludeArtifactFilter( Artifact.SCOPE_TEST ),
                    new ScopeExcludeArtifactFilter( Artifact.SCOPE_PROVIDED ) ) );

            artifacts = findArtifacts( remoteRepositories, localRepository, projectArtifacts, true, filter );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new ApplicationBuilderException( "Error while finding dependencies.", e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ApplicationBuilderException( "Error while finding dependencies.", e );
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
            throw new ApplicationBuilderException( "Error while copying dependencies.", e );
        }

        // ----------------------------------------------------------------------
        // Make the generated plexus descriptor
        // ----------------------------------------------------------------------

        FileWriter writer = null;
        try
        {
            File descriptor = new File( confDir, PlexusApplicationConstants.METADATA_FILE );

            Xpp3Dom dom;

            if ( !descriptor.exists() )
            {
                dom = new Xpp3Dom( "plexus-appserver" );
            }
            else
            {
                FileReader reader = new FileReader( descriptor );

                try
                {
                    dom = Xpp3DomBuilder.build( new FileReader( descriptor ) );
                }
                catch ( XmlPullParserException e )
                {
                    throw new ApplicationBuilderException( "Error reading the application metadata file "
                        + PlexusApplicationConstants.METADATA_FILE + "." );
                }
                finally
                {
                    reader.close();
                }
            }

            Xpp3Dom name = new Xpp3Dom( "name" );
            name.setValue( applicationName );
            dom.addChild( name );

            writer = new FileWriter( descriptor );

            Xpp3DomWriter.write( writer, dom );

            writer.close();
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while writing the appserver descriptor.", e );
        }
        finally
        {
            if ( writer != null )
            {
                try
                {
                    writer.close();
                }
                catch ( IOException e )
                {
                    /* never mind */
                }
            }
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

    private void processConfigurations( File confDir, File applicationConfiguration,
        Properties configurationProperties, File configurationsDirectory )
        throws IOException,
            ApplicationBuilderException
    {
        // ----------------------------------------------------------------------
        // Process the configurations
        // ----------------------------------------------------------------------

        if ( configurationsDirectory != null )
        {
            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir( configurationsDirectory );

            // TODO: centralize this list
            scanner.setExcludes( new String[] { "**/CVS/**", "**/.svn/**" } );

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
                    throw new ApplicationBuilderException( "Could not make parent directories for " + "'"
                        + out.getAbsolutePath() + "'." );
                }

                tools.filterCopy( in, out, configurationProperties );
            }
        }
    }
}
