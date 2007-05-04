package org.codehaus.plexus.fedr.appender.config;

import org.codehaus.plexus.fedr.generator.FeedEntryGenerator;
import org.codehaus.plexus.fedr.locator.FeedLocator;
import org.codehaus.plexus.fedr.output.FeedOutputHandler;

public interface FeedAppenderConfiguration
{
    
    String getFeedLocation();
    
    String getFeedLocatorType();
    
    FeedLocator getFeedLocator();
    
    String getEntryGeneratorType();
    
    FeedEntryGenerator getEntryGenerator();
    
    String getFeedType();
    
    boolean isFeedTypeForced();
    
    int getMaxEntries();
    
    boolean isFeedTitleForced();

    String getFeedTitle();
    
    String getOutputHandlerType();
    
    FeedOutputHandler getOutputHandler();

}
