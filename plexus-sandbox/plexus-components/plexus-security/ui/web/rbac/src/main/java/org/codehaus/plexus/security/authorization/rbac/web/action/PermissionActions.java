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
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import java.util.List;

/**
 * PermissionActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityPermission"
 */
public class PermissionActions
    extends PlexusActionSupport
    implements Preparable, ModelDriven

{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private String permissionName;

    private Permission permission;

    private String operationName;

    private String resourceIdentifier;

    private List operations;

    private List resources;

    private boolean globalResource;

    public void prepare()
        throws Exception
    {
        operations = manager.getAllOperations();
        resources = manager.getAllResources();

        if ( permission == null )
        {
            if ( manager.permissionExists( permissionName ) )
            {
                permission = manager.getPermission( permissionName );
                permissionName = permission.getName();
            }
            else
            {
                permission = manager.createPermission( "name" );
            }
        }
    }

    public Object getModel()
    {
        return permission;
    }


    public String save()
        throws RbacActionException
    {
        Permission temp = manager.createPermission( permission.getName(), operationName, resourceIdentifier );

        temp.setDescription( permission.getDescription() );

        manager.savePermission( temp );

        return SUCCESS;
    }

    public String remove()
        throws RbacActionException
    {
        try
        {
            manager.removePermission( manager.getPermission( permissionName ) );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate permission to remove " + permissionName, ne );
        }
        return SUCCESS;
    }

    public String getPermissionName()
    {
        return permissionName;
    }

    public void setPermissionName( String name )
    {
        this.permissionName = name;
    }

    public Permission getPermission()
    {
        return permission;
    }

    public void setPermission( Permission permission )
    {
        this.permission = permission;
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

    public List getOperations()
    {
        return operations;
    }

    public void setOperations( List operations )
    {
        this.operations = operations;
    }

    public List getResources()
    {
        return resources;
    }

    public void setResources( List resources )
    {
        this.resources = resources;
    }

    public boolean isGlobalResource()
    {
        return globalResource;
}
    public void setGlobalResource( boolean globalResource )
    {
        this.globalResource = globalResource;
    }
}
