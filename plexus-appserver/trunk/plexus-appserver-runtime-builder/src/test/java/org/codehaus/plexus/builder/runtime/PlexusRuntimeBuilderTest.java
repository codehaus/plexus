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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusRuntimeBuilderTest
    extends PlexusTestCase
{
    private ArtifactFactory artifactFactory;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
    }

    public void testRuntimeBuilder()
        throws Exception
    {
        PlexusRuntimeBuilder runtimeBuilder = (PlexusRuntimeBuilder) lookup( PlexusRuntimeBuilder.ROLE );

        ArtifactRepositoryFactory artifactRepositoryFactory =
            (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );

        // ----------------------------------------------------------------------
        // Clean the output directory
        // ----------------------------------------------------------------------

        File workingDirectory = getTestFile( "target/test-runtime" );

        FileUtils.deleteDirectory( workingDirectory );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List remoteRepositories = new ArrayList();

        ArtifactRepositoryLayout repositoryLayout =
            (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE, "legacy" );

        ArtifactRepository localRepository = artifactRepositoryFactory.createArtifactRepository( "local", "file://" +
            getTestFile( "src/test/repository" ).getAbsolutePath(), repositoryLayout );

        Set projectArtifacts = new HashSet();

        projectArtifacts.add( makeArtifact( "classworlds", "classworlds", "1.1-alpha-1" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-container-default", "1.0-alpha-7" ) );
        projectArtifacts.add( makeArtifact( "plexus", "plexus-container-artifact", "1.0-alpha-2" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-appserver-host", "1.0" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-utils", "1.0.4" ) );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File plexusConfiguration = getTestFile( "src/test/resources/conf/plexus.xml" );

        File configurationPropertiesFile = getTestFile( "src/test/resources/configuration.properties" );

        Properties configurationProperties = PropertyUtils.loadProperties( configurationPropertiesFile );

        runtimeBuilder.build( workingDirectory, remoteRepositories, localRepository, projectArtifacts,
                              Collections.EMPTY_SET, plexusConfiguration, configurationProperties, false );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Artifact makeArtifact( String groupId, String artifactId, String version )
    {
        Artifact artifact = artifactFactory.createBuildArtifact( groupId, artifactId, version, "jar" );

        artifact.setFile(
            getTestFile( "src/test/repository/" + groupId + "/jars/" + artifactId + "-" + version + ".jar" ) );

        return artifact;
    }
}
