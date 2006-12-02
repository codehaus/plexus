package org.codehaus.plexus.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A small wrapper around a Jar
 *
 * @author <a href="mailto:daveb@miceda-data.com">Dave Bryson</a>
 * @version $Id$
 */
public class JarHolder
{
    private String urlpath = null;

    private JarFile theJar = null;

    private JarURLConnection conn = null;

    public JarHolder( String urlpath )
    {
        this.urlpath = urlpath;

        init();
    }

    public void init()
    {
        try
        {
            URL url = new URL( urlpath );

            conn = (JarURLConnection) url.openConnection();

            conn.setAllowUserInteraction( false );

            conn.setDoInput( true );

            conn.setDoOutput( false );

            conn.connect();

            theJar = conn.getJarFile();
        }
        catch ( IOException ioe )
        {
        }
    }

    public void close()
    {
        try
        {
            theJar.close();
        }
        catch ( Exception e )
        {
        }

        theJar = null;

        conn = null;
    }

    public InputStream getResource( String theentry )
        throws ResourceNotFoundException
    {
        InputStream data = null;

        try
        {
            JarEntry entry = theJar.getJarEntry( theentry );

            if ( entry != null )
            {
                data = theJar.getInputStream( entry );
            }
        }
        catch ( Exception fnfe )
        {
            throw new ResourceNotFoundException( fnfe.getMessage() );
        }

        return data;
    }

    public Hashtable getEntries()
    {
        Hashtable allEntries = new Hashtable( 559 );

        Enumeration all = theJar.entries();

        while ( all.hasMoreElements() )
        {
            JarEntry je = (JarEntry) all.nextElement();

            // We don't map plain directory entries
            if ( !je.isDirectory() )
            {
                allEntries.put( je.getName(), this.urlpath );
            }
        }
        return allEntries;
    }

    public String getUrlPath()
    {
        return urlpath;
    }
}







