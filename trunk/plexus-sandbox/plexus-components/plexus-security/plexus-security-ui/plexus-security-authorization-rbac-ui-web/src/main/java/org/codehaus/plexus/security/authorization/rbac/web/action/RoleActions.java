package org.codehaus.plexus.security.authorization.rbac.web.action;

import org.codehaus.plexus.xwork.action.PlexusActionSupport;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.security.authorization.rbac.Role;

import java.util.List;
/*
 * Copyright 2005 The Apache Software Foundation.
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
    private RbacStore store;

    private int roleId;

    private Role role;

    private List roles;

    public String display()
        throws RbacStoreException
    {
        role = store.getRole( roleId );

        return SUCCESS;
    }

    public String summary()
        throws RbacStoreException
    {
        roles = store.getAllRoles();

        return SUCCESS;
    }

    public String addRole()
        throws RbacStoreException
    {
        if ( role != null && store.getRole( role.getId() ) == null)
        {
            store.addRole( role );
        }
        else
        {
            addActionError("unable to add role, its either null or exists already" );
            return ERROR;
        }

        return SUCCESS;
    }

    public String removeRole()
        throws RbacStoreException
    {
        store.removeRole( roleId );

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

    public List getRoles()
    {
        return roles;
    }

    public void setRoles( List roles )
    {
        this.roles = roles;
    }

}
