package org.codehaus.plexus.resource.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.codehaus.plexus.resource.PlexusResource;

/**
 * A small wrapper around a Jar
 * 
 * @author <a href="mailto:daveb@miceda-data.com">Dave Bryson</a>
 * @author Jason van Zyl
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

    public PlexusResource getPlexusResource( final String name )
    {
        final JarEntry entry = theJar.getJarEntry( name );
        if ( entry == null )
        {
            return null;
        }
        return new PlexusResource()
        {
            public File getFile()
                throws IOException
            {
                return null;
            }

            public InputStream getInputStream()
                throws IOException
            {
                return theJar.getInputStream( entry );
            }

            public String getName()
            {
                return conn.getJarFileURL() + name;
            }

            public URI getURI()
                throws IOException
            {
                return null;
            }

            public URL getURL()
                throws IOException
            {
                return new URL( conn.getJarFileURL(), name );
            }
        };
    }
}
