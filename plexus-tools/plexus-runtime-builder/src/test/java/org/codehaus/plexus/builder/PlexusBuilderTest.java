package org.codehaus.plexus.builder;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

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

        buildDirectory = new File( basedir, "target/generated-runtime" ).getPath();

        plexusConfiguration = new File( basedir, "src/test/resources/conf/plexus.conf" ).getPath();

        componentManifest = new File( basedir, "src/test/resources/plexus-component.manifest" ).getPath();

        configurationsDirectory = new File( basedir, "src/test/resources/conf" ).getPath();

        configurationPropertiesFile = new File( basedir, "src/test/resources/configuration.properties" ).getPath();
    }

    public void testPlexusBuilder()
        throws Exception
    {
        DefaultPlexusBuilder builder = (DefaultPlexusBuilder)lookup( DefaultPlexusBuilder.ROLE );

        MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        builder.setBaseDirectory( buildDirectory );

        MavenProject project = projectBuilder.build( new File( getTestFile( "src/test/project" ) + "/project.xml" ) );

        builder.setProject( project );

        //!! @todo need a way to parameterize this
//        builder.setMavenRepoLocal( System.getProperty( "user.home" ) + "/maven-repo-local" );
        builder.setMavenRepoLocal( getTestFile( "src/test/repository" ) );

//        builder.setApplication( "tambora" );

//        builder.setPlexusVersion( "0.14-SNAPSHOT" );

        builder.setPlexusConfiguration( plexusConfiguration );

//        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
