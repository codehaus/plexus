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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.resource.loader.AbstractResourceLoader;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @plexus.component role-hint="file"
 */
public class FileResourceLoader
    extends AbstractResourceLoader
{
    /** @configuration */
    private List paths;

    // ----------------------------------------------------------------------
    // ResourceLoader Implementation
    // ----------------------------------------------------------------------

    public InputStream getResourceAsInputStream( String name )
        throws ResourceNotFoundException
    {
        for ( Iterator it = paths.iterator(); it.hasNext(); )
        {
            String path = (String) it.next();

            File file = new File( path, name );

            if ( file.canRead() )
            {
                try
                {
                    return new FileInputStream( file );
                }
                catch ( FileNotFoundException e )
                {
                    // this really shouldn't happen as the file already is
                    // verified as readable

                    throw new ResourceNotFoundException( name );
                }
            }
        }

        throw new ResourceNotFoundException( name );
    }
}
