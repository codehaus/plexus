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

/**
 * A RBAC session base class that provides generic implemenation with no internal data set.
 */
public class AbstractRbacSession implements RbacSession, Serializable
{
    /**
     * Returns true iff this session is authorized with the specified permission.
     */
/*
  public boolean isAuthorized(IPermission perm) {
    if (perm == null
    ||  perm == IPermission.NO_PERMISSION
    ||  perm.equals(IPermission.NO_PERMISSION))
    {
      return true;
    }
    IRole[] role = getActiveRoles();
    for (int i=0; i < role.length; i++) {
      if (role[i].isAuthorized(perm)) {
        return true;
      }
    }
    return false;
  }
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
        Role[] r = getActiveRoles();
        for ( int i = 0; i < r.length; i++ )
        {
            set.add( new DefaultPermission( r[i].getPermissions() ) );
        }
        Permission p = new DefaultPermission( (Permission[]) set.toArray( Permission.ZERO_PERMISSION ) );
        return p.ge( perm );
    }

    /**
     * Returns true iff this session is authorized with the specified permissions.
     */
    public boolean isAuthorized( Permission[] perm )
    {
        if ( perm == null )
        {
            return true;
        }
        return isAuthorized( new DefaultPermission( perm ) );
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
    public Role[] getActiveRoles()
    {
        return Role.ZERO_ROLE;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        Role[] role = getActiveRoles();
//    p("role.length="+role.length);
        for ( int i = 0; i < role.length; i++ )
        {
            sb.append( role[i].toString() );
//      p(role[i].toString());
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
        Role[] r = getActiveRoles();
        if ( r == null || r.length == 0 )
        {
            return false;
        }
        for ( int i = 0; i < r.length; i++ )
        {
            if ( r[i].equals( role ) )
            {
                return true;
            }
        }
        return false;
    }

    public Permission[] getPermissions()
    {
        Set set = new HashSet();
        Role[] roles = getActiveRoles();
        for ( int i = 0; i < roles.length; i++ )
        {
            Permission[] perm = roles[i].getPermissions();
            for ( int j = 0; j < perm.length; j++ )
            {
                set.add( perm[j] );
            }
        }
        return (Permission[]) set.toArray( Permission.ZERO_PERMISSION );
    }
    /**
     * Returns true iff the active role set contains an active role
     * with access privileges greater than or equal to that of the given role.
     */
/*
  public boolean hasRoleGE(IRole role) {
    if (role == null) {
      return true;
    }
    IRole[] r = getActiveRoles();
    if (r == null || r.length == 0) {
      return false;
    }
    for (int i=0; i < r.length; i++) {
      if (r[i].ge(role)) {
        return true;
      }
    }
    return false;
  }
  private void p(String s) {
    System.out.println("AbstractRbacSession>>"+s);
  }
*/
}
