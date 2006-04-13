package org.codehaus.plexus.components.urlfactory;

import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Map;

/**
 * Default implementation  of URLFactory.
 *
 * @author <a href="mailto:michal@codehaus.org.ch">Michal Maczka</a>
 *
 * @version $Revision$
 *
 * @component.implementation
 */
public class DefaultURLFactory
        extends AbstractLogEnabled
        implements URLFactory
{

    /**
     * Map with protocol names as keys and URLStreamHandlers as values
     * @component.requirement
     *    role="org.apache.maven.plugins.resource.URLStreamHandler"
     */
    private Map urlStreamHandlers;


    /*
     * Implements a method in URLFactory.
     */
    public URL getURL( final String url ) throws MalformedURLException
    {
        if ( url == null )
        {
            throw new MalformedURLException( "Null input: url" );
        }
        // try already installed protocols first:
        try
        {
            return new URL( url );
        }
        catch ( MalformedURLException ignore )
        {
            // ignore: try our handler list next
        }

        final int firstColon = url.indexOf( ':' );

        if ( firstColon <= 0 )
        {
            throw new MalformedURLException( "No protocol specified: " + url );
        }
        else
        {
            final String protocol = url.substring( 0, firstColon );

            final URLStreamHandler handler =   getURLStreamHandler( protocol );

            if ( handler == null )
            {
                final String msg = "Not recognized protocol specified: '" +
                             protocol +
                             "' for URL '"+
                             url +
                             "'";

                throw new MalformedURLException( msg );
            }

            return new URL( null, url, handler );
        }
    }



    private URLStreamHandler getURLStreamHandler( final String protocol )
            throws MalformedURLException
    {
        try
        {
            final URLStreamHandler handler =
                    ( URLStreamHandler ) urlStreamHandlers.get( protocol );

            return handler;
        }
        catch ( Exception e )
        {
            final String msg =  "Cannot find appropriate URLStreamHandler for protocol: " + protocol;

            throw new MalformedURLException( msg );
        }
    }
}