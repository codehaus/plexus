package org.codehaus.plexus.fedr.appender;

import org.codehaus.plexus.fedr.FedrException;
import org.codehaus.plexus.fedr.appender.config.FeedAppenderConfiguration;

public interface FeedAppender
{

    void addFeedEntry( FeedAppenderConfiguration config )
        throws FedrException;

}
