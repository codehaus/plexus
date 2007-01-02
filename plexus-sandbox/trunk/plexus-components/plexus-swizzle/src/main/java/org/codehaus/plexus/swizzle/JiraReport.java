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

/**
 * Generates a jira report
 *
 * @author John Tolentino
 * @version $$Id: JiraReport.java 1355 2005-01-06 01:28:02Z jtolentino $$
 */
public interface JiraReport
{
    /**
     * The role associated with the component.
     */
    public final static String ROLE = JiraReport.class.getName();

    /**
     * Generates reports based on the velocity template passed through the configuration parameter.
     * @throws Exception
     */
    public void generateReport( ReportConfiguration configuration, PrintStream result )
        throws ReportGenerationException;

    public void generateSpecialReport( ReportConfiguration configuration, PrintStream result )
        throws ReportGenerationException;

}

