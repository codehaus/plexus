package org.codehaus.plexus.security.rbac.role;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.security.rbac.RbacSecurityViolation;
import org.codehaus.plexus.security.rbac.permission.Permission;
import org.codehaus.plexus.security.rbac.user.RbacUser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Role to which users and permissions can be assigned.
 */
public interface Role
{
    /**
     * Role that has all permissons.
     */
    public static final Role SUPER_USER = new DefaultRole( new Permission[]{Permission.ALL_PERMISSION} );
    /**
     * Role that has no permission.
     */
    public static final Role EMPTY_ROLE = new AbstractRole();
    /**
     * A null role set.
     */
    public static final Set ZERO_ROLE = new HashSet();

    /**
     * Returns true iff this role is authorized with the specified permission.
     */
    public boolean isAuthorized( Permission perm );

    /**
     * Returns true iff this role is authorized with the specified permissions.
     */
    public boolean isAuthorized( Set permissions );

    /**
     * Returns the current set of permissions that have been assigned to this role.
     * For advanced permission-role review.
     */
    public Set getPermissions();

    /**
     * Returns the attributes of this role.
     */
    public Map getRoleAttributes();  // such as password associated with a role.

    /**
     * Returns true iff the access privilege of this role is
     * greater than or equal to that of the given role.
     */
    public boolean ge( Role role );

    /**
     * Returns the maximum number of users this role can be assigned to; or 0 if there is no maximum.
     */
    public int getMaxMembers();

    /**
     * Returns the current list of users this role has been assigned to,
     * either directly or indirectly via the role hierarchy.
     * (A core RBAC feature.)
     */
    public RbacUser[] getAssignedUsers();

    /**
     * Informs this role that it has just been added to the given user.
     *
     * @return true if this role can be successfully added to the given user;
     *         or false if this role has already been added.
     * @throws org.codehaus.plexus.security.rbac.RbacSecurityViolation if the maximum number of user that
     *                               can be assigned to this role is exceeded.
     */
    public boolean roleAdded( RbacUser user )
        throws RbacSecurityViolation;

    /**
     * Informs this role that it has just been removed from the given user.
     *
     * @return true if this role is successfully removed from the user;
     *         or false if there is nothing to remove.
     */
    public boolean roleDropped( RbacUser user );

    /**
     * Grants the given permission to this role.
     *
     * @return true if the grant is successful;
     *         or false if the permission has already been granted.
     */
    public boolean grantPermission( Permission perm );

    /**
     * Revokes the given permission from this role.
     *
     * @return true if the revokation is successful;
     *         or false if there is no such permission to revoke.
     */
    public boolean revokePermission( Permission perm );
}
