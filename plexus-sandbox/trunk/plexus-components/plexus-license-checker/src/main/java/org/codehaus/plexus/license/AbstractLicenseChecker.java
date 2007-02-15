package org.codehaus.plexus.license;
/**
 *
 * Copyright 2007
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

import org.codehaus.plexus.license.reports.LicenseReport;
import org.codehaus.plexus.license.reports.LicenseReporter;

import java.io.IOException;
import java.io.File;

/**
 * @author John Tolentino
 */
public abstract class AbstractLicenseChecker
{
    private File START_LICENSE_FILE;

    private File END_LICENSE_FILE;

    private String START_LICENSE;

    private String END_LICENSE;

    public LicenseReport report;

    public AbstractLicenseChecker( File startLicenseFile, File endLicenseFile )
    {
        START_LICENSE_FILE = startLicenseFile;
        END_LICENSE_FILE = endLicenseFile;
        loadLicenseFile();
    }

    private void loadLicenseFile()
    {
        try
        {
            START_LICENSE = Utils.fileToString( START_LICENSE_FILE ).replaceAll( "\\s", "" );
            END_LICENSE = Utils.fileToString( END_LICENSE_FILE ).replaceAll( "\\s", "" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public abstract String removeCommentCharacters( File source )
        throws IOException;

    public void checkLicense( String filename, LicenseReporter reporter )
    {
        try
        {
            String testFile = removeCommentCharacters( new File( filename ) );

            if ( testFile.lastIndexOf( START_LICENSE ) == -1 )
            {
                reporter.error( "Can't find the start of license: " + filename );
            }
            else if ( testFile.lastIndexOf( END_LICENSE ) == -1 )
            {
                reporter.error( "Can't find the end of license: " + filename );
            }
            else if ( testFile.lastIndexOf( END_LICENSE ) < testFile.lastIndexOf( START_LICENSE ) )
            {
                reporter.error( "Improper sequence of start and end of the license: " + filename );
            }
        }
        catch ( IOException e )
        {
            reporter.error( "Problem encountered while loading file: " + filename );
        }

    }
}