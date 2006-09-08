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

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;
import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.Preparable;

/**
 * PermissionActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="plexusSecurityPermission"
 */
public class PermissionActions
    extends PlexusActionSupport implements ModelDriven, Preparable

{
    /**
     * @plexus.requirement
     */
    private RBACManager manager;

    private int permissionId;

    private Permission permission;

    public void prepare()
        throws Exception
    {
        try
        {
            permission = manager.getPermission( permissionId );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to location permission " + permissionId, ne );
        }
    }

    public Object getModel()
    {
        return permission;
    }

    public String display()
    {
        return SUCCESS;
    }

    public String add()
    {
        manager.addPermission( permission );

        return SUCCESS;
    }

    public String remove()
        throws RbacActionException
    {
        try
        {
            manager.removePermission( manager.getPermission( permissionId );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RbacActionException( "unable to locate permission to remove " + permissionId , ne );
        }
        return SUCCESS;
    }    

    public int getPermissionId()
    {
        return permissionId;
    }

    public void setPermissionId( int permissionId )
    {
        this.permissionId = permissionId;
    }

    public void setPermission( Permission permission )
    {
        this.permission = permission;
    }
}
