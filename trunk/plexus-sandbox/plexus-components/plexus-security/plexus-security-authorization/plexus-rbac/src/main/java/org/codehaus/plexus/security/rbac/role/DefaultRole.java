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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of the IRole interface.
 */
public class DefaultRole
    extends AbstractRole
{
    /**
     * The "name" attribute key of this role.
     */
    public static final String NAME = "name";
    /**
     * An unmodifiable set of permissions assigned to this role.
     */
    public final Set permissionSet;
    /**
     * An unmodifiable map of role attributes, such as name, desc, password, etc.
     */
    public final Map attributes;
    /**
     * Maximum number of user members of this role.
     */
    public final int maxMembers;
    /**
     * The current set of users assigned with this role.
     */
    private Set assignedUsers = Collections.synchronizedSet( new HashSet() );

    /**
     * Constructs with the given permission.
     */
    public DefaultRole( Permission perm )
    {
        this( new Permission[]{perm}, null, 0 );
    }

    /**
     * Constructs with the given set of permissions.
     */
    public DefaultRole( Permission[] perm )
    {
        this( perm, null, 0 );
    }

    /**
     * Constructs with the given sets of permissions and junior roles.
     */
    public DefaultRole( Permission[] perm, Map attr, int maxMembers )
    {
        if ( maxMembers < 0 )
        {
            throw new IllegalArgumentException( "maxMembers must not be less than zero." );
        }
        this.maxMembers = maxMembers;
        Set ps = new HashSet();

        for ( int i = 0; i < perm.length; i++ )
        {
            ps.add( perm[i] );
        }
        this.permissionSet = ps;
        this.attributes = Collections.unmodifiableMap( attr == null ? new HashMap() : attr );
    }

    /**
     * Returns the current set of permissions that have been assigned to this role.
     * For advanced permission-role review.
     */
    public Set getPermissions()
    {
        synchronized ( permissionSet )
        {
            return permissionSet;
        }
    }

    /**
     * Returns the unmodifiable map of attributes of this role.
     */
    public Map getRoleAttributes()
    {
        return attributes;
    }

    /**
     * Convenient method to return the name of this role.
     */
    public String getName()
    {
        return (String) attributes.get( NAME );
    }

    /**
     * Returns the maximum number of users this role can be assigned to; or zero if there is no limit.
     */
    public int getMaxMembers()
    {
        return maxMembers;
    }
        
    /**
     * Informs this role that it has just been added to the given user.
     *
     * @return true if this role can be successfully added to the given user;
     *         or false if this role has already been added.
     * @throws org.codehaus.plexus.security.rbac.RbacSecurityViolation if the maximum number of user that
     *                               can be assigned to this role is exceeded.
     */
    public boolean roleAdded( RbacUser user ) throws RbacSecurityViolation
    {
        if ( maxMembers > 0 && assignedUsers.size() == maxMembers )
        {
            throw new RbacSecurityViolation( "More than "
                                             + maxMembers + " users cannot be assigned to role " + this.getName() );
        }
        return assignedUsers.add( user );
    }

    /**
     * Informs this role that it has just been removed from the given user.
     *
     * @return true if this role is successfully removed from the user;
     *         or false if there is nothing to remove.
     */
    public boolean roleDropped( RbacUser user )
    {
        return assignedUsers.remove( user );
    }

    /**
     * Returns the current set of users this role has been assigned to.
     * (A core RBAC feature.)
     */
    public RbacUser[] getAssignedUsers()
    {
        synchronized ( assignedUsers )
        {
            return (RbacUser[]) assignedUsers.toArray( RbacUser.ZERO_USER );
        }
    }

    /**
     * Grants the given permission to this role.
     *
     * @return true if the grant is successful;
     *         or false if the permission has already been granted.
     */
    public boolean grantPermission( Permission perm )
    {
        if ( perm == null )
        {
            return false;
        }
        synchronized ( permissionSet )
        {
            return permissionSet.add( perm );
        }
    }

    /**
     * Revokes the given permission from this role.
     *
     * @return true if the revokation is successful;
     *         or false if there is no such permission to revoke.
     */
    public boolean revokePermission( Permission perm )
    {
        synchronized ( permissionSet )
        {
            return permissionSet.remove( perm );
        }
    }
}
