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
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.Artifact;

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

        // ----------------------------------------------------------------------
        // Clean the output directory
        // ----------------------------------------------------------------------

        File outputFile = getTestFile( "target/test-runtime.jar" );

        File workingDirectory = getTestFile( "target/test-runtime" );

        FileUtils.deleteDirectory( workingDirectory );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Set remoteRepositories = new HashSet();

        ArtifactRepository localRepository = new ArtifactRepository( "local", "file://" + getTestFile( "src/test/repository" ).getAbsolutePath() );

        Set artifacts = new HashSet();

        Artifact a1 = new DefaultArtifact( "group1", "artifact1", "2.0", "jar" );

        a1.setPath( localRepository.getBasedir() + "/group1/jars/artifact1-2.0.jar" );

        artifacts.add( a1 );

        Artifact a2 = new DefaultArtifact( "group2", "artifact2", "1.0", "jar" );

        a2.setPath( localRepository.getBasedir() + "/group2/jars/artifact2-1.0.jar" );

        artifacts.add( a2 );

        Artifact a3 = new DefaultArtifact( "plexus", "plexus-container-default", "1.0-alpha-2-SNAPSHOT", "jar" );

        a3.setPath( localRepository.getBasedir() + "/plexus/jars/plexus-container-default-1.0-alpha-2-SNAPSHOT.jar" );

        artifacts.add( a3 );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        File plexusConfiguration = getTestFile( "src/test/resources/conf/plexus.xml" );

        File configurationPropertiesFile = getTestFile( "src/test/resources/configuration.properties" );

        runtimeBuilder.build( workingDirectory,
                              remoteRepositories, localRepository, artifacts,
                              plexusConfiguration, configurationPropertiesFile );
    }
}
