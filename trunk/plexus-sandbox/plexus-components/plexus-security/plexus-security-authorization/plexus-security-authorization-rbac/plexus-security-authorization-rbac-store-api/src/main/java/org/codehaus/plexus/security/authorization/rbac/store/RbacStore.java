package org.codehaus.plexus.security.authorization.rbac.store;


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

import org.codehaus.plexus.security.authorization.rbac.Permission;
import org.codehaus.plexus.security.authorization.rbac.Role;

import java.util.List;


/**
 * RbacStore:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 */
public interface RbacStore
{
    public static final String ROLE = RbacStore.class.getName();


    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------
    public void addRole( Role role)
        throws RbacStoreException;

    public Role getRole( int roleId )
        throws RbacStoreException;

    public List getAllRoles()
        throws RbacStoreException;

    public List getAssignableRoles()
        throws RbacStoreException;


    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------
    public void addPermission( int roleId, Permission permission )
        throws RbacStoreException;

    public List getPermissions( int roleId )
        throws RbacStoreException;


    // ----------------------------------------------------------------------
    // User Assignment methods
    // ----------------------------------------------------------------------

    public List getRoleAssignments( String principal )
        throws RbacStoreException;

    public void addRoleAssignment( String principal, int roleId )
        throws RbacStoreException;

    public void removeRoleAssignment( String principal, int roleId )
        throws RbacStoreException;
}
