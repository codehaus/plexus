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
import java.util.Iterator;

import org.codehaus.plexus.resource.PlexusResource;
import org.codehaus.plexus.resource.loader.AbstractResourceLoader;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.util.FileUtils;

import com.sun.naming.internal.ResourceManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @plexus.component role-hint="file"
 */
public class FileResourceLoader
    extends AbstractResourceLoader
{
    public static final String ID = "file";

    // ----------------------------------------------------------------------
    // ResourceLoader Implementation
    // ----------------------------------------------------------------------

    public PlexusResource getResource( String name )
        throws ResourceNotFoundException
    {
        for ( Iterator it = paths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();

            final File file = new File( path, name );

            if ( file.canRead() )
            {
                return new FilePlexusResource( file );
            }
        }
        throw new ResourceNotFoundException( name );
    }

    /**
     * @deprecated Use {@link ResourceManager#getResourceAsFile( PlexusResource )}.
     */
    public static File getResourceAsFile( String name,
                                          String outputPath,
                                          File outputDirectory )
        throws FileResourceCreationException

    {
        File f = new File( name );

        if ( f.exists() )
        {
            if ( outputPath == null )
            {
                return f;
            }
            else
            {
                try
                {
                    File outputFile;

                    if ( outputDirectory != null )
                    {
                        outputFile = new File( outputDirectory, outputPath );
                    }
                    else
                    {
                        outputFile = new File( outputPath );
                    }

                    if ( !outputFile.getParentFile().exists() )
                    {
                        outputFile.getParentFile().mkdirs();
                    }

                    FileUtils.copyFile( f, outputFile );

                    return outputFile;
                }
                catch ( IOException e )
                {
                    throw new FileResourceCreationException( "Cannot create file-based resource.", e );
                }
            }
        }

        return null;
    }
}
