package org.codehaus.plexus.fedr.output;

import com.sun.syndication.feed.synd.SyndFeed;

import org.codehaus.plexus.fedr.FedrException;

public interface FeedOutputHandler
{
    
    String ROLE = FeedOutputHandler.class.getName();
    
    void handleFeed( SyndFeed feed )
        throws FedrException;

}
