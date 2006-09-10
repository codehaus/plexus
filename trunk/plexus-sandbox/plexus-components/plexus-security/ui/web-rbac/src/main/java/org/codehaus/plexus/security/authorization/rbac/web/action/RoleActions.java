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

    private int assignedPermissionId;

    private int assignedRoleId;

    private int removePermissionId;

    private int removeRoleId;

    private Role role;

    private List assignablePermissions;

    private List assignableRoles;

    public void prepare()
    {
        assignablePermissions = manager.getAllPermissions();
        getLogger().info( "Permissions to render " + assignablePermissions.size() );
        assignableRoles = manager.getAllRoles();

        try
        {
            role = manager.getRole( roleId );
            getLogger().info(" contains permissions: " + role.getPermissions().size() );
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

            if ( assignedRoleId != 0 )
            {
                Roles childRoles = temp.getChildRoles();
                childRoles.addRole( manager.getRole( assignedRoleId ) );
                temp.setChildRoles( childRoles );

            }

            if ( assignedPermissionId != 0 )
            {
                List permissions = temp.getPermissions();
                permissions.add( manager.getPermission( assignedPermissionId ) );

                temp.setPermissions( permissions );
            }

            manager.updateRole( temp );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            manager.addRole( role );
        }

        return SUCCESS;
    }

    public String removeAssignedRole()
        throws RbacActionException
    {
        try
        {
            Role temp = manager.getRole( roleId );

            Roles childRoles = temp.getChildRoles();

            childRoles.removeRole( manager.getRole( removeRoleId ) );

            manager.updateRole( temp );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate assigned role to remove", ne );
        }

        return SUCCESS;
    }

    public String removeAssignedPermission()
        throws RbacActionException
    {
        try
        {
            Role temp = manager.getRole( roleId );

            List permissions = temp.getPermissions();

            permissions.remove( manager.getPermission( removePermissionId ) );

            manager.updateRole( temp );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate permission to remove", ne );
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

    public int getAssignedPermissionId()
    {
        return assignedPermissionId;
    }

    public void setAssignedPermissionId( int assignedPermissionId )
    {
        this.assignedPermissionId = assignedPermissionId;
    }

    public int getAssignedRoleId()
    {
        return assignedRoleId;
    }

    public void setAssignedRoleId( int assignedRoleId )
    {
        this.assignedRoleId = assignedRoleId;
    }

    public int getRemovePermissionId()
    {
        return removePermissionId;
    }

    public void setRemovePermissionId( int removePermissionId )
    {
        this.removePermissionId = removePermissionId;
    }

    public int getRemoveRoleId()
    {
        return removeRoleId;
    }

    public void setRemoveRoleId( int removeRoleId )
    {
        this.removeRoleId = removeRoleId;
    }

    public Object getModel()
    {
        return role;
    }

    public void setRole( Role role )
    {
        this.role = role;
    }

    public List getAssignablePermissions()
    {
        return assignablePermissions;
    }

    public void setAssignablePermissions( List assignablePermissions )
    {
        this.assignablePermissions = assignablePermissions;
    }

    public List getAssignableRoles()
    {
        return assignableRoles;
    }

    public void setAssignableRoles( List assignableRoles )
    {
        this.assignableRoles = assignableRoles;
    }
}
