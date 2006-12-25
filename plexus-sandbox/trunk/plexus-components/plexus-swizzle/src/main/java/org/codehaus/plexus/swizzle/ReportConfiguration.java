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

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

/**
 * Author: John Tolentino
 */
public class ReportConfiguration
{
    private String projectKey;

    private String projectVersion;

    private String jiraServerUrl;

    private String template;

    private static final String EMPTY_STRING = "";

    public static final String RESOLVED_ISSUES_TEMPLATE = "org/codehaus/plexus/swizzle/ResolvedIssues.vm";

    public static final String VOTES_TEMPLATE = "org/codehaus/plexus/swizzle/Votes.vm";

    public static final String XDOC_SECTION_TEMPLATE = "org/codehaus/plexus/swizzle/XdocSection.vm";

    private static HashMap AVAILABLE_TEMPLATES;

    public ReportConfiguration()
    {
        loadTemplates();
    }

    private void loadTemplates()
    {
        AVAILABLE_TEMPLATES = new HashMap();
        AVAILABLE_TEMPLATES.put( "RESOLVED_ISSUES", RESOLVED_ISSUES_TEMPLATE );
        AVAILABLE_TEMPLATES.put( "VOTES", VOTES_TEMPLATE );
        AVAILABLE_TEMPLATES.put( "XDOC_SECTION", XDOC_SECTION_TEMPLATE );
    }

    public String getProjectKey()
    {
        return projectKey;
    }

    public void setProjectKey( String projectKey )
    {
        this.projectKey = projectKey;
    }

    public String getProjectVersion()
    {
        return projectVersion;
    }

    public void setProjectVersion( String projectVersion )
    {
        this.projectVersion = projectVersion;
    }

    public String getJiraServerUrl()
    {
        return jiraServerUrl;
    }

    public void setJiraServerUrl( String jiraServerUrl )
    {
        this.jiraServerUrl = jiraServerUrl;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate( String template )
        throws ReportConfigurationException
    {
        if ( EMPTY_STRING.equals( template ) )
        {
            String exceptionString = new String();
            throw new ReportConfigurationException( "Provided template was an empty string. Was expecting " +
                getTemplateKeys( AVAILABLE_TEMPLATES.keySet() ) + "or a fully qualified path to a user-provided " +
                "velocity template." );
        }

        if ( AVAILABLE_TEMPLATES.containsKey( template ) )
        {
            this.template = (String) AVAILABLE_TEMPLATES.get( template );
        }
        else
        {
            this.template = template;
        }
    }

    private String getTemplateKeys( Set keySet )
    {
        StringBuffer templateKeys = new StringBuffer();
        Iterator itr = keySet.iterator();
        while ( itr.hasNext() )
        {
            templateKeys.append( "\"" + itr.next() + "\", " );
        }
        return templateKeys.toString();
    }

}
