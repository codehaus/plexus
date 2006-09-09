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


import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * RoleActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityRole"
 */
public class RoleActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private int roleId;

    private Role role;

    public String display()
        throws RbacActionException
    {
        try
        {
            role = manager.getRole( roleId );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role", ne );
        }

        return SUCCESS;
    }

    public String addRole()
        throws RbacActionException
    {
        if ( role != null )
        {
            manager.addRole( role );
        }
        else
        {
            addActionError( "unable to add role, its either null or exists already" );
            return ERROR;
        }

        return SUCCESS;
    }

    public String removeRole()
        throws RbacActionException
    {
        try
        {
            manager.removeRole( manager.getRole( roleId ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role to remove", ne );
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

    public Role getRole()
    {
        return role;
    }

    public void setRole( Role role )
    {
        this.role = role;
    }
}
