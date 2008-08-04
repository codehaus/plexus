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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.resource.PlexusResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @plexus.component role-hint="jar"
 */
public class JarResourceLoader
    extends AbstractResourceLoader
{
    public static final String ID = "jar";

    /**
     * Maps entries to the parent JAR File
     * Key = the entry *excluding* plain directories
     * Value = the JAR URL
     */
    private Map entryDirectory = new HashMap( 559 );

    /**
     * Maps JAR URLs to the actual JAR
     * Key = the JAR URL
     * Value = the JAR
     */
    private Map jarfiles = new HashMap( 89 );

    public void initialize()
        throws InitializationException
    {
        if ( paths != null )
        {
            for ( int i = 0; i < paths.size(); i++ )
            {
                loadJar( (String) paths.get( i ) );
            }
        }
    }

    private void loadJar( String path )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "JarResourceLoader : trying to load \"" + path + "\"" );
        }

        // Check path information
        if ( path == null )
        {
            getLogger().error( "JarResourceLoader : can not load JAR - JAR path is null" );
        }
        if ( !path.startsWith( "jar:" ) )
        {
            getLogger().error( "JarResourceLoader : JAR path must start with jar: -> " +
                "see java.net.JarURLConnection for information" );
        }
        if ( !path.endsWith( "!/" ) )
        {
            path += "!/";
        }

        // Close the jar if it's already open
        // this is useful for a reload
        closeJar( path );

        // Create a new JarHolder
        JarHolder temp = new JarHolder( path );

        // Add it's entries to the entryCollection
        addEntries( temp.getEntries() );

        // Add it to the Jar table
        jarfiles.put( temp.getUrlPath(), temp );
    }

    /**
     * Closes a Jar file and set its URLConnection
     * to null.
     */
    private void closeJar( String path )
    {
        if ( jarfiles.containsKey( path ) )
        {
            JarHolder theJar = (JarHolder) jarfiles.get( path );

            theJar.close();
        }
    }

    /**
     * Copy all the entries into the entryDirectory
     * It will overwrite any duplicate keys.
     */
    private void addEntries( Hashtable entries )
    {
        entryDirectory.putAll( entries );
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param source name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *                                   in the file template path.
     */
    public PlexusResource getResource( String source )
        throws ResourceNotFoundException
    {
        if ( source == null || source.length() == 0 )
        {
            throw new ResourceNotFoundException( "Need to have a resource!" );
        }

        /*
        *  if a / leads off, then just nip that :)
        */
        if ( source.startsWith( "/" ) )
        {
            source = source.substring( 1 );
        }

        if ( entryDirectory.containsKey( source ) )
        {
            String jarurl = (String) entryDirectory.get( source );

            final JarHolder holder = (JarHolder) jarfiles.get( jarurl );
            if ( holder != null )
            {
                return holder.getPlexusResource( source );
            }
        }

        throw new ResourceNotFoundException( "JarResourceLoader Error: cannot find resource " + source );
    }
}
