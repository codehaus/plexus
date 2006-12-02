package org.codehaus.plexus.resource;

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

import java.util.Iterator;
import java.io.InputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.resource.loader.manager.ResourceLoaderManager;
import org.codehaus.plexus.resource.loader.ResourceLoader;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultResourceManager
    extends AbstractLogEnabled
    implements ResourceManager
{
    /** @plexus.requirement */
    private ResourceLoaderManager resourceLoaderManager;

    // ----------------------------------------------------------------------
    // ResourceManager Implementation
    // ----------------------------------------------------------------------

    public InputStream getResourceAsInputStream( String name )
        throws ResourceNotFoundException
    {
        InputStream is = null;

        for ( Iterator it = resourceLoaderManager.getResourceLoaders(); it.hasNext(); )
        {
            ResourceLoader resourceLoader = (ResourceLoader) it.next();

            try
            {
                is = resourceLoader.getResourceAsInputStream( name );

                break;
            }
            catch ( ResourceNotFoundException e )
            {
                // ignore and continue to the next loader
            }
        }

        if ( is == null )
        {
            throw new ResourceNotFoundException( name );
        }

        return is;
    }   

    public File getResourceAsFile( String name )
        throws ResourceNotFoundException, FileResourceCreationException
    {
        InputStream is = getResourceAsInputStream( name );

        InputStreamReader reader = new InputStreamReader( is );

        Writer writer;

        File outputFile = FileUtils.createTempFile( "plexus-resources", "tmp", null );

        try
        {
            writer = new FileWriter( outputFile );

            IOUtil.copy( reader, writer );
        }
        catch ( IOException e )
        {
            throw new FileResourceCreationException( "Cannot create file-based resource.", e );
        }

        return outputFile;
    }
}
