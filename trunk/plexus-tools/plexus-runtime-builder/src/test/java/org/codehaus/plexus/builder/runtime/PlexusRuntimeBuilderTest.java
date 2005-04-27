package org.codehaus.plexus.builder.runtime;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusRuntimeBuilderTest
    extends PlexusTestCase
{
    public void testRuntimeBuilder()
        throws Exception
    {
        PlexusRuntimeBuilder runtimeBuilder = (PlexusRuntimeBuilder) lookup( PlexusRuntimeBuilder.ROLE );

        ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );

        // ----------------------------------------------------------------------
        // Clean the output directory
        // ----------------------------------------------------------------------

        File workingDirectory = getTestFile( "target/test-runtime" );

        FileUtils.deleteDirectory( workingDirectory );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE, "legacy" );

        ArtifactRepository localRepository =
            artifactRepositoryFactory.createArtifactRepository( "local",
                                                                "file://" + getTestFile( "src/test/repository" ).getAbsolutePath(),
                                                                repositoryLayout,
                                                                null );

        Set projectArtifacts = new HashSet();

        projectArtifacts.add( makeArtifact( "classworlds", "classworlds", "1.1-alpha-1" ) );
        projectArtifacts.add( makeArtifact( "plexus", "plexus-container-default", "1.0-alpha-2" ) );
        projectArtifacts.add( makeArtifact( "plexus", "plexus-container-artifact", "1.0-alpha-2" ) );
        projectArtifacts.add( makeArtifact( "plexus", "plexus-appserver", "1.0-alpha-1-SNAPSHOT" ) );
        projectArtifacts.add( makeArtifact( "plexus", "plexus-utils", "1.0-alpha-2" ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File plexusConfiguration = getTestFile( "src/test/resources/conf/plexus.xml" );

        File configurationPropertiesFile = getTestFile( "src/test/resources/configuration.properties" );

        runtimeBuilder.build( workingDirectory,
                              remoteRepositories, localRepository, projectArtifacts,
                              plexusConfiguration, configurationPropertiesFile );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Artifact makeArtifact( String groupId, String artifactId, String version )
    {
        Artifact artifact = new DefaultArtifact( groupId, artifactId, version, "jar" );

        artifact.setFile( getTestFile( "src/test/repository/" + groupId + "/jars/" + artifact + "-" + version + ".jar" ) );

        return artifact;
    }
}
