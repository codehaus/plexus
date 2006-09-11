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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

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
    extends AbstractLogEnabled
    implements RBACManager
{

    private Resource globalResource;

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

    public boolean resourceExists( Resource resource )
    {
        return getAllResources().contains( resource );
    }

    public boolean resourceExists( String identifier )
    {
        Iterator it = getAllResources().iterator();
        while ( it.hasNext() )
        {
            Resource resource = (Resource) it.next();
            if ( StringUtils.equals( resource.getIdentifier(), identifier ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean operationExists( Operation operation )
    {
        return getAllOperations().contains( operation );
    }

    public boolean operationExists( String name )
    {
        Iterator it = getAllOperations().iterator();
        while ( it.hasNext() )
        {
            Operation operation = (Operation) it.next();
            if ( StringUtils.equals( operation.getName(), name ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean permissionExists( Permission permission )
    {
        return getAllPermissions().contains( permission );
    }

    public boolean permissionExists( String name )
    {
        Iterator it = getAllPermissions().iterator();
        while ( it.hasNext() )
        {
            Permission permission = (Permission) it.next();
            if ( StringUtils.equals( permission.getName(), name ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean roleExists( Role role )
    {
        return getAllRoles().contains( role );
    }

    public boolean roleExists( String name )
    {
        Iterator it = getAllRoles().iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            if ( StringUtils.equals( role.getName(), name ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean userAssignmentExists( String principal )
    {
        Iterator it = getAllUserAssignments().iterator();
        while ( it.hasNext() )
        {
            UserAssignment assignment = (UserAssignment) it.next();
            if ( StringUtils.equals( assignment.getPrincipal(), principal ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean userAssignmentExists( UserAssignment assignment )
    {
        return getAllUserAssignments().contains( assignment );
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
        /*
        if ( role.getChildRoles() != null )
        {
            Iterator it = role.getChildRoles().iterator();
            while ( it.hasNext() )
            {
                Role child = (Role) it.next();
                gatherUniquePermissions( child, coll );
            }
        }
        */
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

    public Resource getGlobalResource()
        throws RbacStoreException
    {
        if ( globalResource == null )
        {
            if ( resourceExists( Resource.GLOBAL ) )
            {
                try
                {
                    globalResource = getResource( Resource.GLOBAL );
                }
                catch ( RbacObjectNotFoundException ne )
                {
                    throw new RbacStoreException( "global resource exists, but doesn't!", ne );
                }
            }
            else
            {
                globalResource = createResource( Resource.GLOBAL );
                addResource( globalResource );
            }
        }
        return globalResource;
    }

}
