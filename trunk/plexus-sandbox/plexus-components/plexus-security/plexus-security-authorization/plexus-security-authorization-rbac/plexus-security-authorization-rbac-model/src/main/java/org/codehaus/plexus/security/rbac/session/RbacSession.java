package org.codehaus.plexus.security.rbac.session;

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

import org.codehaus.plexus.security.rbac.permission.Permission;
import org.codehaus.plexus.security.rbac.role.Role;
import org.codehaus.plexus.security.rbac.user.RbacUser;

import java.util.Set;

/**
 * RBAC session is an active process representing a user.
 */
public interface RbacSession
{
    /**
     * Returns true iff this session is authorized with the specified permission.
     */
    public boolean isAuthorized( Permission perm );

    /**
     * Returns true iff this session is authorized with the specified permissions.
     */
    //public boolean isAuthorized( Permission[] perm );

    /**
     * Returns true iff this session is authorized with the permissions of the specified role.
     */
    public boolean isAuthorized( Role role );

    /**
     * Adds the given role to the current active role set.
     */
    public boolean addActiveRole( Role r );

    /**
     * Drops the given role from the current active role set.
     */
    public boolean dropActiveRole( Role r );

    /**
     * Returns the user of this session.
     */
    public RbacUser getUser();

    /**
     * Returns the current active role set.
     */
    public Set getActiveRoles();

    /**
     * Returns true iff the active role set contains the specified role.
     */
    public boolean hasRole( Role role );

    /**
     * Returns the set of permissions available in this session (ie. union of all
     * permissions assigned to the active role set.
     */
    public Set getPermissions();
    /**
     * Returns true iff the active role set contains an active role
     * with access privileges greater than or equal to that of the given role.
     */
//  public boolean hasRoleGE(IRole role);
}
