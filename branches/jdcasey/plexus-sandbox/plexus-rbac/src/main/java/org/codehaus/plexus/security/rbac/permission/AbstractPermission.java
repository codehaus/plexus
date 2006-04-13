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

/**
 * A permission base class that provides generic implemenation with no internal data set.
 */
public class AbstractPermission implements Permission, Serializable
{
    /**
     * Returns a null set of permission entry.
     */
    public PermissionEntry[] getPermissionEntries()
    {
        return PermissionEntry.ZERO_PERMISSION_ENTRY;
    }

    /**
     * Returns a null set of role.
     */
    public Role[] getAssignedRoles()
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
        PermissionEntry[] to_pe = p.getPermissionEntries();

        if ( to_pe == null || to_pe.length == 0 )
        {
            return true;
        }
        PermissionEntry[] pe = getPermissionEntries();
        outter:
          for ( int i = 0; i < to_pe.length; i++ )
          {
              PermissionEntry pi = to_pe[i];
              for ( int j = 0; j < pe.length; j++ )
              {
                  PermissionEntry pj = pe[j];
                  if ( pj.ge( pi ) )
                  {
                      continue outter;
                  }
              }
              return false;
          }
        return true;
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
        PermissionEntry[] pe = getPermissionEntries();
        PermissionEntry[] to_pe = p.getPermissionEntries();

        if ( pe == null && to_pe == null )
        {
            return true;
        }
        if ( pe == null || to_pe == null )
        {
            return false;
        }
        if ( pe.length != to_pe.length )
        {
            return false;
        }
        outter:
          for ( int i = 0; i < pe.length; i++ )
          {
              PermissionEntry pi = pe[i];
              for ( int j = 0; j < to_pe.length; j++ )
              {
                  PermissionEntry pj = to_pe[j];
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
        PermissionEntry[] pe = getPermissionEntries();
        int hashCode = 0;
        if ( pe != null )
        {
            for ( int i = 0; i < pe.length; i++ )
            {
                hashCode ^= pe[i].hashCode();
            }
        }
        return hashCode;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        PermissionEntry[] p = getPermissionEntries();
        for ( int i = 0; i < p.length; i++ )
        {
            sb.append( "\n" );
            sb.append( p[i].toString() );
        }
        return sb.toString();
    }
}
