package org.codehaus.plexus.fedr.output;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import org.codehaus.plexus.fedr.FedrException;

import java.io.File;
import java.io.IOException;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.fedr.output.FeedOutputHandler"
 *   role-hint="file"
 *   
 * @author jdcasey
 *
 */
public class FileOutputHandler
    implements FeedOutputHandler
{
    
    /**
     * @plexus.configuration
     */
    private File outputFile;
    
    public FileOutputHandler()
    {
        // plexus init.
    }
    
    public FileOutputHandler( File outputFile )
    {
        this.outputFile = outputFile;
    }

    public void handleFeed( SyndFeed feed )
        throws FedrException
    {
        try
        {
            new SyndFeedOutput().output( feed, outputFile );
        }
        catch ( IOException e )
        {
            throw new FedrException( "Failed to write feed: " + e.getMessage(), e );
        }
        catch ( FeedException e )
        {
            throw new FedrException( "Failed to write feed: " + e.getMessage(), e );
        }
    }
}
