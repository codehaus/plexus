package org.codehaus.plexus.security.authorization.rbac.web.model;

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

import java.util.ArrayList;
import java.util.List;

/**
 * RoleDetails - this is a placeholder for information passed back 
 * and forth between the Action and the Client.
 * 
 * We intentionally do not hook up the actual object to prevent
 * creative injection of fields and values by the untrusted client. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class RoleDetails
{
    private String name;

    private String description;

    private boolean assignable;

    private List childRoleNames = new ArrayList();

    private List permissions = new ArrayList();

    public void addChildRoleName( String name )
    {
        childRoleNames.add( name );
    }

    public void addPermission( String permissionName, String operationName, String resourceIdentifier )
    {
        SimplePermission permission = new SimplePermission();
        permission.setName( permissionName );
        permission.setOperationName( operationName );
        permission.setResourceIdentifier( resourceIdentifier );

        permissions.add( permission );
    }

    public List getChildRoleNames()
    {
        return childRoleNames;
    }

    public boolean isAssignable()
    {
        return assignable;
    }

    public void setAssignable( boolean assignable )
    {
        this.assignable = assignable;
    }

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

    public List getPermissions()
    {
        return permissions;
    }

    public void setPermissions( List permissions )
    {
        this.permissions = permissions;
    }

    public void setChildRoleNames( List childRoleNames )
    {
        this.childRoleNames = childRoleNames;
    }
}
