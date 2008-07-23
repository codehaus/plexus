package org.codehaus.plexus.webdav.util;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * MimeTypes 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.webdav.util.MimeTypes"
 */
public class MimeTypes extends AbstractLogEnabled implements Initializable
{
    private static final String MIME_TYPES_RESOURCE = "org/codehaus/plexus/webdav/util/mime-types.txt";

    private Map mimeMap = new HashMap();

    /**
     * Get the Mime Type for the provided filename.
     * 
     * @param filename the filename to obtain the mime type for.
     * @return a mime type String, or null if filename is null, has no extension, or no mime type is associated with it.
     */
    public String getMimeType( String filename )
    {
        if ( StringUtils.isEmpty( filename ) )
        {
            return null;
        }

        String ext = FileUtils.extension( filename );

        if ( StringUtils.isEmpty( ext ) )
        {
            return null;
        }

        return (String) mimeMap.get( ext.toLowerCase() );
    }

    public void initialize() throws InitializationException
    {
        load();
    }

    public void load()
    {
        ClassLoader cloader = this.getClass().getClassLoader();

        /* Load up the mime types table */
        URL mimeURL = cloader.getResource( MIME_TYPES_RESOURCE );

        if ( mimeURL == null )
        {
            throw new IllegalStateException( "Unable to find resource " + MIME_TYPES_RESOURCE );
        }

        mimeMap.clear();

        InputStream mimeStream = null;
        InputStreamReader reader = null;
        BufferedReader buf = null;

        try
        {
            mimeStream = mimeURL.openStream();
            reader = new InputStreamReader( mimeURL.openStream() );
            buf = new BufferedReader( reader );
            String line = null;

            while ( ( line = buf.readLine() ) != null )
            {
                line = line.trim();

                if ( line.length() == 0 )
                {
                    // empty line. skip it
                    continue;
                }

                if ( line.startsWith( "#" ) )
                {
                    // Comment. skip it
                    continue;
                }

                StringTokenizer tokenizer = new StringTokenizer( line );
                if ( tokenizer.countTokens() > 1 )
                {
                    String type = tokenizer.nextToken();
                    while ( tokenizer.hasMoreTokens() )
                    {
                        String extension = tokenizer.nextToken().toLowerCase();
                        this.mimeMap.put( extension, type );
                    }
                }
            }
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to load mime map " + MIME_TYPES_RESOURCE + " : " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( buf );
            IOUtil.close( reader );
            IOUtil.close( mimeStream );
        }
    }
}
