package org.codehaus.plexus.archiver.gzip;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiverException;

import java.io.File;
import java.io.IOException;

/**
 * @version $Revision$ $Date$
 */
public class GZipArchiver extends AbstractArchiver
{
    public void createArchive() throws ArchiverException, IOException
    {
        GZipCompressor compressor = new GZipCompressor();
        if ( getFiles().size() > 1 )
        {
            throw new ArchiverException( "There are more than one file in input." );
        }
        File sourceFile = (File) getFiles().values().toArray()[0];
        compressor.setSourceFile( sourceFile );
        compressor.setDestFile( getDestFile() );
        compressor.execute();
    }
}
