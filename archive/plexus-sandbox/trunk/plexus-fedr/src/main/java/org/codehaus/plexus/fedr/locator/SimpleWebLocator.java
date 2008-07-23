package org.codehaus.plexus.fedr.locator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.codehaus.plexus.fedr.FedrException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SimpleWebLocator
    implements FeedLocator
{

    private static final int ONE_SECOND = 1000;

    public InputStream getFeedStream( String feedLocation )
        throws FedrException
    {
        if ( feedLocation.matches( "https?:\\/\\/.+" ) )
        {
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setConnectionTimeout( 10 * ONE_SECOND );
            
            HttpConnectionManager manager = new SimpleHttpConnectionManager();
            manager.setParams( params );
            
            HttpClient client = new HttpClient( manager );
            
            HeadMethod head = new HeadMethod( feedLocation );
            head.setFollowRedirects( true );
            
            try
            {
                int result = client.executeMethod( head );
                
                if ( result == HttpStatus.SC_OK )
                {
                    GetMethod get = new GetMethod( feedLocation );
                    get.setFollowRedirects( true );
                    
                    if ( ( result = client.executeMethod( get ) ) == HttpStatus.SC_OK )
                    {
                        return get.getResponseBodyAsStream();
                    }
                }
                
                throw new FedrException( "Cannot access feed at: " + feedLocation + " (" + result + ")" );
            }
            catch ( HttpException e )
            {
                throw new FedrException( "Cannot access feed at: " + feedLocation + ". Reason: " + e.getMessage(), e );
            }
            catch ( IOException e )
            {
                throw new FedrException( "Cannot access feed at: " + feedLocation + ". Reason: " + e.getMessage(), e );
            }
        }
        else
        {
            int colonIdx = feedLocation.indexOf( ':' );
            if ( colonIdx > -1 && colonIdx + 3 < feedLocation.length() )
            {
                feedLocation = feedLocation.substring( colonIdx + 3 );
            }
            
            File file = new File( feedLocation );
            
            try
            {
                return new FileInputStream( file );
            }
            catch ( FileNotFoundException e )
            {
                throw new FedrException( "Feed location not found on filesystem: " + feedLocation, e );
            }
        }
        
    }

}
