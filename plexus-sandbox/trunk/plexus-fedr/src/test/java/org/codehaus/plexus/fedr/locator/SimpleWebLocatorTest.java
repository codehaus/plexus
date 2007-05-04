package org.codehaus.plexus.fedr.locator;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import org.codehaus.plexus.fedr.FedrException;
import org.codehaus.plexus.fedr.locator.SimpleWebLocator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

public class SimpleWebLocatorTest
    extends TestCase
{

    public void testSimpleHttpFeed()
        throws FedrException, IllegalArgumentException, FeedException, IOException
    {
        String href = "http://news.google.com/nwshp?ie=UTF-8&oe=UTF-8&hl=en&tab=wn&q=&output=atom";

        InputStream stream = new SimpleWebLocator().getFeedStream( href );

        assertNotNull( stream );

        SyndFeed feed = new SyndFeedInput().build( new XmlReader( stream ) );

        assertEquals( "Google News", feed.getTitle() );
    }

    public void testSimpleFileFeed()
        throws FedrException, IllegalArgumentException, FeedException, IOException
    {
        String href = getTestFile( "google-news.xml", "src/test/resources" ).getAbsolutePath();

        InputStream stream = new SimpleWebLocator().getFeedStream( href );

        assertNotNull( stream );

        SyndFeed feed = new SyndFeedInput().build( new XmlReader( stream ) );

        assertEquals( "Google News", feed.getTitle() );
    }

    private File getTestFile( String path, String altBase )
    {
        String classPath = getClass().getName().replace( '.', '/' ) + ".class";

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URL url = cl.getResource( classPath );

        String base = url.getPath();
        base = base.substring( 0, base.length() - classPath.length() );

        File f = new File( base, path );
        if ( !f.exists() )
        {
            f = new File( altBase, path );
        }
        
        return f;
    }

}
