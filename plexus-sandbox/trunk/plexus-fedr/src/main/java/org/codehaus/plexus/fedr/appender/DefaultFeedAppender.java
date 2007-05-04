package org.codehaus.plexus.fedr.appender;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.codehaus.plexus.collections.ActiveCollectionManager;
import org.codehaus.plexus.collections.ActiveMap;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.fedr.FedrException;
import org.codehaus.plexus.fedr.appender.config.FeedAppenderConfiguration;
import org.codehaus.plexus.fedr.generator.FeedEntryGenerator;
import org.codehaus.plexus.fedr.locator.FeedLocator;
import org.codehaus.plexus.fedr.output.FeedOutputHandler;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.fedr.FeedAppender"
 *   role-hint="default"
 *   
 * @author jdcasey
 *
 */
public class DefaultFeedAppender
    implements FeedAppender, LogEnabled
{

    /**
     * @plexus.requirement
     * @todo Replace this with a real ActiveMap once it's compatible with maven 2.0.x!
     */
    private ActiveCollectionManager collectionManager;

    private Logger logger;

    public DefaultFeedAppender( ActiveCollectionManager collectionManager, Logger logger )
    {
        this.collectionManager = collectionManager;
        this.logger = logger;
    }

    public DefaultFeedAppender()
    {
        // used for plexus init.
    }

    public void addFeedEntry( FeedAppenderConfiguration config )
        throws FedrException
    {
        int maxEntries = config.getMaxEntries();

        List entries = new ArrayList();

        FeedLocator locator = getLocator( config );

        String feedLocation = config.getFeedLocation();
        InputStream feedStream;
        try
        {
            feedStream = locator.getFeedStream( feedLocation );
        }
        catch ( FedrException e )
        {
            throw new FedrException( "Failed to locate feed stream: " + e.getMessage(), e );
        }

        SyndFeed inFeed = null;
        if ( feedStream != null )
        {
            inFeed = readFeed( feedStream );
        }

        String type;
        String title;
        if ( inFeed != null )
        {
            type = config.isFeedTypeForced() ? config.getFeedType() : inFeed.getFeedType();
            title = config.isFeedTitleForced() ? config.getFeedTitle() : inFeed.getTitle();
        }
        else
        {
            type = config.getFeedType();
            title = config.getFeedTitle();
        }

        if ( maxEntries > 1 )
        {
            if ( inFeed != null )
            {
                entries.addAll( inFeed.getEntries() );
            }
        }
        else
        {
            getLogger().info(
                              "maxEntries parameter constrains the generated feed to less than two items. NOT reading existing feed." );
        }

        SyndFeed outFeed = createOutFeed( entries, type, title, config );
        FeedOutputHandler outputHandler = getOutputHandler( config );
        
        outputHandler.handleFeed( outFeed );
    }

    private SyndFeed createOutFeed( List entries, String type, String title, FeedAppenderConfiguration config )
        throws FedrException
    {
        SyndFeed outFeed = new SyndFeedImpl();
        outFeed.setFeedType( type );

        entries = sortAndLimit( entries, config );

        FeedEntryGenerator generator = getEntryGenerator( config );
        entries.add( 0, generator.generateEntry() );

        outFeed.setEntries( entries );

        Set authors = new LinkedHashSet();
        for ( Iterator it = entries.iterator(); it.hasNext(); )
        {
            SyndEntry entry = (SyndEntry) it.next();

            String author = entry.getAuthor();
            if ( author != null )
            {
                authors.add( author );
            }
            else
            {
                List entryAuthors = entry.getAuthors();
                if ( entryAuthors != null && !entryAuthors.isEmpty() )
                {
                    authors.addAll( entryAuthors );
                }
            }
        }

        outFeed.setAuthors( new ArrayList( authors ) );

        Set categories = new LinkedHashSet();
        for ( Iterator it = entries.iterator(); it.hasNext(); )
        {
            SyndEntry entry = (SyndEntry) it.next();

            List entryCategories = entry.getCategories();
            if ( entryCategories != null && !entryCategories.isEmpty() )
            {
                categories.addAll( entryCategories );
            }
        }

        outFeed.setCategories( new ArrayList( categories ) );

        return outFeed;
    }

    private List sortAndLimit( List entries, FeedAppenderConfiguration config )
    {
        List result = entries;

        Collections.sort( result, new Comparator()
        {

            public int compare( Object first, Object second )
            {
                SyndEntry one = (SyndEntry) first;
                SyndEntry two = (SyndEntry) second;

                Date dOne = one.getUpdatedDate();
                if ( dOne == null )
                {
                    dOne = one.getPublishedDate();
                }

                Date dTwo = two.getUpdatedDate();
                if ( dTwo == null )
                {
                    dTwo = two.getPublishedDate();
                }

                return dTwo.compareTo( dOne );
            }

        } );

        int maxEntries = config.getMaxEntries();

        if ( entries.size() > maxEntries - 1 )
        {
            result = result.subList( 0, maxEntries - 1 );
        }

        return result;
    }

    private SyndFeed readFeed( InputStream feedStream )
        throws FedrException
    {
        SyndFeedInput input = new SyndFeedInput();
        try
        {
            return input.build( new XmlReader( feedStream ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new FedrException( "Failed to read feed input stream: " + e.getMessage(), e );
        }
        catch ( FeedException e )
        {
            throw new FedrException( "Failed to read feed input stream: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new FedrException( "Failed to read feed input stream: " + e.getMessage(), e );
        }
    }

    private FeedEntryGenerator getEntryGenerator( FeedAppenderConfiguration config )
        throws FedrException
    {
        FeedEntryGenerator generator = config.getEntryGenerator();
        
        if ( generator == null )
        {
            try
            {
                generator = (FeedEntryGenerator) lookup( config.getEntryGeneratorType(), FeedEntryGenerator.class );
            }
            catch ( ComponentLookupException e )
            {
                throw new FedrException( "Failed to retrieve FeedEntryGenerator with hint: " + config.getEntryGeneratorType(), e );
            }
        }
        
        return generator;
    }

    private FeedLocator getLocator( FeedAppenderConfiguration config )
        throws FedrException
    {
        FeedLocator locator = config.getFeedLocator();
        if ( locator == null )
        {
            try
            {
                locator = (FeedLocator) lookup( config.getFeedLocatorType(), FeedLocator.class );
            }
            catch ( ComponentLookupException e )
            {
                throw new FedrException( "Failed to retrieve FeedLocator with hint: " + config.getFeedLocatorType(), e );
            }
        }

        return locator;
    }

    private FeedOutputHandler getOutputHandler( FeedAppenderConfiguration config )
        throws FedrException
    {
        FeedOutputHandler handler = config.getOutputHandler();
        if ( handler == null )
        {
            try
            {
                handler = (FeedOutputHandler) lookup( config.getOutputHandlerType(), FeedOutputHandler.class );
            }
            catch ( ComponentLookupException e )
            {
                throw new FedrException( "Failed to retrieve FeedOutputHandler with hint: " + config.getOutputHandlerType(), e );
            }
        }

        return handler;
    }

    private Object lookup( String hint, Class clazz )
        throws ComponentLookupException
    {
        ActiveMap map = collectionManager.getActiveMap( clazz );
        return map.checkedGet( hint );
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            logger = new ConsoleLogger( Logger.LEVEL_DEBUG, "DefaultFeedAppender::internal" );
        }

        return logger;
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }
}
