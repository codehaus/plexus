package org.codehaus.plexus.cdc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.Maven;
import org.apache.maven.MavenConstants;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.embed.Embedder;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusCdcTest
    extends PlexusTestCase
{
    public void testCdc()
        throws Exception
    {
        ComponentDescriptorCreator cdc = new ComponentDescriptorCreator();

        MavenProjectBuilder projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        MavenProject project = projectBuilder.build( new File( getTestFile( "src/test-project/project.xml" ) ),
                                                     getTestFile( "target/repository" ) );

        compileProject( project );

        cdc.setProject( project );

        cdc.setBasedir( getTestFile( "src/test-project" ) );

        cdc.setDestDir( getTestFile( "target/output" ) );

        cdc.execute();

        // TODO: Add some assertions
        assertTrue( "The components.xml file must be generated.",
            (new File( getTestFile( "target/output/components.xml" ) ) ).exists() );
    }

    private void compileProject( MavenProject project ) throws Exception
    {
        Embedder embedder = new Embedder();

        embedder.start();

        Maven maven = (Maven) embedder.lookup( Maven.ROLE );

        maven.setMavenHome( System.getProperty( "maven.home" ) );

        maven.setLocalRepository( "target/repository" );

        maven.booty();

        List goals = new ArrayList();

        goals.add( "clean:clean" );

        goals.add( "compiler:compile" );

        maven.execute( project, goals );
    }
}
