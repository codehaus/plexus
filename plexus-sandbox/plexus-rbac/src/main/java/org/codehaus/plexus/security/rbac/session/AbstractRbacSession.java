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

import org.codehaus.plexus.security.rbac.permission.DefaultPermission;
import org.codehaus.plexus.security.rbac.permission.Permission;
import org.codehaus.plexus.security.rbac.role.Role;
import org.codehaus.plexus.security.rbac.user.RbacUser;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * A RBAC session base class that provides generic implemenation with no internal data set.
 */
public class AbstractRbacSession
    implements RbacSession, Serializable
{
    /**
     * Returns true iff this session is authorized with the specified permission.
     */
    public boolean isAuthorized( Permission perm )
    {
        if ( perm == null
            || perm == Permission.NO_PERMISSION
            || perm.equals( Permission.NO_PERMISSION ) )
        {
            return true;
        }

        Set set = new HashSet();

        Set r = getActiveRoles();

        for ( Iterator i = r.iterator(); i.hasNext(); )
        {
            Role role = (Role) i.next();

            //set.add( new DefaultPermission( role.getPermissions() ) );

            set.add( role.getPermissions() );
        }

        Permission p = new DefaultPermission( Permission.ZERO_PERMISSION );

        return p.ge( perm );
    }

    /**
     * Returns true iff this session is authorized with the specified permissions.
     */
    //public boolean isAuthorized( Permission[] perm )
    //{
    //    return false;
    //}

    /**
     * Returns true iff this session is authorized with the specified permissions.
     */
    public boolean isAuthorized( Set permissions )
    {
        if ( permissions == null )
        {
            return true;
        }

        return isAuthorized( permissions );
    }

    /**
     * Returns true iff this session is authorized with the permissions of the specified role.
     */
    public boolean isAuthorized( Role role )
    {
        if ( role == null )
        {
            return true;
        }

        return isAuthorized( role.getPermissions() );
    }

    /**
     * A no-op that always returns false.
     */
    public boolean addActiveRole( Role r )
    {
        return false;
    }

    /**
     * A no-op that always returns false.
     */
    public boolean dropActiveRole( Role r )
    {
        return false;
    }

    /**
     * Always returns a user with no role.
     */
    public RbacUser getUser()
    {
        return RbacUser.NO_USER;
    }

    /**
     * Always returns a null role set.
     */
    public Set getActiveRoles()
    {
        return Role.ZERO_ROLE;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        Set role = getActiveRoles();

        for ( Iterator i = role.iterator(); i.hasNext(); )
        {
            sb.append( i.next().toString() );
        }
        return sb.toString();
    }

    /**
     * Returns true iff the active role set contains the specified role.
     */
    public boolean hasRole( Role role )
    {
        if ( role == null )
        {
            return false;
        }

        Set activeRoles = getActiveRoles();

        if ( activeRoles == null || activeRoles.size() == 0 )
        {
            return false;
        }

        for ( Iterator i = activeRoles.iterator(); i.hasNext(); )
        {
            Role activeRole = (Role) i.next();

            if ( activeRole.equals( role ) )
            {
                return true;
            }
        }
                
        return false;
    }

    public Set getPermissions()
    {
        Set permissions = new HashSet();

        Set roles = getActiveRoles();

        for ( Iterator i = roles.iterator(); i.hasNext(); )
        {
            Role role = (Role) i.next();

            Set perm = role.getPermissions();

            permissions.addAll( perm );
        }

        return permissions;
    }
}
