package org.codehaus.plexus.archiver;

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

import java.io.File;

/**
 * @version $Revision$ $Date$
 */
public interface UnArchiver
{
    String ROLE = UnArchiver.class.getName();

    /**
     * Extract the archive.
     *
     * @throws ArchiverException
     */
    void extract()
        throws ArchiverException;

    /**
     * Take a patch into the archive and extract it to the specified directory.
     *
     * @param path Path inside the archive to be extracted.
     * @param outputDirectory Directory to extract to.
     * @throws ArchiverException
     */
    void extract( String path, File outputDirectory )
        throws ArchiverException;

    File getDestDirectory();

    void setDestDirectory( File destDirectory );

    //todo What is this? If you're extracting isn't it always to a directory. I think it would be cool to extract an
    // archive to another archive but I don't think we support this right now.
    File getDestFile();

    void setDestFile( File destFile );

    File getSourceFile();

    void setSourceFile( File sourceFile );

    /**
     * Should we overwrite files in dest, even if they are newer than
     * the corresponding entries in the archive?
     */
    void setOverwrite( boolean b );
}
