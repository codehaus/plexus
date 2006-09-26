package org.codehaus.plexus.security.ui.web.action.admin;

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

import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.ui.web.action.AbstractSecurityAction;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * EditRoleAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-role-edit"
 *                   instantiation-strategy="per-lookup"
 */
public class EditRoleAction
    extends AbstractSecurityAction
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

    private String name;

    private String description;
    
    private List childRoleNames = new ArrayList();
    
    private List permissions = new ArrayList();
    
    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String input()
    {
        if ( name == null )
        {
            addActionError( "Unable to edit null role name." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( name ) )
        {
            addActionError( "Unable to edit empty role name." );
            return ERROR;
        }
        
        if( !manager.roleExists( name ))
        {
            // Means that the role name doesn't exist.
            // We should exit early and not attempt to look up the role information.
            return INPUT;
        }
        
        try
        {
            Role role = manager.getRole( name );
            if ( role == null )
            {
                addActionError( "Unable to operate on null role." );
                return ERROR;
            }

            description = role.getDescription();
            childRoleNames = role.getChildRoleNames();
            permissions = role.getPermissions();
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Role '" + name + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Role '" + name + "': Role not found." );
            return ERROR;
        }

        return INPUT;
    }
    
    public String submit()
    {
        if ( name == null )
        {
            addActionError( "Unable to edit null role name." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( name ) )
        {
            addActionError( "Unable to edit empty role name." );
            return ERROR;
        }
        
        try
        {
            Role role;
            if ( manager.roleExists( name ) )
            {
                role = manager.getRole( name );
            }
            else
            {
                role = manager.createRole( name );
            }
            
            role.setDescription( description );
            role.setChildRoleNames( childRoleNames );
            role.setPermissions( permissions );
            
            manager.saveRole( role );
            
            addActionMessage( "Successfully Saved Role '" + name + "'" );
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Role '" + name + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Role '" + name + "': Role not found." );
            return ERROR;
        }        
        
        return SUCCESS;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String roleName )
    {
        this.name = roleName;
    }

    public List getChildRoleNames()
    {
        return childRoleNames;
    }

    public void setChildRoleNames( List childRoleNames )
    {
        this.childRoleNames = childRoleNames;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public List getPermissions()
    {
        return permissions;
    }

    public void setPermissions( List permissions )
    {
        this.permissions = permissions;
    }
    
    // ------------------------------------------------------------------
    // Internal Support Methods
    // ------------------------------------------------------------------

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_RBAC_ADMIN_OPERATION, Resource.GLOBAL );
        return bundle;
    }
    
}
