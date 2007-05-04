package org.codehaus.plexus.fedr.generator;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.fedr.FedrException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.fedr.generator.FeedEntryGenerator"
 *   role-hint="simple-version-announcement"
 *   
 * @author jdcasey
 *
 */
public class SimpleVersionAnnouncementGenerator
    implements FeedEntryGenerator
{
    
    /**
     * @plexus.configuration
     */
    private MavenProject project;
    
    public SimpleVersionAnnouncementGenerator( MavenProject project )
    {
        this.project = project;
    }
    
    public SimpleVersionAnnouncementGenerator()
    {
        // plexus init.
    }

    public SyndEntry generateEntry()
        throws FedrException
    {
        SyndEntry entry = new SyndEntryImpl();
        entry.setAuthor( System.getProperty( "user.name" ) );
        entry.setLink( project.getUrl() );
        entry.setTitle( project.getName() + " Version " + project.getVersion() + " Released!" );
        
        List categories = new ArrayList();
        categories.add( project.getGroupId() );
        categories.add( project.getArtifactId() );
        categories.add( project.getUrl() );
        categories.add( project.getName() );
        
        entry.setCategories( categories );
        
        TimeZone tz = TimeZone.getTimeZone( "GMT" );
        Calendar cal = Calendar.getInstance( tz );
        
        entry.setPublishedDate( cal.getTime() );
        
        SyndContent desc = new SyndContentImpl();
        desc.setType( "text/plain" );
        desc.setValue( project.getArtifact().getId() );
        
        entry.setDescription( desc );
        
        SyndContent content = new SyndContentImpl();
        content.setType( "text/plain" );
        content.setValue( project.getVersion() );
        
        entry.setContents( Collections.singletonList( content ) );
        
        return entry;
    }

}
