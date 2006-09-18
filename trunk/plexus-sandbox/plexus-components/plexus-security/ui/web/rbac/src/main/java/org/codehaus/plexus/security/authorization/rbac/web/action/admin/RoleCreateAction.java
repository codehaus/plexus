package org.codehaus.plexus.security.authorization.rbac.web.action.admin;

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

import org.codehaus.plexus.security.authorization.rbac.web.model.CreateRoleDetails;
import org.codehaus.plexus.security.authorization.rbac.web.model.SimplePermission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RoleCreateAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-role-create"
 *                   instantiation-strategy="per-lookup"
 */
public class RoleCreateAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String principal;

    private CreateRoleDetails role;

    private SimplePermission addpermission;

    private boolean addPermissionButton;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        role = new CreateRoleDetails();

        addpermission = new SimplePermission();

        return INPUT;
    }

    public String addpermission()
    {
        if ( addpermission == null )
        {
            addActionError( "Unable to add null permission." );
            return ERROR;
        }

        role.addPermission( addpermission.getName(), addpermission.getOperationName(), addpermission
            .getResourceIdentifier() );

        addpermission = new SimplePermission();

        return INPUT;
    }

    public String submit()
    {
        if ( role == null )
        {
            addActionError( "Unable to create null role." );
            return ERROR;
        }

        if ( addPermissionButton )
        {
            return addpermission();
        }

        if ( StringUtils.isEmpty( role.getName() ) )
        {
            addActionError( "Unable to create role with empty name." );
            return ERROR;
        }

        try
        {
            Role _role;
            if ( manager.roleExists( role.getName() ) )
            {
                _role = manager.getRole( role.getName() );
            }
            else
            {
                _role = manager.createRole( role.getName() );
            }

            _role.setDescription( role.getDescription() );
            _role.setChildRoleNames( role.getChildRoleNames() );

            List permissionList = new ArrayList();
            Iterator it = role.getPermissions().iterator();
            while ( it.hasNext() )
            {
                SimplePermission perm = (SimplePermission) it.next();
                permissionList.add( manager.createPermission( perm.getName(), perm.getOperationName(), perm
                    .getResourceIdentifier() ) );
            }

            _role.setPermissions( permissionList );

            manager.saveRole( _role );

            addActionMessage( "Successfully Saved Role '" + role.getName() + "'" );
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Role '" + role.getName() + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Role '" + role.getName() + "': Role not found." );
            return ERROR;
        }

        return SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public CreateRoleDetails getRole()
    {
        return role;
    }

    public void setRole( CreateRoleDetails role )
    {
        this.role = role;
    }

    public SimplePermission getAddpermission()
    {
        return addpermission;
    }

    public void setAddpermission( SimplePermission addpermission )
    {
        this.addpermission = addpermission;
    }

    public boolean isAddPermissionButton()
    {
        return addPermissionButton;
    }

    public void setAddPermissionButton( boolean addPermissionButton )
    {
        this.addPermissionButton = addPermissionButton;
    }

}
