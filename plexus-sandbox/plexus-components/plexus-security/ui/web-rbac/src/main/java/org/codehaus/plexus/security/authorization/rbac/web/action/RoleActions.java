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


import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.Preparable;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.Roles;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.List;

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
    implements Preparable, ModelDriven
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private int roleId;

    private int permissionId;

    private int childRoleId;

    private Role role;

    private List permissions;

    private List roles;

    public void prepare()
    {
        permissions = manager.getAllPermissions();
        roles = manager.getAllRoles();

        try
        {
            role = manager.getRole( roleId );
            roleId = role.getId();
        }
        catch ( RbacObjectNotFoundException ne )
        {
            role = manager.createRole( "name", "description" );
        }
    }

    public String save()
        throws RbacActionException
    {
        try
        {
            Role temp = manager.getRole( role.getId() );

            temp.setName( role.getName() );
            temp.setDescription( role.getDescription() );
            temp.setAssignable( role.isAssignable() );

            if ( childRoleId != 0 )
            {
                Roles childRoles = temp.getChildRoles();
                childRoles.addRole( manager.getRole( childRoleId ) );
            }

            if ( permissionId != 0 )
            {
                List permissions = temp.getPermissions();
                permissions.add( manager.getPermission( permissionId ) );
            }

            manager.updateRole( temp );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            manager.addRole( role );
        }

        return SUCCESS;
    }

    public String removeChildRole()
        throws RbacActionException
    {
        try
        {
            Role temp = manager.getRole( role.getId() );

            Roles childRoles = temp.getChildRoles();

            childRoles.removeRole( manager.getRole( childRoleId ) );

            manager.updateRole( temp );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role to remove", ne );
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

    public int getPermissionId()
    {
        return permissionId;
    }

    public void setPermissionId( int permissionId )
    {
        this.permissionId = permissionId;
    }

    public int getChildRoleId()
    {
        return childRoleId;
    }

    public void setChildRoleId( int childRoleId )
    {
        this.childRoleId = childRoleId;
    }

    public Object getModel()
    {
        return role;
    }

    public void setRole( Role role )
    {
        this.role = role;
    }

    public List getPermissions()
    {
        return permissions;
    }

    public void setPermissions( List permissions )
    {
        this.permissions = permissions;
    }

    public List getRoles()
    {
        return roles;
    }

    public void setRoles( List roles )
    {
        this.roles = roles;
    }
}
