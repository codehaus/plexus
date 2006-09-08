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
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

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

    private int roleId;

    private String principal;

    private List assignedRoles;

    private List availableRoles;

    public String display()
        throws RbacActionException
    {
        try
        {
            assignedRoles = manager.getAssignedRoles( principal );

            availableRoles = manager.getAllAssignableRoles();
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
            UserAssignment assignment = manager.createUserAssignment( principal );

            assignment.getRoles().addRole( manager.getRole( roleId ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role to assign", ne );
        }

        return SUCCESS;
    }


    public String removeRole()
        throws RbacActionException
    {
        try
        {
            UserAssignment assignment = manager.getUserAssignment( principal );

            assignment.getRoles().removeRole( manager.getRole( roleId ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( ne );
        }

        return SUCCESS;
    }


    public int getRoleId()
    {
        return roleId;
    }

    public void setRoleId( int roleId )
    {
        this.roleId = roleId;
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


