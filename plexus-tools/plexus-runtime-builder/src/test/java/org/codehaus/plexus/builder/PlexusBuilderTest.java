package org.codehaus.plexus.builder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

public class PlexusBuilderTest
    extends TestCase
{
    private String buildDirectory;

    private String plexusConfiguration;

    private String componentManifest;

    private String configurationsDirectory;

    private String configurationPropertiesFile;

    public PlexusBuilderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PlexusBuilderTest.class );
    }

    public void setUp()
    {
        String basedir = System.getProperty( "basedir" );

        buildDirectory = new File( basedir, "target/plexus" ).getPath();

        plexusConfiguration = new File( basedir, "src/test-input/conf/plexus.conf" ).getPath();

        componentManifest = new File( basedir, "src/test-input/plexus-component.manifest" ).getPath();

        configurationsDirectory = new File( basedir, "src/test-input/conf" ).getPath();

        configurationPropertiesFile = new File( basedir, "src/test-input/configuration.properties" ).getPath();
    }

    /**
     * Rigourous Test :-)
     */
    public void testPlexusBuilder()
        throws Exception
    {
        DefaultPlexusBuilder builder = new DefaultPlexusBuilder();

        builder.setBaseDirectory( buildDirectory );

        builder.setProjectPom( System.getProperty( "basedir" ) + "/project.xml" );

        //!! @todo need a way to parameterize this
        builder.setMavenRepoLocal( System.getProperty( "user.home" ) + "/maven-repo-local" );

        builder.setApplication( "tambora" );

        builder.setPlexusVersion( "0.14-SNAPSHOT" );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
