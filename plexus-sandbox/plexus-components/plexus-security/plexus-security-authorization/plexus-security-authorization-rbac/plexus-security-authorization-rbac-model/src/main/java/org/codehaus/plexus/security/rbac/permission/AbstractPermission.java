package org.codehaus.plexus.security.rbac.permission;

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

import org.codehaus.plexus.security.rbac.role.Role;

import java.io.Serializable;
import java.util.Set;
import java.util.Iterator;

/**
 * A permission base class that provides generic implemenation with no internal data set.
 */
public class AbstractPermission
    implements Permission,
    Serializable
{
    /**
     * Returns a null set of permission entry.
     */
    public Set getPermissionEntries()
    {
        return Operation.ZERO_PERMISSION_ENTRY;
    }

    /**
     * Returns a null set of role.
     */
    public Set getAssignedRoles()
    {
        return Role.ZERO_ROLE;
    }

    /**
     * Returns true iff this permission is greater than or equal to
     * the given permission in terms of access privileges.
     */
    public boolean ge( Permission p )
    {
        if ( p == null )
        {
            return true;
        }

        if ( p == this )
        {
            return true;
        }

        Set toOperations = p.getPermissionEntries();

        if ( toOperations == null || toOperations.size() == 0 )
        {
            return true;
        }

        Set operations = getPermissionEntries();

        operations.removeAll( toOperations );

        return operations.size() == 0;
    }

    /**
     * Two permissions are equal if they have the same set of permission entries.
     */
    public boolean equals( Object o )
    {
        if ( o == null || !( o instanceof Permission ) )
        {
            return false;
        }
        if ( o == this )
        {
            return true;
        }

        Permission p = (Permission) o;

        Set operations = getPermissionEntries();

        Set toOperations = p.getPermissionEntries();

        if ( operations == null && toOperations == null )
        {
            return true;
        }
        if ( operations == null || toOperations == null )
        {
            return false;
        }
        if ( operations.size() != toOperations.size() )
        {
            return false;
        }

        operations.removeAll( toOperations );

        return operations.size() == 0;
    }

    public int hashCode()
    {
        Set pe = getPermissionEntries();

        int hashCode = 0;

        if ( pe != null )
        {
            for ( Iterator i = pe.iterator(); i.hasNext();)
            {
                hashCode ^= i.next().hashCode();
            }
        }
        return hashCode;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        Set p = getPermissionEntries();

        for ( Iterator i = p.iterator(); i.hasNext(); )
        {
            sb.append( "\n" );

            sb.append( i.next().toString() );
        }

        return sb.toString();
    }
}
