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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.MavenMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractBuilder
    extends AbstractLogEnabled
{
    protected final static Set BOOT_ARTIFACTS = new HashSet( Arrays.asList( new Artifact[]{
        new DefaultArtifact( "classworlds", "classworlds", "1.1-alpha-2-SNAPSHOT", "jar" ),
    } ) );

    protected final static Set CORE_ARTIFACTS = new HashSet( Arrays.asList( new Artifact[]{
        new DefaultArtifact( "plexus", "plexus-container-default", "1.0-alpha-2-SNAPSHOT", "jar" ),
        new DefaultArtifact( "plexus", "plexus-container-artifact", "1.0-alpha-2-SNAPSHOT", "jar" ),
        new DefaultArtifact( "plexus", "plexus-appserver", "1.0-alpha-1-SNAPSHOT", "jar" ),
        new DefaultArtifact( "plexus", "plexus-utils", "1.0-alpha-2-SNAPSHOT", "jar" ),
    } ) );

    protected final static Set EXCLUDED_ARTIFACTS = new HashSet( Arrays.asList( new Artifact[]{
        new DefaultArtifact( "plexus", "plexus", "", "jar" ),
        new DefaultArtifact( "plexus", "plexus-container-api", "", "jar" ),
    } ) );

    private ArtifactFilter artifactFilter = new BuilderArtifactFilter();

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    protected ArtifactResolver artifactResolver;

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    protected void executable( File file )
        throws CommandLineException, IOException
    {
        if ( Os.isFamily( "unix" ) )
        {
            Commandline cli = new Commandline();

            cli.setExecutable( "chmod" );

            cli.createArgument().setValue( "+x" );

            cli.createArgument().setValue( file.getAbsolutePath() );

            cli.execute();
        }
    }

    protected File mkdir( File directory )
    {
        if ( !directory.exists() )
        {
            directory.mkdirs();
        }

        return directory;
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

    protected void copyArtifact( Artifact artifact, File outputDir, File destination )
        throws IOException
    {
        String dest = destination.getAbsolutePath().substring( outputDir.getAbsolutePath().length() + 1 );

        getLogger().info( "Adding " + artifact.getId() + " to " + dest );

        FileUtils.copyFileToDirectory( artifact.getFile(), destination );
    }

    protected void copyArtifacts( File outputDir, File dir, Set artifacts )
        throws IOException
    {
        for ( Iterator it = artifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            copyArtifact( artifact, outputDir, dir );
        }
    }

    protected Set findArtifacts( List remoteRepositories, ArtifactRepository localRepository, Set sourceArtifacts, boolean resolveTransitively )
        throws ArtifactResolutionException
    {
        ArtifactResolutionResult result;

        MavenMetadataSource metadata = new MavenMetadataSource( artifactResolver );

        Set artifacts;

        if ( resolveTransitively )
        {
            result = artifactResolver.resolveTransitively( sourceArtifacts, remoteRepositories, localRepository, metadata, artifactFilter );

            // TODO: Assert that there wasn't any conflicts.

            artifacts = new HashSet( result.getArtifacts().values() );
        }
        else
        {
            artifacts = artifactResolver.resolve( sourceArtifacts, remoteRepositories, localRepository );
        }

        return artifacts;
    }

    protected void filterArtifacts( Set candidateArtifacts, Set filteredArtifacts )
    {
        // ----------------------------------------------------------------------
        // Remove any artifacts from the candidateArtifacts set.
        // It will ignore the version when checking for a match
        // ----------------------------------------------------------------------

        for ( Iterator it = candidateArtifacts.iterator(); it.hasNext(); )
        {
            Artifact candiateArtifact = (Artifact) it.next();

            for ( Iterator it2 = filteredArtifacts.iterator(); it2.hasNext(); )
            {
                Artifact artifact = (Artifact) it2.next();

                if ( candiateArtifact.getGroupId().equals( artifact.getGroupId() ) &&
                    candiateArtifact.getArtifactId().equals( artifact.getArtifactId() ) &&
                    candiateArtifact.getType().equals( artifact.getType() ) )
                {
                    it.remove();

                    continue;
                }
            }
        }
    }

    class BuilderArtifactFilter
        implements ArtifactFilter
    {
        public boolean include( Artifact artifact )
        {
            if ( artifact.getScope().equals(  Artifact.SCOPE_TEST ) )
            {
                return false;
            }

            return true;
        }
    }
}
