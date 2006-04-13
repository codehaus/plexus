package org.codehaus.plexus.components.urlfactory.handlers;




import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * <p>
 * A simple URL stream handler that maps the scheme-specific part of a URL
 * to a classloader resource name. It allows creation of URLs in the format
 * <code>classloader:resource_name</code>, where 'resource_name' is of the same
 * format as you would use for java.lang.ClassLoader.getResourceAsStream()
 * [additionally, any leading "/"'s in the resource name are ignored].
 * </p>
 * <p>
 * This stream handler also supports creating URLs from base and relative parts.
 * The rules for that are similar to file: and http: schemes. So, for example,
 * <code>new URL(URLFactory.newURL("classsloader:a/b/c"), "d")</code> is the same
 * as <code>URLFactory.newURL("classsloader:a/b/d")</code>, while
 * <code>new URL(URLFactory.newURL("classsloader:a/b/c/"), "d")</code> is the same
 * as <code>URLFactory.newURL("classsloader:a/b/c/d")</code>.<p>
 * </p>
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 * @todo This class is very simple ... didn't bother to re-implement it myself
 * but Copyright issues should be resolved.
 * I think that this is an open source code without any licence.
 */
public class ClassLoaderStreamHandler extends URLStreamHandler
{

    /**
     * The scheme name ("classsloader").
     */
    public static final String PROTOCOL = "classloader";

    protected URLConnection openConnection( final URL url ) throws IOException
    {
        return new ClassLoaderResourceURLConnection( url );
    }

    /**
     * This method should return a parseable string form of this URL.
     */
    protected String toExternalForm( final URL url )
    {
        return PROTOCOL.concat( ":" ).concat( url.getFile() );
    }

    /**
     * Must override to prevent default parsing of our URLs as HTTP-like URLs
     * (the base class implementation eventually calls setURL(), which is tied
     * to HTTP URL syntax too much).
     */
    protected void parseURL( final URL context,
                             final String spec,
                             final int start,
                             final int limit )
    {
        final String resourceName =
                combineResourceNames( context.getFile(), spec.substring( start ) );

        // this method is deprecated in J2SDK 1.4+. If you want to avoid
        // compilation warnings you could use the new setURL(URL, String, String, int, String, String, String, String)
        // alternative and fold all resource names into URL's path, for example

        setURL( context, context.getProtocol(), "", -1, resourceName, "" );

    }

    /*
     * The URLConnection implementation used by this scheme. 
     */
    private static final class ClassLoaderResourceURLConnection
            extends URLConnection
    {
        public void connect()
        {
           // do nothing, as we will look for the resource in getInputStream()
        }

        public InputStream getInputStream() throws IOException
        {
            // this always uses the current thread's context loader. A better
            // strategy would be to use techniques shown in
            // http://www.javaworld.com/javaworld/javaqa/2003-06/01-qa-0606-load.html
            // probably for plexus it will be something different
            final ClassLoader loader =
                    Thread.currentThread().getContextClassLoader();

            // don't be fooled by our calling url.getFile(): it is just a string,
            // not necessarily a real file name
            String resourceName = url.getFile();

            if ( resourceName.startsWith( "/" ) )
            {
                resourceName = resourceName.substring( 1 );
            }

            final InputStream result = loader.getResourceAsStream( resourceName );

            if ( result == null )
            {
                final String msg = "Resource '" +
                             resourceName +
                             "' could not be found by classloader [" +
                             loader.getClass().getName() +
                             "]";

                throw new IOException( msg );
            }

            return result;
        }

        protected ClassLoaderResourceURLConnection( final URL url )
        {
            super( url );
         }

    } // end of nested class

    /*
     * This method implements the URL combination as described in the class's
     * javadoc.
     */
    private static String combineResourceNames( final String base, final String relative )
    {
        if ( ( base == null ) || ( base.length() == 0 ) )
        {
            return relative;
        }
        if ( ( relative == null ) || ( relative.length() == 0 ) )
        {
            return base;
        }

        // 'relative' is actually absolute in this case
        if ( relative.startsWith( "/" ) )
        {
            return relative.substring( 1 );
        }

        if ( base.endsWith( "/" ) )
        {
            return base.concat( relative );
        }
        else
        {
            // replace the name segment after the last separator:
            final int lastBaseSlash = base.lastIndexOf( '/' );

            if ( lastBaseSlash < 0 )
            {
                return relative;
            }
            else
            {
                return base.substring( 0, lastBaseSlash ).concat( "/" ).concat( relative );
            }
        }
    }
}