package org.codehaus.plexus.security.ui.web.reports;

/*
 * Copyright 2005-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ReportManager
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.ui.web.reports.ReportManager"
 */
public class ReportManager
    extends AbstractLogEnabled
    implements Initializable
{
    /**
     * @plexus.requirement role="org.codehaus.plexus.security.ui.web.reports.Report"
     */
    private List availableReports;

    private Map reportMap;

    public Report findReport( String id, String type )
        throws ReportException
    {
        if ( StringUtils.isBlank( id ) )
        {
            throw new ReportException( "Unable to generate report from empty report id." );
        }

        if ( StringUtils.isBlank( type ) )
        {
            throw new ReportException( "Unable to generate report from empty report type." );
        }

        Map typeMap = (Map) reportMap.get( id );
        if ( typeMap == null )
        {
            throw new ReportException( "Unable to find report id [" + id + "]" );
        }

        Report requestedReport = (Report) typeMap.get( type );

        if ( requestedReport == null )
        {
            throw new ReportException( "Unable to find report id [" + id + "] type [" + type + "]" );
        }

        return requestedReport;
    }

    public Map getReportMap()
    {
        return Collections.unmodifiableMap( reportMap );
    }

    public void initialize()
        throws InitializationException
    {
        reportMap = new HashMap();

        Iterator it = availableReports.iterator();
        while ( it.hasNext() )
        {
            Report report = (Report) it.next();

            Map typeMap = (Map) reportMap.get( report.getId() );
            if ( typeMap == null )
            {
                typeMap = new HashMap();
            }

            typeMap.put( report.getType(), report );
            reportMap.put( report.getId(), typeMap );
        }
    }
}
