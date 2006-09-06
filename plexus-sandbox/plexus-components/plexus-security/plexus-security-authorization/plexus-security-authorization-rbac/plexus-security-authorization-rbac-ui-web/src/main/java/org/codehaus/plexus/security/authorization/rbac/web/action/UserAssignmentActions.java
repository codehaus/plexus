package org.codehaus.plexus.security.authorization.rbac.web.action;

import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

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
 * UserAssignmentActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityUserAssignment"
 */
public class UserAssignmentActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RbacStore store;

    private int roleId;

    private String principal;

    private List assignedRoles;

    private List availableRoles;

    public String display()
        throws RbacStoreException
    {
        assignedRoles = store.getRoleAssignments( principal );

        availableRoles = store.getAssignableRoles();

        return SUCCESS;
    }

    public String assignRole()
        throws RbacStoreException
    {
        store.addRoleAssignment( principal, roleId );

        return SUCCESS;
    }


    public String removeRole()
        throws RbacStoreException
    {
        store.removeRoleAssignment( principal, roleId );

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

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( String principal )
    {
        this.principal = principal;
    }

    public List getAssignedRoles()
    {
        return assignedRoles;
    }

    public void setAssignedRoles( List assignedRoles )
    {
        this.assignedRoles = assignedRoles;
    }

    public List getAvailableRoles()
    {
        return availableRoles;
    }

    public void setAvailableRoles( List availableRoles )
    {
        this.availableRoles = availableRoles;
    }

}


