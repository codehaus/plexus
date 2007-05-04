package org.codehaus.plexus.fedr.generator;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.fedr.FedrException;
import org.codehaus.plexus.fedr.generator.SimpleVersionAnnouncementGenerator;
import org.easymock.MockControl;

import java.util.List;

import junit.framework.TestCase;

public class SimpleVersionAnnouncementGeneratorTest
    extends TestCase
{

    public void testGenerateEntry()
        throws FedrException
    {
        Model model = new Model();
        model.setVersion( "1.2.1" );
        model.setGroupId( "some.group" );
        model.setArtifactId( "some-artifact" );

        model.setName( "Some Project" );

        model.setUrl( "http://mojo.codehaus.org" );

        MavenProject project = new MavenProject( model );

        MockControl ctl = MockControl.createControl( Artifact.class );
        Artifact artifact = (Artifact) ctl.getMock();

        artifact.getId();
        ctl.setReturnValue( "some.group:some-artifact:1.2.1:jar", MockControl.ZERO_OR_MORE );

        project.setArtifact( artifact );

        ctl.replay();

        SyndEntry entry = new SimpleVersionAnnouncementGenerator( project ).generateEntry();

        List contents = entry.getContents();

        assertNotNull( contents );
        assertEquals( 1, contents.size() );
        assertEquals( model.getVersion(), ( (SyndContent) contents.get( 0 ) ).getValue() );
        
        assertEquals( model.getName() + " Version " + model.getVersion() + " Released!", entry.getTitle() );
        assertEquals( System.getProperty( "user.name" ), entry.getAuthor() );
        
        System.out.println( entry.getCategories() );

        ctl.verify();
    }

}
