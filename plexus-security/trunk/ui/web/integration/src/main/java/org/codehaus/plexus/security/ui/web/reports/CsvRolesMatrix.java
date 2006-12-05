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

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.util.RoleSorter;
import org.codehaus.plexus.security.ui.web.util.UserComparator;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CsvRolesMatrix 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.ui.web.reports.Report" role-hint="rolesmatrix-csv"
 */
public class CsvRolesMatrix
    implements Report
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    /**
     * @plexus.requirement
     */
    private RBACManager rbacManager;

    public String getName()
    {
        return "Roles Matrix";
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
        List allRoles;
        Map assignmentsMap;

        try
        {
            allRoles = rbacManager.getAllRoles();
            Collections.sort( allRoles, new RoleSorter() );

            List allAssignments = rbacManager.getAllUserAssignments();
            assignmentsMap = new HashMap();

            Iterator it = allAssignments.iterator();
            while ( it.hasNext() )
            {
                UserAssignment assignment = (UserAssignment) it.next();
                assignmentsMap.put( assignment.getPrincipal(), assignment.getRoleNames() );
            }
        }
        catch ( RbacManagerException e )
        {
            throw new ReportException( "Unable to obtain list of all roles.", e );
        }

        Collections.sort( allUsers, new UserComparator( "username", true ) );

        PrintWriter out = new PrintWriter( os );

        writeCsvHeader( out, allRoles );

        Iterator itUsers = allUsers.iterator();
        while ( itUsers.hasNext() )
        {
            User user = (User) itUsers.next();
            writeCsvRow( out, user, assignmentsMap, allRoles );
        }

        out.flush();
    }

    private void writeCsvHeader( PrintWriter out, List allRoles )
    {
        out.print( "Username,Full Name,Email Address" );
        Iterator itRoles = allRoles.iterator();
        while ( itRoles.hasNext() )
        {
            Role role = (Role) itRoles.next();
            out.print( "," + escapeCell( role.getName() ) );
        }
        out.println();
    }

    private void writeCsvRow( PrintWriter out, User user, Map assignmentsMap, List allRoles )
    {
        out.print( escapeCell( user.getUsername() ) );
        out.print( "," + escapeCell( user.getFullName() ) );
        out.print( "," + escapeCell( user.getEmail() ) );

        List assignedRoleNames = (List) assignmentsMap.get( user.getPrincipal().toString() );
        if ( assignedRoleNames == null )
        {
            assignedRoleNames = new ArrayList();
        }
        Iterator itRoles = allRoles.iterator();
        while ( itRoles.hasNext() )
        {
            Role role = (Role) itRoles.next();
            out.print( ',' );
            if ( assignedRoleNames.contains( role.getName() ) )
            {
                out.print( 'Y' );
            }
        }
        out.println();
    }

    private String escapeCell( String cell )
    {
        return "\"" + StringEscapeUtils.escapeJava( cell ) + "\"";
    }

    public String getId()
    {
        return "rolesmatrix";
    }

    public String getMimeType()
    {
        return "text/csv";
    }
}
