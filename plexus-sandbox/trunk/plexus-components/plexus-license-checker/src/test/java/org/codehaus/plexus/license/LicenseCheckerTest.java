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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.license.reports.LicenseReporter;
import org.codehaus.plexus.license.reports.LicenseReport;

import java.util.List;
import java.util.ArrayList;

/**
 * @author John Tolentino
 */
public class LicenseCheckerTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\AbstractLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\test\\resources\\License.java" );
        fileList.add( getBasedir() + "\\src\\test\\resources\\LICENSE.HTML" );
        fileList.add( getBasedir() + "\\src\\test\\resources\\test.apt" );
        fileList.add( getBasedir() + "\\src\\test\\resources\\site.xml" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertFalse( reporter.hasErrors() );

    }

    public void testSelf()
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\AbstractLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\ApacheLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\AptLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\FactoryLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\JavaLicenseChecker.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\Utils.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\XmlLicenseChecker.java" );
        fileList.add(
            getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\AbstractLicenseReport.java" );
        fileList.add(
            getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\ErrorLicenseReport.java" );
        fileList.add(
            getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\InfoLicenseReport.java" );
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\LicenseReport.java" );
        fileList.add(
            getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\LicenseReporter.java" );
        fileList.add(
            getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\reports\\WarningLicenseReport.java" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertFalse( reporter.hasErrors() );

    }

    public void testFileNotFoundException()
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\main\\java\\org\\codehaus\\plexus\\license\\something.java" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertTrue( reporter.hasErrors() );
        assertEquals( 1, reporter.getMessagesByType( LicenseReport.TYPE_ERROR ).size() );

    }

    public void testNoLicenseException()
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\test\\resources\\NoLicense.java" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertTrue( reporter.hasErrors() );

        assertEquals( 1, reporter.getMessagesByType( LicenseReport.TYPE_ERROR ).size() );

    }

    public void testFileTypeNotSupported()
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\test\\resources\\test.xar" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertFalse( reporter.hasErrors() );

        assertEquals( 1, reporter.getMessagesByType( LicenseReport.TYPE_WARN ).size() );

    }

    public void testErrorAndWarning()
    {
        List fileList = new ArrayList();
        fileList.add( getBasedir() + "\\src\\test\\resources\\NoLicense.java" );
        fileList.add( getBasedir() + "\\src\\test\\resources\\test.xar" );

        ApacheLicenseChecker checker = new ApacheLicenseChecker();

        LicenseReporter reporter = checker.generateReports( fileList, System.out );

        assertTrue( reporter.hasErrors() );

        assertEquals( 1, reporter.getMessagesByType( LicenseReport.TYPE_ERROR ).size() );

        assertEquals( 1, reporter.getMessagesByType( LicenseReport.TYPE_WARN ).size() );

    }

}
