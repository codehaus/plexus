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
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.builder.BuilderException;
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

    // ----------------------------------------------------------------------
    // ApplicationBuilder Implementation
    // ----------------------------------------------------------------------

    public void build()
        throws BuilderException
    {
        try
        {
            checkApplicationConfiguration();

            createDirectoryStructure();

            processConfigurations();

            copyApplicationDependencies();
        }
        catch ( IOException e )
        {
            throw new ApplicationBuilderException( "Error while copying dependencies.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void checkApplicationConfiguration()
        throws ApplicationBuilderException
    {
        if ( applicationName == null || applicationName.trim().length() == 0 )
        {
            throw new ApplicationBuilderException( "The application name must be set." );
        }
    }

    private void processConfigurations()
        throws BuilderException, IOException
    {
        if ( configurationPropertiesFile == null )
        {
            throw new ApplicationBuilderException( "The configuration properties file must be set." );
        }

        // ----------------------------------------------------------------------
        // Copy the main plexus.xml
        // ----------------------------------------------------------------------

        File plexusConfigurationFile = new File( plexusConfiguration );

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
//            throw new ApplicationBuilderException( "The configuration directory must be set." );
        }

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( configurationsDirectory );

        scanner.setExcludes( new String[]{configurationPropertiesFile, "**/CVS/**"} );

        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        for ( int i = 0; i < files.length; i++ )
        {
            String file = files[i];

            File in = new File( configurationsDirectory, file );

            File out = new File( confDir, files[i] );

            filterCopy( in, out, getConfigurationProperties() );
        }
    }

    protected void createDirectoryStructure()
    {
        applicationLibDirectory = new File( baseDirectory, "lib" );

        confDir = new File( baseDirectory, "conf" );

        mkdir( applicationLibDirectory );

        mkdir( confDir );
    }

    private void copyApplicationDependencies()
        throws BuilderException, IOException
    {
        Set artifacts = project.getArtifacts();

        Iterator it = artifacts.iterator();

        Artifact artifact = new DefaultArtifact( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getType() );

        try
        {
            artifact = artifactResolver.resolve( artifact, getRemoteRepositories(), getLocalRepository() );
        }
        catch ( ArtifactResolutionException ex )
        {
            throw new ApplicationBuilderException( "Error while resolving the project artifact.", ex );
        }

        copyArtifact( artifact, applicationLibDirectory );

        while ( it.hasNext() )
        {
            artifact = (Artifact) it.next();

            if ( isBootArtifact( artifact ) || isPlexusArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, applicationLibDirectory );
        }
    }
}
