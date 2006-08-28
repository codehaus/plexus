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

import java.io.Serializable;
import java.util.Set;

/**
 * A RBAC user base class that provides generic implemenation with no internal data set.
 */
public class AbstractRbacUser
    implements RbacUser, Serializable
{
    /**
     * Adds the given role to this user.
     * The limit of the number of users that can be assigned to the role
     * should be checked before calling this method.
     *
     * @throws org.codehaus.plexus.security.rbac.RbacSecurityViolation if the limit of the number of users
     *                               that can be assigned to the given role is exceeded.
     */
    public boolean addRole( Role role )
        throws RbacSecurityViolation
    {
        if ( role == null )
        {
            return false;
        }

        return role.roleAdded( this );
    }

    /**
     * Removes the given role from this user.
     */
    public boolean dropRole( Role role )
    {
        if ( role == null )
        {
            return false;
        }
        return role.roleDropped( this );
    }

    /**
     * Always returns a null set of role.
     */
    public Set getRoles()
    {
        return Role.ZERO_ROLE;
    }

    public Set getPermissions()
    {
        return Permission.ZERO_PERMISSION;
    }
}
