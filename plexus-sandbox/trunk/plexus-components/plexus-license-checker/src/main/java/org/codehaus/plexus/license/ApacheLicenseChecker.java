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

package org.codehaus.plexus.license;

import org.codehaus.plexus.license.reports.LicenseReporter;
import org.codehaus.plexus.license.reports.LicenseReport;

import java.util.List;
import java.util.Iterator;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;

/**
 * @author John Tolentino
 */
public class ApacheLicenseChecker
{
    private static File START_LICENSE_FILE;

    private static File END_LICENSE_FILE;

    private static final String EMPTY_STRING = "";


    public ApacheLicenseChecker()
    {
        setLicenseFiles( null, null );
    }

    public ApacheLicenseChecker( File startLicense, File endLicense )
    {
        setLicenseFiles( startLicense, endLicense );
    }

    private void setLicenseFiles( File startLicense, File endLicense )
    {
        if ( ( null == startLicense ) || ( EMPTY_STRING.equals( startLicense ) ) )
        {
            START_LICENSE_FILE = new File( "target/classes/org/codehaus/plexus/license/APACHE_START_LICENSE.TXT" );
        }
        else
        {
            START_LICENSE_FILE = startLicense;
        }

        if ( ( null == startLicense ) || ( EMPTY_STRING.equals( startLicense ) ) )
        {
            END_LICENSE_FILE = new File( "target/classes/org/codehaus/plexus/license/APACHE_END_LICENSE.TXT" );
        }
        else
        {
            END_LICENSE_FILE = endLicense;
        }
    }

    public LicenseReporter generateReports( List fileList, PrintStream result )
    {
        LicenseReporter reporter = generateReports( fileList );

        StringBuffer buffer = new StringBuffer();

        if ( !reporter.getMessages().isEmpty() )
        {
            buffer.append( "\no License Report \n" );
            buffer.append( "\n  The following license problems were found " );
            buffer.append( " (" ).append( reporter.getMessagesByType( LicenseReport.TYPE_ERROR ).size() )
                .append( " errors," );
            buffer.append( " " ).append( reporter.getMessagesByType( LicenseReport.TYPE_WARN ).size() )
                .append( " warnings)" );
            buffer.append( ":\n" );
            for ( Iterator errorIterator = reporter.getMessages().iterator(); errorIterator.hasNext(); )
            {
                String error = (String) errorIterator.next();

                buffer.append( "\n\t" ).append( error );
            }

            buffer.append( "\n" );
        }
        else
        {
            buffer.append( "\nNo documentation errors were found.\n" );
        }

        PrintWriter writer = new PrintWriter( result );
        writer.print( buffer );
        writer.flush();

        return reporter;
    }

    public LicenseReporter generateReports( List fileList )
    {
        LicenseReporter reporter = new LicenseReporter();

        Iterator itr = fileList.iterator();

        while ( itr.hasNext() )
        {
            checkLicense( (String) itr.next(), reporter );
        }

        return reporter;
    }

    public void checkLicense( String filename, LicenseReporter reporter )
    {
        AbstractLicenseChecker checker =
            FactoryLicenseChecker.getLicenseChecker( filename, START_LICENSE_FILE, END_LICENSE_FILE );

        if ( null == checker )
        {
            reporter.warn( "Skipping " + filename + ". No licence checker found for this file type" );
        }
        else
        {
            checker.checkLicense( filename, reporter );
        }
    }
}
