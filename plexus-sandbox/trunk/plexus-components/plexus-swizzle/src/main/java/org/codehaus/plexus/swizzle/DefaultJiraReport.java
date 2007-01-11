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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Iterator;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.swizzle.jirareport.Main;
import org.codehaus.swizzle.jira.Jira;
import org.codehaus.swizzle.jira.Project;
import org.codehaus.swizzle.jira.Version;
import org.apache.velocity.VelocityContext;

/**
 * Concrete implementation of a <tt>JiraReport</tt> component.  The
 * component is configured via the Plexus container.
 *
 * @author John Tolentino
 * @version $$Id: DefaultJiraReport.java 3353 2006-05-31 14:17:11Z jtolentino $$
 */
public class DefaultJiraReport
    extends AbstractLogEnabled
    implements JiraReport, Startable
{
    /**
     * Velocity context to use
     */
    private VelocityContext context;

    private static Jira jira;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
        getLogger().info( "Starting DefaultJiraReport component." );
    }

    public void stop()
    {
        getLogger().info( "Stopping DefaultJiraReport component." );
    }

    // ----------------------------------------------------------------------
    // JiraReport Implementation
    // ----------------------------------------------------------------------

    public void generateReport( ReportConfiguration configuration, PrintStream result )
        throws ReportGenerationException
    {
        try
        {
            putStandardConfigToContext( configuration, context );
            if ( JiraReport.RELEASE_TEMPLATE.equals( configuration.getTemplate() ) ||
                configuration.isReleaseInfoNeeded() )
            {
                putReleaseInfoConfigToContext( configuration, context );
            }
            Main.generate( context, configuration.getTemplate(), result );
        }
        catch ( Exception e )
        {
            throw new ReportGenerationException( "Error encountered while generating swizzle report.", e );
        }
    }

    private void putStandardConfigToContext( ReportConfiguration configuration, VelocityContext context )
    {
        context.put( "username", configuration.getUsername() );
        context.put( "password", configuration.getPassword() );
        context.put( "projectKey", configuration.getProjectKey() );
        context.put( "projectVersion", configuration.getProjectVersion() );
        context.put( "jiraServerUrl", configuration.getJiraServerUrl() );
    }

    private void putReleaseInfoConfigToContext( ReportConfiguration configuration, VelocityContext context )
        throws Exception
    {
        context.put( "previousReleaseVersion", getPreviousRelease( configuration ) );
        context.put( "lastReleaseDate", getReleaseDate( configuration ) );

        context.put( "groupId", configuration.getGroupId() );
        context.put( "artifactId", configuration.getArtifactId() );
        context.put( "downloadUrl", configuration.getDownloadUrl() );
        context.put( "scmUrl", configuration.getScmUrl() );
        context.put( "scmRevisionId", configuration.getScmRevisionId() );
        context.put( "scmType", configuration.getScmType() );
        context.put( "scmCheckoutCommand", configuration.getScmCheckoutCommand() );
        context.put( "stagingSiteUrl", configuration.getStagingSiteUrl() );
        context.put( "docckPassed", configuration.isDocckPassed() );
        context.put( "docckResultDetails", configuration.getDocckResultDetails() );
        context.put( "docckResultContents", configuration.getDocckResultContents() );
        context.put( "licenseCheckPassed", configuration.isLicenseCheckPassed() );
        context.put( "licenseCheckResultDetails", configuration.getLicenseCheckResultDetails() );
        context.put( "licenseCheckResultContents", configuration.getLicenseCheckResultContents() );
    }

    private String getPreviousRelease( ReportConfiguration configuration )
        throws Exception
    {
        String previousVersion = "";

        Jira jira = getJira( configuration );

        Project project = jira.getProject( configuration.getProjectKey() );

        List versions = jira.getVersions( project );

        for ( Iterator itr = versions.iterator(); itr.hasNext(); )
        {
            Version nextVersion = (Version) itr.next();
            if ( configuration.getProjectVersion().equals( nextVersion.getName() ) )
            {
                return previousVersion;
            }
            else
            {
                previousVersion = nextVersion.getName();
            }
        }

        return previousVersion;
    }

    private String getReleaseDate( ReportConfiguration configuration )
        throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( configuration.getDateFormat() );

        Jira jira = getJira( configuration );

        Project project = jira.getProject( configuration.getProjectKey() );

        String previousRelease = getPreviousRelease( configuration );

        Version version;

        if ( "".equals( previousRelease ) )
        {
            version = jira.getVersion( project, configuration.getProjectVersion() );
        }
        else
        {
            version = jira.getVersion( project, previousRelease );
        }

        return simpleDateFormat.format( version.getReleaseDate() );
    }

    private Jira getJira( ReportConfiguration configuration )
        throws Exception
    {
        if ( null == jira )
        {
            jira = new Jira( configuration.getJiraServerUrl() + "/rpc/xmlrpc" );
            jira.login( configuration.getUsername(), configuration.getPassword() );
        }
        return jira;
    }

}

