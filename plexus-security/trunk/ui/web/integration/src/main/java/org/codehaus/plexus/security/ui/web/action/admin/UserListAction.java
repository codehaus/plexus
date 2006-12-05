package org.codehaus.plexus.security.ui.web.action.admin;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.ui.web.action.AbstractSecurityAction;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.reports.ReportManager;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * UserListAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-admin-user-list"
 * instantiation-strategy="per-lookup"
 */
public class UserListAction
    extends AbstractSecurityAction
{
    private final static Map SEARCH_CRITERIA = new HashMap();

    static
    {
        SEARCH_CRITERIA.put( "username", "Username contains" );
        SEARCH_CRITERIA.put( "fullName", "Name contains" );
        SEARCH_CRITERIA.put( "email", "Email contains" );
    }

    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private UserManager manager;

    /**
     * @plexus.requirement
     */
    private RBACManager rbac;
    
    /**
     * @plexus.requirement
     */
    private ReportManager reportManager;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private List users;

    private List roles;

    private String roleName;

    private String searchKey;

    private String criterion;
    
    private boolean ascending = true;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        try
        {
            roles = rbac.getAllRoles();
        }
        catch ( RbacManagerException e )
        {
            roles = Collections.EMPTY_LIST;
        }

        if ( StringUtils.isEmpty( roleName ) )
        {
            if ( StringUtils.isEmpty( searchKey ) )
            {
                users = manager.getUsers( ascending );
            }
            else
            {
                users = findUsers( criterion, searchKey, ascending );
            }
        }
        else
        {
            try
            {
                Role target = rbac.getRole( roleName );
                Set targetRoleNames = new HashSet();

                for ( int i = 0; i < roles.size(); i++ )
                {
                    Role r = (Role) roles.get( i );
                    if ( rbac.getEffectiveRoles( r ).contains( target ) )
                    {
                        targetRoleNames.add( r.getName() );
                    }
                }

                users = findUsers( targetRoleNames, criterion, searchKey, ascending );
            }
            catch ( RbacObjectNotFoundException e )
            {
                users = Collections.EMPTY_LIST;
            }
            catch ( RbacManagerException e )
            {
                users = Collections.EMPTY_LIST;
            }
        }

        if ( users == null )
        {
            users = Collections.EMPTY_LIST;
        }

        return INPUT;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_LIST_OPERATION, Resource.GLOBAL );
        return bundle;
    }

    private List findUsers( String searchProperty, String searchKey, boolean orderAscending )
    {
        List users = null;

        if ( "username".equals( searchProperty ) )
        {
            users = manager.findUsersByUsernameKey( searchKey, orderAscending );
        }
        else if ( "fullName".equals( getCriterion() ) )
        {
            users = manager.findUsersByFullNameKey( searchKey, orderAscending );
        }
        else if ( "email".equals( getCriterion() ) )
        {
            users = manager.findUsersByEmailKey( searchKey, orderAscending );
        }
        else
        {
            users = Collections.EMPTY_LIST;
        }

        return users;
    }

    private List findUsers( Collection roleNames, String searchProperty, String searchKey, boolean orderAscending )
    {
        List usernames = getUsernamesForRoles( roleNames );
        List users = findUsers( searchProperty, searchKey, orderAscending );
        List filteredUsers = new ArrayList();

        for ( Iterator i = users.iterator(); i.hasNext(); )
        {
            User user = (User) i.next();
            if ( usernames.contains( user.getUsername() ) )
            {
                filteredUsers.add( user );
            }
        }

        return filteredUsers;
    }

    private List getUsernamesForRoles( Collection roleNames )
    {
        Set usernames = new HashSet();

        try
        {
            List userAssignments = rbac.getUserAssignmentsForRoles( roleNames );

            if ( userAssignments != null )
            {
                for ( int i = 0; i < userAssignments.size(); i++ )
                {
                    usernames.add( ( (UserAssignment) userAssignments.get( i ) ).getPrincipal() );
                }
            }
        }
        catch ( RbacManagerException e )
        {
        }

        return new ArrayList( usernames );
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public List getUsers()
    {
        return users;
    }

    public void setUsers( List users )
    {
        this.users = users;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName( String roleName )
    {
        this.roleName = roleName;
    }

    public String getCriterion()
    {
        return criterion;
    }

    public void setCriterion( String criterion )
    {
        this.criterion = criterion;
    }

    public String getSearchKey()
    {
        return searchKey;
    }

    public void setSearchKey( String searchKey )
    {
        this.searchKey = searchKey;
    }

    public List getRoles()
    {
        return roles;
    }

    public Map getCriteria()
    {
        return SEARCH_CRITERIA;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    public void setAscending( boolean ascending )
    {
        this.ascending = ascending;
    }
    
    public Map getReportMap()
    {
        return reportManager.getReportMap();
    }
}
