package org.codehaus.plexus.security.ui.web.action.admin;

/*
 * Copyright 2001-2006 The Codehaus.
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

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.ui.web.action.AbstractSecurityAction;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.reports.Report;
import org.codehaus.plexus.security.ui.web.reports.ReportException;
import org.codehaus.plexus.security.ui.web.reports.ReportManager;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.extremecomponents.table.context.HttpServletRequestContext;

/**
 * ReportAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-report"
 * instantiation-strategy="per-lookup"
 */
public class ReportAction
    extends AbstractSecurityAction
{
    private static final String DOWNLOAD = "download";

    /**
     * @plexus.requirement
     */
    private ReportManager reportManager;

    private String reportId;

    private String reportType;
    
    private Report report;

    public String generate()
    {
        try
        {
            this.report = reportManager.findReport( reportId, reportType );
        }
        catch ( ReportException e )
        {
            addActionError( "Unable to find report: " + e.getMessage() );
            return ERROR;
        }
        
        ServletActionContext.getRequest().setAttribute( "report", report );
        
        return DOWNLOAD;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_LIST_OPERATION, Resource.GLOBAL );
        return bundle;
    }

    public String getReportId()
    {
        return reportId;
    }

    public void setReportId( String reportId )
    {
        this.reportId = reportId;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }
}
