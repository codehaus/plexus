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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.builder.runtime.PlexusRuntimeBuilderException;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.repository.RepositoryUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class AbstractBuilder
    extends AbstractLogEnabled
{
    protected ArtifactRepository localRepository;

    protected MavenProject project;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    protected VelocityComponent velocity;

    protected MavenProjectBuilder projectBuilder;

    protected ArtifactResolver artifactResolver;

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

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    protected void executable( String file )
        throws IOException
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
        throws PlexusRuntimeBuilderException
    {
        Artifact artifact = new DefaultArtifact( groupId, artifactId, version, type );

        try
        {
            return artifactResolver.resolve( artifact, getRemoteRepositories(), getLocalRepository() );
        }
        catch ( ArtifactResolutionException ex )
        {
            throw new PlexusRuntimeBuilderException( "Error while resolving artifact. id=" + artifact.getId() + ":" );
        }
    }
}
