package org.codehaus.plexus.security.rbac;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AbstractRBACManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractRBACManager
    implements RBACManager
{

    public void removeRole( String roleName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        removeRole( getRole( roleName ) );
    }

    public void removePermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        removePermission( getPermission( permissionName ) );
    }

    public void removeOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        removeOperation( getOperation( operationName ) );
    }

    public void removeResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        removeResource( getResource( resourceIdentifier ) );
    }

    public void removeUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        removeUserAssignment( getUserAssignment( principal ) );
    }

    /**
     * returns a set of all permissions that are in all active roles for a given
     * principal
     *
     * @param principal
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Set getAssignedPermissions( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        UserAssignment ua = getUserAssignment( principal.toString() );

        Set permissionSet = new HashSet();

        if ( ua.getRoles() != null )
        {
            Iterator it = ua.getRoles().values().iterator();
            while ( it.hasNext() )
            {
                Role role = (Role) it.next();
                gatherUniquePermissions( role, permissionSet );
            }
        }

        return permissionSet;
    }

    private void gatherUniquePermissions( Role role, Collection coll )
    {
        if ( role.getPermissions() != null )
        {
            Iterator itperm = role.getPermissions().iterator();
            while ( itperm.hasNext() )
            {
                Permission permission = (Permission) itperm.next();
                if ( !coll.contains( permission ) )
                {
                    coll.add( permission );
                }
            }
        }

        if ( role.getChildRoles() != null )
        {

            Iterator it = role.getChildRoles().values().iterator();
            while ( it.hasNext() )
            {
                Role child = (Role) it.next();
                gatherUniquePermissions( child, coll );
            }
        }
    }

    public List getAllAssignableRoles()
        throws RbacStoreException
    {
        List allRoles = getAllRoles();
        List assignableRoles = new ArrayList();

        Iterator it = allRoles.iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            if ( role.isAssignable() )
            {
                assignableRoles.add( role );
            }
        }

        return assignableRoles;
    }

    /**
     * returns the active roles for a given principal
     *
     * NOTE: roles that are returned might have have roles themselves, if
     * you just want all permissions then use {@link #getAssignedPermissions( Object principal )}
     *
     * @param principal
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Map getAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        UserAssignment ua = getUserAssignment( principal.toString() );

        return ua.getRoles();
    }

}
