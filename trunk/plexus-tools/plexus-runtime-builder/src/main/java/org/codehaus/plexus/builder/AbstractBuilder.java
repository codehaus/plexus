package org.codehaus.plexus.builder;

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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.MavenMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositoryUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractBuilder
    extends AbstractLogEnabled
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    protected ArtifactRepository localRepository;

    protected MavenProject project;

    protected String baseDirectory;

    protected String plexusConfiguration;

    protected String configurationPropertiesFile;

    protected Properties configurationProperties;

    protected File confDir;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    protected VelocityComponent velocity;

    protected MavenProjectBuilder projectBuilder;

    protected ArtifactResolver artifactResolver;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private final static String[][] bootArtifacts =
        {
            {"classworlds", "classworlds"}
        };

    private Set plexusArtifacts;

    // ----------------------------------------------------------------------
    // Abstract methods
    // ----------------------------------------------------------------------

    protected abstract void createDirectoryStructure();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    protected ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public Set getRemoteRepositories()
    {
        return RepositoryUtils.mavenToWagon( project.getRepositories() );
    }

    public void setBaseDirectory( String baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }

    public void setPlexusConfiguration( String plexusConfiguration )
    {
        this.plexusConfiguration = plexusConfiguration;
    }

    public void setConfigurationPropertiesFile( String configurationPropertiesFile )
    {
        this.configurationPropertiesFile = configurationPropertiesFile;
    }

    protected Properties getConfigurationProperties()
        throws BuilderException
    {
        if ( configurationProperties == null )
        {
            Properties p = new Properties();

            File input = new File( configurationPropertiesFile );

            try
            {
                p.load( new FileInputStream( input ) );
            }
            catch ( IOException ex )
            {
                throw new BuilderException( "Exception while reading configuration file: " + input.getPath(), ex );
            }

            configurationProperties = new Properties();

            for ( Enumeration e = p.propertyNames(); e.hasMoreElements(); )
            {
                String name = (String) e.nextElement();

                configurationProperties.setProperty( name, p.getProperty( name ) );
            }
        }

        return configurationProperties;
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    protected void executable( String file )
        throws CommandLineException, IOException
    {
        if ( Os.isFamily( "unix" ) )
        {
            Commandline cli = new Commandline();

            cli.setExecutable( "chmod" );

            cli.createArgument().setValue( "+x" );

            cli.createArgument().setValue( file );

            cli.execute();
        }
    }

    protected void mkdir( File directory )
    {
        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    protected void mergeTemplate( String templateName, File outputFileName )
        throws IOException, BuilderException
    {
        FileWriter output = new FileWriter( outputFileName );

        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), output );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new BuilderException( "Missing Velocity template: '" + templateName + "'.", ex );
        }
        catch ( Exception ex )
        {
            throw new BuilderException( "Exception merging the velocity template.", ex );
        }

        output.close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void filterCopy( File in, File out, Map map )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map );
    }

    protected void filterCopy( InputStream in, File out, Map map )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map );
    }

    protected void filterCopy( Reader in, File out, Map map )
        throws IOException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, "@", "@" );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );

        writer.close();
    }

    // ----------------------------------------------------------------------
    // Artifact methods
    // ----------------------------------------------------------------------

    protected Artifact resolve( String groupId, String artifactId, String version, String type )
        throws BuilderException
    {
        Artifact artifact = new DefaultArtifact( groupId, artifactId, version, type );

        try
        {
            return artifactResolver.resolve( artifact, getRemoteRepositories(), getLocalRepository() );
        }
        catch ( ArtifactResolutionException ex )
        {
            throw new BuilderException( "Error while resolving artifact. id=" + artifact.getId() + ":" );
        }
    }

    protected boolean isPlexusArtifact( Artifact artifact )
        throws BuilderException
    {
        Set plexusArtifacts = findPlexusArtifacts();

        for ( Iterator it = plexusArtifacts.iterator(); it.hasNext(); )
        {
            Artifact a = (Artifact) it.next();

            if ( a.getGroupId().equals( artifact.getGroupId() ) && a.getArtifactId().equals( artifact.getArtifactId() ) )
            {
                return true;
            }
        }

        return false;
    }

    protected void copyArtifact( Artifact artifact, File destination )
        throws IOException
    {
        String dest = destination.getAbsolutePath().substring( baseDirectory.length() + 1 );

        getLogger().info( "Adding " + artifact.getId() + " to " + dest );

        FileUtils.copyFileToDirectory( artifact.getFile(), destination );

        //artifacts.remove( artifact.getId() );
    }

    protected MavenProject buildProject( File file )
        throws BuilderException
    {
        try
        {
            return projectBuilder.build( file, localRepository );
        }
        catch ( ProjectBuildingException ex )
        {
            throw new BuilderException( "Error while building project: " + ex );
        }
    }

    protected boolean isBootArtifact( Artifact artifact )
    {
        for ( int i = 0; i < bootArtifacts.length; i++ )
        {
            String groupId = artifact.getGroupId();

            String artifactId = artifact.getArtifactId();

            if ( bootArtifacts[i][0].equals( groupId ) &&
                bootArtifacts[i][1].equals( artifactId ) )
            {
                return true;
            }
        }

        return false;
    }

    protected Set findArtifacts( MavenProject project )
        throws BuilderException
    {
        Set artifacts = findArtifacts( project, getRemoteRepositories(), getLocalRepository() );

        // ----------------------------------------------------------------------
        // Find the artifact for the application itself
        // ----------------------------------------------------------------------

        Artifact artifact = resolve( project.getGroupId(), project.getArtifactId(), project.getVersion(), project.getType() );

        artifacts.add( artifact );

        return artifacts;
    }

    protected Set findArtifacts( MavenProject project, Set repositories, ArtifactRepository localRepository )
        throws BuilderException
    {
        ArtifactResolutionResult result;

        MavenMetadataSource metadata = new MavenMetadataSource( repositories, localRepository, artifactResolver );

        try
        {
            result = artifactResolver.resolveTransitively( project.getArtifacts(), repositories, localRepository, metadata );
        }
        catch ( ArtifactResolutionException ex )
        {
            throw new BuilderException( "Exception while getting artifacts for " + project.getId() + ".", ex );
        }

        return new HashSet( result.getArtifacts().values() );
    }

    protected Set findPlexusArtifacts()
        throws BuilderException
    {
        if ( plexusArtifacts != null )
        {
            return plexusArtifacts;
        }

        Artifact plexusArtifact = null;

        for ( Iterator it = project.getArtifacts().iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            String groupId = artifact.getGroupId();

            String artifactId = artifact.getArtifactId();

            String type = artifact.getType();

            if ( groupId.equals( "plexus" ) &&
                 artifactId.equals( "plexus-container-default" ) &&
                 type.equals( "jar" ) )
            {
                plexusArtifact = artifact;

                break;
            }
        }

        if ( plexusArtifact == null )
        {
            throw new BuilderException( "Could not find plexus JAR in the dependency list." );
        }

        Artifact plexusPom = resolve( plexusArtifact.getGroupId(), plexusArtifact.getArtifactId(),
                                      plexusArtifact.getVersion(), "pom" );

        if ( plexusPom == null )
        {
            throw new BuilderException( "Cannot find pom for: " + plexusArtifact.getId() );
        }

        MavenProject plexus = buildProject( plexusPom.getFile() );

        plexusArtifacts = findArtifacts( plexus, getRemoteRepositories(), getLocalRepository() );

        plexusArtifacts.add( plexusArtifact );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Artifact appserver = new DefaultArtifact( "plexus", "plexus-appserver", "1.0-alpha-1-SNAPSHOT", "jar" );

        appserver.setPath( getLocalRepository().getBasedir() + "/plexus/jars/plexus-appserver-1.0-alpha-1-SNAPSHOT.jar" );

        plexusArtifacts.add( appserver );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Artifact utils = new DefaultArtifact( "plexus", "plexus-utils", "1.0-alpha-1-SNAPSHOT", "jar" );

        utils.setPath( getLocalRepository().getBasedir() + "/plexus/jars/plexus-utils-1.0-alpha-1-SNAPSHOT.jar" );

        plexusArtifacts.add( utils );

        return plexusArtifacts;
    }
}
