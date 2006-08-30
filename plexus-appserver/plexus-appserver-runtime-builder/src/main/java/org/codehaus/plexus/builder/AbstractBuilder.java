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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractBuilder
    extends AbstractLogEnabled
{
    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private ArtifactResolver artifactResolver;

    /**
     * @plexus.requirement
     */
    private ArtifactFactory artifactFactory;

    /**
     * @plexus.requirement
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * @plexus.requirement
     */
    private ArtifactMetadataSource metadata;

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

    protected File mkdirs( File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            if ( !directory.mkdirs() )
            {
                throw new IOException( "Could not make directories '" + directory.getAbsolutePath() + "'." );
            }
        }

        return directory;
    }

    protected InputStream getResourceAsStream( String resource )
        throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream( resource );

        if ( is == null )
        {
            throw new IOException( "Could not find resource '" + resource + "'." );
        }

        return is;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void filterCopy( File in, File out, Map map )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map );
    }

    protected void filterCopy( File in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map, beginToken, endToken );
    }

    protected void filterCopy( InputStream in, File out, Map map )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map );
    }

    protected void filterCopy( InputStream in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map, beginToken, endToken );
    }

    protected void filterCopy( Reader in, File out, Map map )
        throws IOException
    {
        filterCopy( in, out, map, "@", "@" );
    }

    protected void filterCopy( Reader in, File out, Map map, String beginToken, String endToken )
        throws IOException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, beginToken, endToken );

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

    protected Set getBootArtifacts( Set projectArtifacts, List remoteRepositories, ArtifactRepository localRepository,
                                    boolean ignoreIfMissing )
        throws ArtifactResolutionException
    {
        Set artifacts = new HashSet();

        resolveVersion( "classworlds", "classworlds", projectArtifacts, ignoreIfMissing, artifacts );

        artifacts = findArtifacts( remoteRepositories, localRepository, artifacts, false, null );

        return artifacts;
    }

    protected Set getCoreArtifacts( Set projectArtifacts, Set additionalCoreArtifacts, List remoteRepositories,
                                    ArtifactRepository localRepository, boolean ignoreIfMissing )
        throws ArtifactResolutionException
    {
        Set artifacts = new HashSet();

        resolveVersion( "org.codehaus.plexus", "plexus-container-default", projectArtifacts, ignoreIfMissing,
                        artifacts );

        resolveVersion( "org.codehaus.plexus", "plexus-appserver-host", projectArtifacts, ignoreIfMissing, artifacts );

        resolveVersion( "org.codehaus.plexus", "plexus-utils", projectArtifacts, ignoreIfMissing, artifacts );

        for ( Iterator i = additionalCoreArtifacts.iterator(); i.hasNext(); )
        {
            String additionalArtifact = (String) i.next();

            String[] s = StringUtils.split( additionalArtifact, ":" );

            resolveVersion( s[0], s[1], projectArtifacts, ignoreIfMissing, artifacts );
        }

        artifacts = findArtifacts( remoteRepositories, localRepository, artifacts, false, null );

        return artifacts;
    }

    protected Set getExcludedArtifacts( Set projectArtifacts, List remoteRepositories,
                                        ArtifactRepository localRepository )
        throws ArtifactResolutionException
    {
        Set artifacts = new HashSet();

        resolveVersion( "plexus", "plexus", projectArtifacts, true, artifacts );
        resolveVersion( "plexus", "plexus-container-default", projectArtifacts, true, artifacts );
        resolveVersion( "plexus", "plexus-appserver-host", projectArtifacts, true, artifacts );
        resolveVersion( "plexus", "plexus-utils", projectArtifacts, true, artifacts );

        artifacts = findArtifacts( remoteRepositories, localRepository, artifacts, true, null );

        return artifacts;
    }

    protected Set findArtifacts( List remoteRepositories, ArtifactRepository localRepository, Set sourceArtifacts,
                                 boolean resolveTransitively, ArtifactFilter artifactFilter )
        throws ArtifactResolutionException
    {
        ArtifactResolutionResult result;

        Set resolvedArtifacts;

        Artifact originatingArtifact = artifactFactory.createProjectArtifact( "dummy", "dummy", "dummy" );

        if ( resolveTransitively )
        {
            result = artifactResolver.resolveTransitively( sourceArtifacts, originatingArtifact, localRepository,
                                                           remoteRepositories, metadata, artifactFilter );
            // TODO: Assert that there wasn't any conflicts.

            resolvedArtifacts = result.getArtifacts();
        }
        else
        {
            resolvedArtifacts = new HashSet();

            for ( Iterator it = sourceArtifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                artifactResolver.resolve( artifact, remoteRepositories, localRepository );

                resolvedArtifacts.add( artifact );
            }
        }

        return resolvedArtifacts;
    }

    protected String resolveVersion( String groupId, String artifactId, Set projectArtifacts, boolean ignoreIfMissing,
                                     Set resolvedArtifacts )
    {
        for ( Iterator it = projectArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if ( artifact.getGroupId().equals( groupId ) && artifact.getArtifactId().equals( artifactId ) &&
                artifact.getType().equals( "jar" ) )
            {
                resolvedArtifacts.add( artifact );

                return artifact.getVersion();
            }
        }

        if ( !ignoreIfMissing )
        {
            throw new RuntimeException( "Could not version for artifact: " + groupId + ":" + artifactId + "." );
        }

        return null;
    }

    // TODO: these filters belong in maven-artifact - they are generally useful

    public static class ScopeExcludeArtifactFilter
        implements ArtifactFilter
    {
        private String scope;

        public ScopeExcludeArtifactFilter( String scope )
        {
            this.scope = scope;
        }

        public boolean include( Artifact artifact )
        {
            if ( scope.equals( artifact.getScope() ) )
            {
                return false;
            }

            return true;
        }
    }

    public static class GroupArtifactTypeArtifactFilter
        implements ArtifactFilter
    {
        private Set filteredArtifacts;

        public GroupArtifactTypeArtifactFilter( Set filteredArtifacts )
        {
            this.filteredArtifacts = filteredArtifacts;
        }

        public boolean include( Artifact candiateArtifact )
        {
            for ( Iterator it = filteredArtifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                if ( candiateArtifact.getGroupId().equals( artifact.getGroupId() ) &&
                    candiateArtifact.getArtifactId().equals( artifact.getArtifactId() ) &&
                    candiateArtifact.getType().equals( artifact.getType() ) )
                {
                    return false;
                }
            }

            return true;
        }
    }

    public static class AndArtifactFilter
        implements ArtifactFilter
    {
        private ArtifactFilter filterA;

        private ArtifactFilter filterB;

        public AndArtifactFilter( ArtifactFilter filterA, ArtifactFilter filterB )
        {
            this.filterA = filterA;

            this.filterB = filterB;
        }

        public boolean include( Artifact artifact )
        {
            return filterA.include( artifact ) && filterB.include( artifact );
        }
    }
}
