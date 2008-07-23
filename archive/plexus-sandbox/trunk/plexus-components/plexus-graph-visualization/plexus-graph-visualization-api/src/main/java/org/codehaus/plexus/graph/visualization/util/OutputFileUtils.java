package org.codehaus.plexus.graph.visualization.util;

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

import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * OutputFileUtils - some common output file utility functions used across many visualizers. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class OutputFileUtils
{
    /**
     * Utility to change the filename of a File object by providing a new extension
     * and/or classifier.
     * 
     * @param originalFile the original file to work off of.
     * @param classifier the classifier (or null to not provide one)
     * @param extension (or null to not provide one)
     * @return the new File object with its name adjusted.
     */
    public static File changeFileName( File originalFile, String classifier, String extension )
    {
        if ( ( classifier == null ) && ( extension == null ) )
        {
            return originalFile;
        }

        StringBuffer filename = new StringBuffer();

        filename.append( FileUtils.removeExtension( originalFile.getName() ) );

        if ( classifier != null )
        {
            filename.append( '-' ).append( classifier );
        }

        if ( extension != null )
        {
            filename.append( '.' ).append( extension );
        }
        else
        {
            filename.append( FileUtils.extension( originalFile.getName() ) );
        }

        File changedFile = new File( filename.toString() );

        // Copy in full path (if it exists)
        if ( originalFile.getParentFile() != null )
        {
            changedFile = new File( originalFile.getParentFile(), filename.toString() );
        }

        return changedFile;
    }

    /**
     * Create parent directories (if they don't exist)
     * 
     * @param file the file to work off of.
     */
    public static void ensureParentDirectoriesExist( File file )
    {
        if ( file.getParentFile() != null )
        {
            if ( !file.getParentFile().exists() )
            {
                file.getParentFile().mkdirs();
            }
        }
    }
}
