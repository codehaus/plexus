package org.codehaus.plexus.cdc;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import org.codehaus.plexus.PlexusTestCase;

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
                                                     getTestFile( "src/test/repository" ) );

        cdc.setProject( project );

        cdc.setBasedir( getTestFile( "src/test-project" ) );

        cdc.setDestDir( getTestFile( "target/output" ) );

        cdc.execute();

        // TODO: Add some assertions
    }
}
