package org.codehaus.plexus.security.ui.web.reports;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.util.UserComparator;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * CsvUserList 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.ui.web.reports.Report" role-hint="userlist-csv" 
 */
public class CsvUserList
    extends AbstractLogEnabled
    implements Report
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private Map fields;

    public CsvUserList()
    {
        fields = new HashMap();
        fields.put( "username", "User Name" );
        fields.put( "fullName", "Full Name" );
        fields.put( "email", "Email Address" );
        fields.put( "permanent", "Permanent User" );
        fields.put( "locked", "Locked User" );
        fields.put( "validated", "Validated User" );
        fields.put( "passwordChangeRequired", "Must Change Password On Next Login" );
        fields.put( "countFailedLoginAttempts", "Failed Login Attempts" );
        fields.put( "lastPasswordChange", "Last Password Change" );
        fields.put( "accountCreationDate", "Date Created" );
        fields.put( "lastLoginDate", "Date Last Logged In" );
    }

    public String getId()
    {
        return "userlist";
    }

    public String getMimeType()
    {
        return "text/csv";
    }

    public String getName()
    {
        return "User List";
    }

    public String getType()
    {
        return "csv";
    }

    public void writeReport( OutputStream os )
        throws ReportException
    {
        UserManager userManager = securitySystem.getUserManager();

        List allUsers = userManager.getUsers();

        Collections.sort( allUsers, new UserComparator( "username", true ) );

        PrintWriter out = new PrintWriter( os );

        writeCsvHeader( out, allUsers );

        Iterator itUsers = allUsers.iterator();
        while ( itUsers.hasNext() )
        {
            User user = (User) itUsers.next();
            writeCsvRow( out, user );
        }

        out.flush();
    }

    private void writeCsvHeader( PrintWriter out, List allUsers )
    {
        boolean hasPreviousField = false;
        Iterator it = fields.entrySet().iterator();
        while ( it.hasNext() )
        {
            Map.Entry field = (Entry) it.next();
            if ( hasPreviousField )
            {
                out.print( "," );
            }
            String heading = (String) field.getValue();
            out.print( escapeCell( heading ) );
            hasPreviousField = true;
        }
        out.println();
    }

    private void writeCsvRow( PrintWriter out, User user )
        throws ReportException
    {
        try
        {
            boolean hasPreviousField = false;
            Map propMap = PropertyUtils.describe( user );
            Iterator it = fields.keySet().iterator();
            while ( it.hasNext() )
            {
                String propName = (String) it.next();
                Object propValue = propMap.get( propName );

                if ( hasPreviousField )
                {
                    out.print( "," );
                }

                if ( propValue != null )
                {
                    out.print( escapeCell( propValue.toString() ) );
                }
                hasPreviousField = true;
            }
            out.println();
        }
        catch ( IllegalAccessException e )
        {
            String emsg = "Unable to produce " + getName() + " report.";
            getLogger().error( emsg, e );
            throw new ReportException( emsg, e );
        }
        catch ( InvocationTargetException e )
        {
            String emsg = "Unable to produce " + getName() + " report.";
            getLogger().error( emsg, e );
            throw new ReportException( emsg, e );
        }
        catch ( NoSuchMethodException e )
        {
            String emsg = "Unable to produce " + getName() + " report.";
            getLogger().error( emsg, e );
            throw new ReportException( emsg, e );
        }
    }

    private String escapeCell( String cell )
    {
        return "\"" + StringEscapeUtils.escapeJava( cell ) + "\"";
    }
}
