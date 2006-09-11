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
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.List;

/**
 * RoleActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityRole"
 */
public class RoleActions
    extends PlexusActionSupport
    implements Preparable, ModelDriven
{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String roleName;

    private String assignPermissionName;

    private String childRoleName;

    private String removePermissionName;

    private String removeRoleName;

    private Role role;

    private List assignablePermissions;

    private List assignableRoles;

    public void prepare()
        throws Exception
    {
        assignablePermissions = manager.getAllPermissions();
        getLogger().info( "Permissions to render " + assignablePermissions.size() );
        assignableRoles = manager.getAllRoles();

        if ( manager.roleExists( roleName ) )
        {
            role = manager.getRole( roleName );
            roleName = role.getName();
        }
        else
        {
            role = manager.createRole( "name", "description" );
        }
    }

    public String save()
        throws RbacActionException
    {
        try
        {
            if ( manager.roleExists( role ) )
            {
                Role temp = manager.getRole( role.getName() );

                temp.setName( role.getName() );
                temp.setDescription( role.getDescription() );
                temp.setAssignable( role.isAssignable() );

                if ( StringUtils.isNotEmpty( childRoleName ) )
                {
                    temp.addChildRole( manager.getRole( childRoleName ) );
                }

                if ( StringUtils.isNotEmpty( assignPermissionName ) )
                {
                    temp.addPermission( manager.getPermission( assignPermissionName ) );
                }

                manager.updateRole( temp );
            }
            else
            {
                if ( StringUtils.isNotEmpty( childRoleName ) )
                {
                    role.addChildRole( manager.getRole( childRoleName ) );
                }

                if ( StringUtils.isNotEmpty( assignPermissionName ) )
                {
                    role.addPermission( manager.getPermission( assignPermissionName ) );
                }
                manager.addRole( role );
            }
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( ne );
        }

        return SUCCESS;
    }

    public String removeAssignedRole()
        throws RbacActionException
    {
        try
        {
            Role temp = manager.getRole( roleName );

            temp.getChildRoles().remove( manager.getRole( childRoleName ) );

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
            Role temp = manager.getRole( roleName );

            List permissions = temp.getPermissions();

            permissions.remove( manager.getPermission( removePermissionName ) );

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
            manager.removeRole( manager.getRole( roleName ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate role to remove", ne );
        }

        return SUCCESS;
    }

    public String getChildRoleName()
    {
        return childRoleName;
    }

    public void setChildRoleName( String childRoleName )
    {
        this.childRoleName = childRoleName;
    }

    public String getAssignPermissionName()
    {
        return assignPermissionName;
    }

    public void setAssignPermissionName( String assignPermissionName )
    {
        this.assignPermissionName = assignPermissionName;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName( String roleName )
    {
        this.roleName = roleName;
    }

    public String getRemovePermissionName()
    {
        return removePermissionName;
    }

    public void setRemovePermissionName( String removePermissionName )
    {
        this.removePermissionName = removePermissionName;
    }

    public String getRemoveRoleName()
    {
        return removeRoleName;
    }

    public void setRemoveRoleName( String removeRoleName )
    {
        this.removeRoleName = removeRoleName;
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
