package org.codehaus.plexus.builder;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;

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

        String basedir = System.getProperty( "basedir" );

        buildDirectory = new File( basedir, "target/plexus" ).getPath();

        plexusConfiguration = new File( basedir, "src/test-input/conf/plexus.conf" ).getPath();

        componentManifest = new File( basedir, "src/test-input/plexus-component.manifest" ).getPath();

        configurationsDirectory = new File( basedir, "src/test-input/conf" ).getPath();

        configurationPropertiesFile = new File( basedir, "src/test-input/configuration.properties" ).getPath();
    }

    public void testPlexusBuilder()
        throws Exception
    {
        DefaultPlexusBuilder builder = (DefaultPlexusBuilder)lookup( DefaultPlexusBuilder.ROLE );

        builder.setBaseDirectory( buildDirectory );

//        builder.setProjectPom( System.getProperty( "basedir" ) + "/project.xml" );
        builder.setProjectPom( getTestFile( "src/test/project" ) + "/project.xml" );

        //!! @todo need a way to parameterize this
//        builder.setMavenRepoLocal( System.getProperty( "user.home" ) + "/maven-repo-local" );
        builder.setMavenRepoLocal( getTestFile( "src/test/repository" ) );

        builder.setApplication( "tambora" );

        builder.setPlexusVersion( "0.14-SNAPSHOT" );

        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
