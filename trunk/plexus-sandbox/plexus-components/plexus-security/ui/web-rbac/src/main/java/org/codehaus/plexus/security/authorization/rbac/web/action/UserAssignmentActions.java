package org.codehaus.plexus.security.authorization.rbac.web.action;

/*
 * Copyright 2005 The Codehaus.
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


import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * UserAssignmentActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityUserAssignment"
 */
public class UserAssignmentActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String roleName;

    private String principal;

    private List assignedRoles;

    private List availableRoles;

    public String display()
        throws RbacActionException
    {
        try
        {
            principal = ((User)session.get( "user" )).getPrincipal().toString();

            if ( principal != null && manager.userAssignmentExists( principal ) )
            {
                getLogger().info( "recovering assigned roles" );
                assignedRoles = new ArrayList( manager.getAssignedRoles( principal ) );
                availableRoles = new ArrayList( manager.getUnassignedRoles( principal ) );
            }
            else
            {
                getLogger().info( "new assigned roles" );
                assignedRoles = new ArrayList();
                availableRoles = manager.getAllAssignableRoles();
            }

            getLogger().info( "assigned roles: " + assignedRoles.size() );
            getLogger().info( "available roles: " + availableRoles.size() );
        }
        catch ( RbacStoreException  se )
        {
            throw new RbacActionException( se );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( ne );
        }
        return SUCCESS;
    }

    public String assignRole()
        throws RbacActionException
    {
        try
        {
            if ( principal == null )
            {
                throw new RbacActionException( "principal can not be null" );
            }

            if ( manager.userAssignmentExists( principal ) )
            {
                UserAssignment assignment = manager.getUserAssignment( principal );
                assignment.addRole( manager.getRole( roleName ) );
                manager.saveUserAssignment( assignment );
            }
            else
            {
                UserAssignment assignment = manager.createUserAssignment( principal );
                assignment.addRole( manager.getRole( roleName ) );
                manager.saveUserAssignment( assignment );
            }
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role to assign", ne );
        }

        return SUCCESS;
    }

    /* TODO: We should be careful with use of 'remove' vs 'delete' or just spell it out.
     *
     * For example, this method should just 'detach' the role from this particular user assignment, not actually
     * delete the role from the underlying usermanager.
     *
     * To do that, a call to usermanager.removeRole() should remove the role entirely.
     *
     * TODO: Do we need the ability to do a reverse lookup.  Given a role, get a list of Users with it set?
     */
    public String removeRole()
        throws RbacActionException
    {
        try
        {
            if ( principal == null )
            {
                throw new RbacActionException( "principal can not be null" );
            }

            getLogger().info( "removing " + roleName + " for " + principal );

            UserAssignment assignment = manager.getUserAssignment( principal );

            List roles = assignment.getRoles();
            
            roles.remove( manager.getRole( roleName ) );

            assignment.setRoles( roles );

            manager.saveUserAssignment( assignment );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( ne );
        }

        return SUCCESS;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName( String roleName )
    {
        this.roleName = roleName;
    }

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public List getAssignedRoles()
    {
        return assignedRoles;
    }

    public void setAssignedRoles( List assignedRoles )
    {
        this.assignedRoles = assignedRoles;
    }

    public List getAvailableRoles()
    {
        return availableRoles;
    }

    public void setAvailableRoles( List availableRoles )
    {
        this.availableRoles = availableRoles;
    }

}


