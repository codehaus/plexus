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

import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;

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
    public Role addRole( Role role )
        throws RbacStoreException;

    public Role getRole( int roleId )
        throws RbacStoreException;

    public List getAllRoles()
        throws RbacStoreException;

    public List getAssignableRoles()
        throws RbacStoreException;

    public Role removeRole( int roleId )
        throws RbacStoreException;

    public Role removeRole( Role role )
        throws RbacStoreException;

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------

    public Permission addPermission( int roleId, Permission permission )
        throws RbacStoreException;

    public List getPermissions( int roleId )
        throws RbacStoreException;

    public List getAllPermissions()
        throws RbacStoreException;

    public Permission removePermissionFromRole( int roleId, int permissionId )
        throws RbacStoreException;

    public Permission getPermission( int permissionId )
        throws RbacStoreException;

    public Permission removePermission( int permissionId )
        throws RbacStoreException;

    // ----------------------------------------------------------------------
    // Operation methods
    // ----------------------------------------------------------------------
    public List getAllOperations()
        throws RbacStoreException;

    public Operation getOperation( int operationId )
        throws RbacStoreException;

    public Operation removeOperation( int operationId )
        throws RbacStoreException;

    // ----------------------------------------------------------------------
    // Resource methods
    // ----------------------------------------------------------------------
    public List getAllResources()
        throws RbacStoreException;

    public Resource getResource( int resourceId )
        throws RbacStoreException;

    public Resource removeResource( int resourceId )
        throws RbacStoreException;

    // ----------------------------------------------------------------------
    // User Assignment methods
    // ----------------------------------------------------------------------
    public List getRoleAssignments( String principal )
        throws RbacStoreException;

    public Role addRoleAssignment( String principal, int roleId )
        throws RbacStoreException;

    public Role removeRoleAssignment( String principal, int roleId )
        throws RbacStoreException;
}
