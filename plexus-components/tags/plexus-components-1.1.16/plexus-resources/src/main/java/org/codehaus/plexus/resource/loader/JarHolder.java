package org.codehaus.plexus.resource.loader;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
                return conn.getURL() + name;
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
