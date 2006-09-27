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

import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;
import org.codehaus.plexus.security.ui.web.util.PermissionSorter;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.Collections;
import java.util.List;

/**
 * PermissionsAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-permissions"
 *                   instantiation-strategy="per-lookup"
 */
public class PermissionsAction
    extends PlexusActionSupport
{
    private static final String LIST = "list";

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

    private String operationName;

    private String operationDescription;

    private String resourceIdentifier;

    private List allPermissions;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String list()
    {
        allPermissions = manager.getAllPermissions();
        
        if(allPermissions == null)
        {
            allPermissions = Collections.EMPTY_LIST;
        }
        
        Collections.sort( allPermissions, new PermissionSorter() );

        return LIST;
    }

    public String input()
    {
        if ( name == null )
        {
            addActionError( "Unable to edit null permission name." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( name ) )
        {
            addActionError( "Unable to edit empty permission name." );
            return ERROR;
        }

        if ( !manager.permissionExists( name ) )
        {
            // Means that the permission name doesn't exist.
            // We should exit early and not attempt to look up the permission information.
            return LIST;
        }

        try
        {
            Permission permission = manager.getPermission( name );
            if ( permission == null )
            {
                addActionError( "Unable to operate on null permission." );
                return ERROR;
            }

            description = permission.getDescription();
            Operation operation = permission.getOperation();
            if ( operation != null )
            {
                operationName = operation.getName();
                operationDescription = operation.getDescription();
            }

            Resource resource = permission.getResource();
            if ( resource != null )
            {
                resourceIdentifier = resource.getIdentifier();
            }
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Permission '" + name + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Permission '" + name + "': Permission not found." );
            return ERROR;
        }

        return LIST;
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
            Permission permission;
            if ( manager.permissionExists( name ) )
            {
                permission = manager.getPermission( name );
            }
            else
            {
                permission = manager.createPermission( name );
            }

            permission.setDescription( description );

            Operation operation = manager.createOperation( operationName );
            if ( StringUtils.isNotEmpty( operationDescription ) )
            {
                operation.setDescription( operationDescription );
            }
            permission.setOperation( manager.saveOperation( operation ) );

            Resource resource = manager.createResource( resourceIdentifier );
            permission.setResource( manager.saveResource( resource ) );

            manager.savePermission( permission );

            addActionMessage( "Successfully Saved Permission '" + name + "'" );
        }
        catch ( RbacStoreException e )
        {
            addActionError( "Unable to get Permission '" + name + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( RbacObjectNotFoundException e )
        {
            addActionError( "Unable to get Permission '" + name + "': Permission not found." );
            return ERROR;
        }

        return LIST;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getOperationDescription()
    {
        return operationDescription;
    }

    public void setOperationDescription( String operationDescription )
    {
        this.operationDescription = operationDescription;
    }

    public String getOperationName()
    {
        return operationName;
    }

    public void setOperationName( String operationName )
    {
        this.operationName = operationName;
    }

    public String getResourceIdentifier()
    {
        return resourceIdentifier;
    }

    public void setResourceIdentifier( String resourceIdentifier )
    {
        this.resourceIdentifier = resourceIdentifier;
    }

    public List getAllPermissions()
    {
        return allPermissions;
    }

    public void setAllPermissions( List allPermissions )
    {
        this.allPermissions = allPermissions;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_RBAC_ADMIN_OPERATION, Resource.GLOBAL );
        return bundle;
    }
}
