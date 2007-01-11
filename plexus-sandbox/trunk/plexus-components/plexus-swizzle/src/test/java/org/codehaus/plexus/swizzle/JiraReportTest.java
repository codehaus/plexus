/**
 *
 * Copyright 2006
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

package org.codehaus.plexus.swizzle;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author jtolentino
 * @version $$Id: JiraReportTest.java 3353 2006-05-31 14:17:11Z jtolentino $$
 */
public class JiraReportTest
    extends PlexusTestCase
{
    private static final String EMPTY_STRING = "";

    public void testConfigurationExceptionHandling()
        throws Exception
    {
        ReportConfiguration configuration = new ReportConfiguration();

        configuration.setUsername( "swizzletester" );
        configuration.setPassword( "swizzle" );
        configuration.setJiraServerUrl( "http://jira.codehaus.org" );
        configuration.setProjectKey( "SWIZZLE" );
        configuration.setProjectVersion( "Test 0.1.1" );

        try
        {
            configuration.setTemplate( EMPTY_STRING );
        }
        catch ( ReportConfigurationException e )
        {
            assertTrue( true );
        }
    }

    public void testGenerateReportResolvedIssuesTemplate()
        throws Exception
    {
        useTemplate( JiraReport.RESOLVED_ISSUES, "Test 0.1.1", false,
                     "org/codehaus/plexus/swizzle/ResolvedIssuesExpectedResult.txt" );
    }

    public void testGenerateVotesReportTemplate()
        throws Exception
    {
        useTemplate( JiraReport.VOTES, "Test 0.2.0", false, "org/codehaus/plexus/swizzle/VotesExpectedResult.txt" );
    }

    public void testGenerateXdocSectionTemplate()
        throws Exception
    {
        useTemplate( JiraReport.XDOC_SECTION, "Test 0.1.1", false, "org/codehaus/plexus/swizzle/XdocSectionExpectedResult.txt" );
    }

    public void testGenerateReleaseTemplate()
        throws Exception
    {
        // release information should implicitly be loaded when the release template is used
        useTemplate( JiraReport.RELEASE, "Test 0.3.0", false, "org/codehaus/plexus/swizzle/ReleaseExpectedResult.txt" );
    }

    // TODO: This test will fail if timezone is enabled in formatting and ran from a different timezone.

    public void testUserProvidedTemplate()
        throws Exception
    {
        useTemplate( "org/codehaus/plexus/swizzle/MyResolvedIssuesTemplate.vm", "Test 0.1.1", true,
                     "org/codehaus/plexus/swizzle/MyResolvedIssuesExpectedResult.txt" );
    }

    private void useTemplate( String template, String version, boolean releaseInfoNeeded, String expectedResult )
        throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream result = new PrintStream( baos );

        ReportConfiguration configuration = new ReportConfiguration();

        configuration.setUsername( "swizzletester" );
        configuration.setPassword( "swizzle" );
        configuration.setJiraServerUrl( "http://jira.codehaus.org" );
        configuration.setProjectKey( "SWIZZLE" );
        configuration.setProjectVersion( version );
        configuration.setTemplate( template );
        configuration.setReleaseInfoNeeded( releaseInfoNeeded );
        if ( JiraReport.RELEASE.equals( template ) || releaseInfoNeeded )
        {
            loadReleaseInfo( configuration );
        }

        JiraReport report = (DefaultJiraReport) lookup( JiraReport.ROLE );

        report.generateReport( configuration, result );

        result.close();

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource( expectedResult );
        String expected = Utils.streamToString( resource.openStream() );

        String actual = new String( baos.toByteArray() );

        assertEquals( expected, actual );
    }

    private void loadReleaseInfo( ReportConfiguration configuration )
    {
        configuration.setGroupId( "org.codehaus.plexus.swizzle" );
        configuration.setArtifactId( "swizzle" );
        configuration.setScmConnection( "scm:svn:http://svn.codehaus.org/swizzle/trunk" );
        configuration.setScmRevisionId( "107" );
        configuration.setDownloadUrl( "http://download-it-here.org/repo/swizzle-1.0.jar" );
        configuration.setStagingSiteUrl( "http://people.apache.org/~jtolentino/release-reports" );
        configuration.setDocckPassed( true );
        configuration.setDocckResultDetails( "target/test-classes/org/codehaus/plexus/swizzle/docck-successful.txt" );
        configuration.setLicenseCheckPassed( false );
        configuration.setLicenseCheckResultDetails( "target/test-classes/org/codehaus/plexus/swizzle/license-failed.txt" );
    }

}
