package org.codehaus.plexus.application;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

public class ApplicationBuilderTest
    extends TestCase
{
    private String buildDirectory;

    private String plexusConfiguration;

    private String componentManifest;

    private String configurationsDirectory;

    private String configurationPropertiesFile;

    private String basedir;
    
    public ApplicationBuilderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ApplicationBuilderTest.class );
    }

    public void setUp()
    {
        basedir = System.getProperty( "basedir" );

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
        ApplicationBuilder builder = new ApplicationBuilder();

        builder.setBaseDirectory( buildDirectory );

        builder.setProjectPom( basedir + "/project.xml" );

        builder.setMavenRepoLocal( System.getProperty("user.home") + "/.maven/repository" );

        builder.setApplication( "tambora" );
        
        builder.setPlexusConfiguration( plexusConfiguration );

        builder.setComponentManifest( componentManifest );

        builder.setConfigurationsDirectory( configurationsDirectory );

        builder.setConfigurationPropertiesFile( configurationPropertiesFile );

        builder.build();
    }
}
