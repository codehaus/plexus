package org.codehaus.plexus.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

public class PlexusBuilderTest
    extends PlexusTestCase
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

        buildDirectory = new File( getBasedir(), "target/generated-runtime" ).getPath();

        plexusConfiguration = new File( getBasedir(), "src/test/resources/conf/plexus.conf" ).getPath();

        componentManifest = new File( getBasedir(), "src/test/resources/plexus-component.manifest" ).getPath();

        configurationsDirectory = new File( getBasedir(), "src/test/resources/conf" ).getPath();

        configurationPropertiesFile = new File( getBasedir(), "src/test/resources/configuration.properties" ).getPath();
    }

    public void testPlexusBuilder()
        throws Exception
    {
        DefaultPlexusBuilder builder = (DefaultPlexusBuilder)lookup( DefaultPlexusBuilder.ROLE );

        MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        builder.setBaseDirectory( buildDirectory );

        MavenProject project = projectBuilder.build( new File( getTestFile( "src/test/project/project.xml" ) ), getTestFile( "src/test/repository" ) );

        builder.setProject( project );

        builder.setMavenRepoLocal( getTestFile( "src/test/repository" ) );

        builder.setApplicationName( "testapp" );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();

        assertFileEquals( "classworlds-classworlds-1.1-SNAPSHOT", "core/boot/classworlds-1.1-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-0.14-SNAPSHOT", "core/plexus-0.14-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-dependency-1.0", "core/plexus-dependency-1.0.jar" );

        assertFileEquals( "group1-artifact1-2.0", "apps/testapp/lib/artifact1-2.0.jar" );

        assertFileEquals( "group2-artifact2-1.0", "apps/testapp/lib/artifact2-1.0.foo" );

        assertFileEquals( "group2-artifact2-1.0", "apps/testapp/lib/artifact2-1.0.foo" );

        assertFileEquals( "plexus-plexus-runtime-builder-test-project-2.0-SNAPSHOT", "apps/testapp/lib/plexus-runtime-builder-test-project-2.0-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-runtime-builder-test-project-2.0-SNAPSHOT", "apps/testapp/lib/plexus-runtime-builder-test-project-2.0-SNAPSHOT.jar" );

        assertFileEquals( "plexus-plexus-runtime-builder-test-project-2.0-SNAPSHOT", "apps/testapp/lib/plexus-runtime-builder-test-project-2.0-SNAPSHOT.jar" );

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
