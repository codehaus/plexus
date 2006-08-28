package org.codehaus.plexus.security.rbac.user;

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
import org.codehaus.plexus.security.rbac.role.Role;

import java.util.Set;

/**
 * User which acquires permissions by being members of roles.
 */
public interface RbacUser
{
    /**
     * A null set of RBAC user.
     */
    public static final RbacUser[] ZERO_USER = new RbacUser[0];
    /**
     * A user with no role.
     */
    public static final RbacUser NO_USER = new AbstractRbacUser();

    /**
     * Adds the given role to this user.
     * The limit of the number of users that can be assigned to the role
     * should be checked before calling this method.
     *
     * @throws RbacSecurityViolation if the limit of the number of users
     *                               that can be assigned to the given role is exceeded.
     */
    public boolean addRole( Role role )
        throws RbacSecurityViolation;

    /**
     * Removes the given role from this user.
     */
    public boolean dropRole( Role role );

    /**
     * Returns the current set of roles that have been assigned to this user.
     * (A core RBAC feature.)
     */
    public Set getRoles();

    /**
     * Returns the set of permissions a this user gets thru his assgined roles.
     */
    public Set getPermissions();
}
