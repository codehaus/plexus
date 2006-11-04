package org.codehaus.plexus.appserver.supervisor;

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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultSupervisor
    implements Supervisor
{
    private List directories = new ArrayList();

    private String name;

    private String extension;

    // ----------------------------------------------------------------------
    // Supervisor Implementation
    // ----------------------------------------------------------------------

    public void addDirectory( File directory, SupervisorListener listener )
        throws SupervisorException
    {
        if ( !directory.isDirectory() )
        {
            throw new SupervisorException(
                "The specified directory doesn't exists or isn't a directory: '" + directory.getAbsolutePath() + "'." );
        }

        directories.add( new SupervisedDirectory( directory, listener ) );
    }

    public void scan()
        throws SupervisorException
    {
        for ( Iterator it = directories.iterator(); it.hasNext(); )
        {
            SupervisedDirectory directory = (SupervisedDirectory) it.next();

            File dir = directory.getDirectory();

            SupervisorListener listener = directory.getListener();

            scanDirectory( dir, listener );
        }
    }

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void scanDirectory( File dir, SupervisorListener listener )
    {
        File[] files = dir.listFiles( new FileFilter()
        {
            public boolean accept( File file )
            {
                return file.isFile() && file.getName().endsWith( extension );
            }
        } );

        for ( int i = 0; i < files.length; i++ )
        {
            File file = files[i];

            listener.onJarDiscovered( file );
        }
    }
}
