package org.codehaus.plexus.fedr.generator;

import com.sun.syndication.feed.synd.SyndEntry;

import org.codehaus.plexus.fedr.FedrException;

public interface FeedEntryGenerator
{
    
    String ROLE = FeedEntryGenerator.class.getName();

    SyndEntry generateEntry()
        throws FedrException;

}
