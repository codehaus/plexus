package org.codehaus.plexus.fedr.appender.config;

import org.codehaus.plexus.fedr.generator.FeedEntryGenerator;
import org.codehaus.plexus.fedr.locator.FeedLocator;
import org.codehaus.plexus.fedr.output.FeedOutputHandler;

public class BasicFeedAppenderConfiguration
    implements FeedAppenderConfiguration
{

    private String feedLocation;

    private String feedLocatorType;
    
    private FeedLocator feedLocator;

    private String entryGeneratorType;
    
    private FeedEntryGenerator entryGenerator;

    private String feedType;
    
    String feedTitle;

    private boolean forceFeedType;
    
    boolean forceFeedTitle;

    private int maxEntries;
    
    private String outputHandlerType;
    
    private FeedOutputHandler outputHandler;
    
    public boolean isFeedTypeForced()
    {
        return forceFeedType;
    }
    
    public void setFeedTypeForced( boolean forceFeedType )
    {
        this.forceFeedType = forceFeedType;
    }

    public boolean isFeedTitleForced()
    {
        return forceFeedTitle;
    }
    
    public void setFeedTitleForced( boolean forceFeedTitle )
    {
        this.forceFeedTitle = forceFeedTitle;
    }

    public String getEntryGeneratorType()
    {
        return entryGeneratorType;
    }

    public void setEntryGeneratorType( String entryGeneratorType )
    {
        this.entryGeneratorType = entryGeneratorType;
    }

    public String getFeedLocation()
    {
        return feedLocation;
    }

    public void setFeedLocation( String feedLocation )
    {
        this.feedLocation = feedLocation;
    }

    public String getFeedLocatorType()
    {
        return feedLocatorType;
    }

    public void setFeedLocatorType( String feedLocatorType )
    {
        this.feedLocatorType = feedLocatorType;
    }

    public String getFeedType()
    {
        return feedType;
    }

    public void setFeedType( String feedType )
    {
        this.feedType = feedType;
    }

    public int getMaxEntries()
    {
        return maxEntries;
    }

    public void setMaxEntries( int maxEntries )
    {
        this.maxEntries = maxEntries;
    }

    public String getFeedTitle()
    {
        return feedTitle;
    }

    public void setFeedTitle( String feedTitle )
    {
        this.feedTitle = feedTitle;
    }

    public FeedEntryGenerator getEntryGenerator()
    {
        return entryGenerator;
    }

    public void setEntryGenerator( FeedEntryGenerator entryGenerator )
    {
        this.entryGenerator = entryGenerator;
    }

    public FeedLocator getFeedLocator()
    {
        return feedLocator;
    }

    public void setFeedLocator( FeedLocator feedLocator )
    {
        this.feedLocator = feedLocator;
    }

    public FeedOutputHandler getOutputHandler()
    {
        return outputHandler;
    }

    public void setOutputHandler( FeedOutputHandler outputHandler )
    {
        this.outputHandler = outputHandler;
    }

    public String getOutputHandlerType()
    {
        return outputHandlerType;
    }

    public void setOutputHandlerType( String outputHandlerType )
    {
        this.outputHandlerType = outputHandlerType;
    }

}
