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

    private String roleName;

    private String description;

    private List permissions;

    private List childRoles;

    private SimplePermission addpermission;

    private String submitMode;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        if ( permissions == null )
        {
            permissions = new ArrayList();
        }

        if ( childRoles == null )
        {
            childRoles = new ArrayList();
        }

        if ( addpermission == null )
        {
            addpermission = new SimplePermission();
        }

        return INPUT;
    }

    public String addpermission()
    {
        if ( addpermission == null )
        {
            addActionError( "Unable to add null permission." );
            return ERROR;
        }
        
        if ( permissions == null )
        {
            permissions = new ArrayList();
        }
        
        permissions.add( addpermission );

        addpermission = new SimplePermission();

        return INPUT;
    }

    public String submit()
    {
        if ( StringUtils.equals( getSubmitMode(), "addPermission" ) )
        {
            return addpermission();
        }

        if ( StringUtils.isEmpty( roleName ) )
        {
            addActionError( "Unable to create role with empty name." );
            return ERROR;
        }

        try
        {
            Role _role;
            if ( manager.roleExists( roleName ) )
            {
                _role = manager.getRole( roleName );
            }
            else
            {
                _role = manager.createRole( roleName );
            }

            _role.setDescription( description );
            _role.setChildRoleNames( childRoles );

            List _permissionList = new ArrayList();
            Iterator it = permissions.iterator();
            while ( it.hasNext() )
            {
                SimplePermission perm = (SimplePermission) it.next();
                _permissionList.add( manager.createPermission( perm.getName(), perm.getOperationName(), perm
                    .getResourceIdentifier() ) );
            }

            _role.setPermissions( _permissionList );

            manager.saveRole( _role );

            addActionMessage( "Successfully Saved Role '" + roleName + "'" );
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Role '" + roleName + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Role '" + roleName + "': Role not found." );
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

    public SimplePermission getAddpermission()
    {
        return addpermission;
    }

    public void setAddpermission( SimplePermission addpermission )
    {
        this.addpermission = addpermission;
    }

    public String getSubmitMode()
    {
        return submitMode;
    }

    public void setSubmitMode( String submitMode )
    {
        this.submitMode = submitMode;
    }
}
