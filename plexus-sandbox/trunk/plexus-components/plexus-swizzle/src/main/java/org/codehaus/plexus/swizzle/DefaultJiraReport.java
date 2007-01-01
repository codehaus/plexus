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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.swizzle.jirareport.Main;
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
            context.put( "username", configuration.getUsername() );
            context.put( "password", configuration.getPassword() );
            context.put( "projectKey", configuration.getProjectKey() );
            context.put( "projectVersion", configuration.getProjectVersion() );
            context.put( "jiraServerUrl", configuration.getJiraServerUrl() );
            Main.generate( context, configuration.getTemplate(), result );
        }
        catch ( Exception e )
        {
            throw new ReportGenerationException( "Error encountered while generating swizzle report.", e );
        }
    }
}

