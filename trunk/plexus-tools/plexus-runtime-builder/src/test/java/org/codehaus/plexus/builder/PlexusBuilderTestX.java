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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.ArtifactEnabledPlexusTestCase;
import org.codehaus.plexus.builder.runtime.DefaultPlexusRuntimeBuilder;
import org.codehaus.plexus.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PlexusBuilderTestX
    extends ArtifactEnabledPlexusTestCase
{
    private String buildDirectory;

    private String plexusConfiguration;

    private String componentManifest;

    private String configurationsDirectory;

    private String configurationPropertiesFile;

    public void setUp()
        throws Exception
    {
        super.setUp();

        buildDirectory = new File( getBasedir(), "target/test/generated-runtime" ).getPath();

        plexusConfiguration = new File( getBasedir(), "src/test/resources/conf/plexus.conf" ).getPath();

        componentManifest = new File( getBasedir(), "src/test/resources/plexus-component.manifest" ).getPath();

        configurationsDirectory = new File( getBasedir(), "src/test/resources/conf" ).getPath();

        configurationPropertiesFile = new File( getBasedir(), "src/test/resources/configuration.properties" ).getPath();
    }

    public void testPlexusBuilder()
        throws Exception
    {
        DefaultPlexusRuntimeBuilder builder = (DefaultPlexusRuntimeBuilder) lookup( DefaultPlexusRuntimeBuilder.ROLE );

        MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        builder.setBaseDirectory( buildDirectory );

        MavenProject project = projectBuilder.build( new File( getTestFile( "src/test/project/project.xml" ) ) );

        builder.setProject( project );

        ArtifactRepository repository = new ArtifactRepository();

        repository.setBasedir( new File( getBasedir(), "src/test/repository" ).getPath() );

        builder.setLocalRepository( repository );

        builder.setApplicationName( "testapp" );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();

        assertFileEquals( "classworlds-classworlds-1.1-SNAPSHOT", "core/boot/classworlds-1.1-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-0.14-SNAPSHOT", "core/plexus-0.14-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-dependency-1.0", "core/plexus-dependency-1.0.jar" );

        assertFileEquals( "group1-artifact1-2.0", "application/testapp/lib/artifact1-2.0.jar" );

        assertFileEquals( "group2-artifact2-1.0", "application/testapp/lib/artifact2-1.0.jar" );

        assertFileEquals( "group3-artifact3-1.0", "application/testapp/lib/artifact3-1.0.jar" );

        assertFileEquals( "plexus-plexus-runtime-builder-test-project-2.0-SNAPSHOT", "application/testapp/lib/plexus-runtime-builder-test-project-2.0-SNAPSHOT.jar" );

        assertFile( "bin/plexus.bat" );

        assertFile( "bin/plexus.sh" );

        assertFile( "conf/classworlds.conf" );

        assertFile( "conf/plexus.conf" );

        assertFile( "conf/wrapper.conf" );
    }

    private void assertFileEquals( String expected, String fileName )
        throws Exception
    {
        File file = new File( buildDirectory, fileName );

        assertTrue( file.getAbsolutePath() + " doesn't exist.", file.exists() );

        assertTrue( file.getAbsolutePath() + " isn't a file.", file.isFile() );

        String actual = FileUtils.fileRead( file ).trim();

        assertEquals( expected, actual );
    }

    private void assertFile( String fileName )
        throws Exception
    {
        File expectedFile = new File( getTestFile( "src/test/expected-runtime/" ), fileName );

        File actualFile = new File( buildDirectory, fileName );

        BufferedReader expectedInput = new BufferedReader( new FileReader( expectedFile ) );

        BufferedReader actualInput = new BufferedReader( new FileReader( actualFile ) );

        String expected, actual;

        int line = 0;

        while ( ( expected = expectedInput.readLine() ) != null )
        {
            actual = actualInput.readLine();

            if ( actual == null )
            {
                fail( "Line #" + line + ": unexpected end of file." );
            }

            assertEquals( "Line: #" + line, expected, actual );

            line++;
        }

        actual = actualInput.readLine();

        if ( actual != null )
        {
            fail( "The actual file contains more files than the expected file." );
        }
    }
}
