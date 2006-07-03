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
import org.codehaus.plexus.security.rbac.permission.DefaultPermission;
import org.codehaus.plexus.security.rbac.permission.Permission;
import org.codehaus.plexus.security.rbac.user.RbacUser;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A role base class that provides generic implemenation with no internal data set.
 */
public class AbstractRole
    implements Role,
    Serializable
{
    /**
     * Returns true iff this role is authorized with the specified permission.
     */
    public boolean isAuthorized( Permission perm )
    {
        if ( perm == null || perm == Permission.NO_PERMISSION || perm.equals( Permission.NO_PERMISSION ) )
        {
            return true;
        }
        return new DefaultPermission( getPermissions() ).ge( perm );
    }

    /**
     * Returns true iff this role is authorized with the specified permissions.
     */
    public boolean isAuthorized( Set perm )
    {
        if ( perm == null )
        {
            return true;
        }
        return new DefaultPermission( getPermissions() ).ge( new DefaultPermission( perm ) );
    }

    /**
     * Always returns zero.
     */
    public int getMaxMembers()
    {
        return 0;
    }

    /**
     * Always returns a null set of user.
     */
    public RbacUser[] getAssignedUsers()
    {
        return RbacUser.ZERO_USER;
    }

    /**
     * Always returns a null set of permission.
     */
    public Set getPermissions()
    {
        return Permission.ZERO_PERMISSION;
    }

    /**
     * Always returns a non-modifiable empty set of attributes.
     */
    public Map getRoleAttributes()
    {
        return Collections.unmodifiableMap( new HashMap() );
    }

    /**
     * Two roles are equal if they have the same set of attributes and permissions.
     */
    public boolean equals( Object o )
    {
        if ( o == null || !( o instanceof Role ) )
        {
            return false;
        }
        if ( o == this )
        {
            return true;
        }

        Role r = (Role) o;

        Map attr = getRoleAttributes();

        Set permissions = getPermissions();

        Map to_attr = r.getRoleAttributes();

        Set toPermissions = r.getPermissions();

        if ( !attr.equals( to_attr ) )
        {
            return false;
        }

        if ( permissions.length != toPermissions.length )
        {
            return false;
        }

        outter:

        for ( int i = 0; i < permissions.length; i++ )
        {
            Permission pi = permissions[i];

            for ( int j = 0; j < toPermissions.length; j++ )
            {
                Permission pj = toPermissions[j];

                if ( pi.equals( pj ) )
                {
                    continue outter;
                }
            }
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int hash = getRoleAttributes().hashCode();

        Set p = getPermissions();

        if ( p != null )
        {
            for ( int i = 0; i < p.length; i++ )
            {
                hash ^= p[i].hashCode();
            }
        }
        return hash;
    }

    /**
     * Returns true iff the access privilege this role is
     * greater than or equal to that of the given role.
     */
    public boolean ge( Role r )
    {
        if ( r == null || r == this )
        {
            return true;
        }

        Set permissions = getPermissions();

        Set toPermissions = r.getPermissions();

        outter:

        for ( int i = 0; i < toPermissions.length; i++ )
        {
            Permission pi = toPermissions[i];

            for ( int j = 0; j < permissions.length; j++ )
            {
                Permission pj = permissions[j];

                if ( pj.ge( pi ) )
                {
                    continue outter;
                }
            }
            return false;
        }
        return true;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        Map map = getRoleAttributes();

        for ( Iterator itr = map.entrySet().iterator(); itr.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) itr.next();
            sb.append( "\n" );
            sb.append( entry.getKey().toString() );
            sb.append( "=" );
            sb.append( entry.getValue().toString() );
        }

        Set permissions = getPermissions();

        for ( int i = 0; i < permissions.length; i++ )
        {
            sb.append( "\n" );

            sb.append( permissions[i].toString() );
        }
        return sb.toString();
    }

    /**
     * Always returns false.
     */
    public boolean roleAdded( RbacUser user )
        throws RbacSecurityViolation
    {
        return false;
    }

    /**
     * Always returns false.
     */
    public boolean roleDropped( RbacUser user )
    {
        return false;
    }

    /**
     * Always returns false.
     */
    public boolean grantPermission( Permission perm )
    {
        return false;
    }

    /**
     * Always returns false.
     */
    public boolean revokePermission( Permission perm )
    {
        return false;
    }
}



