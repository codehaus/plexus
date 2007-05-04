package org.codehaus.plexus.fedr.locator;

import org.codehaus.plexus.fedr.FedrException;

import java.io.InputStream;

public interface FeedLocator
{
    
    String ROLE = FeedLocator.class.getName();

    InputStream getFeedStream( String feedLocation )
        throws FedrException;

}
