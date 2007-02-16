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
    private String startLicensePattern;

    private String endLicensePattern;

    public LicenseReport report;

    public AbstractLicenseChecker( String startOfLicense, String endOfLicense )
    {
        startLicensePattern = startOfLicense.replaceAll( "\\s", "" );
        endLicensePattern = endOfLicense.replaceAll( "\\s", "" );
    }

    public abstract String removeCommentCharacters( File source )
        throws IOException;

    public void checkLicense( String filename, LicenseReporter reporter )
    {
        try
        {
            String testFile = removeCommentCharacters( new File( filename ) );

            if ( testFile.lastIndexOf( startLicensePattern ) == -1 )
            {
                reporter.error( "Can't find the start of license: " + filename );
            }
            else if ( testFile.lastIndexOf( endLicensePattern ) == -1 )
            {
                reporter.error( "Can't find the end of license: " + filename );
            }
            else if ( testFile.lastIndexOf( endLicensePattern ) < testFile.lastIndexOf( startLicensePattern ) )
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