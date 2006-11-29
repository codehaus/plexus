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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    implements RBACManager, Initializable
{
    private CacheManager cacheManager = CacheManager.create();

    private List listeners = new ArrayList();

    private Resource globalResource;

    public void addListener( RBACManagerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }

    public void removeListener( RBACManagerListener listener )
    {
        listeners.remove( listener );
    }

    public void fireRbacInit( boolean freshdb )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacInit( freshdb );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Unable to trigger .rbacInit( boolean ) to " + listener.getClass().getName(), e );
            }
        }
    }

    public void fireRbacRoleSaved( Role role )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacRoleSaved( role );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Unable to trigger .rbacRoleSaved( Role ) to " + listener.getClass().getName(), e );
            }
        }
    }

    public void fireRbacRoleRemoved( Role role )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacRoleRemoved( role );
            }
            catch ( Exception e )
            {
                getLogger().warn( "Unable to trigger .rbacRoleRemoved( Role ) to " + listener.getClass().getName(), e );
            }
        }
    }

    public void fireRbacPermissionSaved( Permission permission )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacPermissionSaved( permission );
            }
            catch ( Exception e )
            {
                getLogger().warn(
                    "Unable to trigger .rbacPermissionSaved( Permission ) to " + listener.getClass().getName(), e );
            }
        }
    }

    public void fireRbacPermissionRemoved( Permission permission )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacPermissionRemoved( permission );
            }
            catch ( Exception e )
            {
                getLogger().warn(
                    "Unable to trigger .rbacPermissionRemoved( Permission ) to " + listener.getClass().getName(), e );
            }
        }
    }

    public void fireRbacUserAssignmentSaved( UserAssignment userAssignment )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacUserAssignmentSaved( userAssignment );
            }
            catch ( Exception e )
            {
                getLogger().warn(
                    "Unable to trigger .rbacUserAssignmentSaved( UserAssignment ) to " + listener.getClass().getName(), e );
            }
        }

        // smoke the entry from the cacheManager
        if ( cacheManager.getCache( USER_PERMISSION_CACHE ).get( userAssignment.getPrincipal() ) != null )
        {
            cacheManager.getCache( USER_PERMISSION_CACHE ).remove( userAssignment.getPrincipal() );
        }

    }

    public void fireRbacUserAssignmentRemoved( UserAssignment userAssignment )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            RBACManagerListener listener = (RBACManagerListener) it.next();
            try
            {
                listener.rbacUserAssignmentRemoved( userAssignment );
            }
            catch ( Exception e )
            {
                getLogger().warn(
                    "Unable to trigger .rbacUserAssignmentRemoved( UserAssignment ) to " + listener.getClass().getName(), e );
            }
        }

        // smoke the entry from the cacheManager
        if ( cacheManager.getCache( USER_PERMISSION_CACHE ).get( userAssignment.getPrincipal() ) != null )
        {
            cacheManager.getCache( USER_PERMISSION_CACHE ).remove( userAssignment.getPrincipal() );
        }
    }


    public void removeRole( String roleName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        removeRole( getRole( roleName ) );
    }

    public void removePermission( String permissionName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        removePermission( getPermission( permissionName ) );
    }

    public void removeOperation( String operationName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        removeOperation( getOperation( operationName ) );
    }

    public void removeResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        removeResource( getResource( resourceIdentifier ) );
    }

    public void removeUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        removeUserAssignment( getUserAssignment( principal ) );
    }

    public boolean resourceExists( Resource resource )
    {
        try
        {
            return getAllResources().contains( resource );
        }
        catch ( RbacManagerException e )
        {
            return false;
        }
    }

    public boolean resourceExists( String identifier )
    {
        try
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
        }
        catch ( RbacManagerException e )
        {
            return false;
        }

        return false;
    }

    public boolean operationExists( Operation operation )
    {
        try
        {
            return getAllOperations().contains( operation );
        }
        catch ( RbacManagerException e )
        {
            return false;
        }
    }

    public boolean operationExists( String name )
    {
        try
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
        }
        catch ( RbacManagerException e )
        {
            return false;
        }

        return false;
    }

    public boolean permissionExists( Permission permission )
    {
        try
        {
            return getAllPermissions().contains( permission );
        }
        catch ( RbacManagerException e )
        {
            return false;
        }
    }

    public boolean permissionExists( String name )
    {
        try
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
        }
        catch ( RbacManagerException e )
        {
            return false;
        }

        return false;
    }

    public boolean roleExists( Role role )
    {
        try
        {
            return getAllRoles().contains( role );
        }
        catch ( RbacManagerException e )
        {
            return false;
        }
    }

    public boolean roleExists( String name )
    {
        try
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
        }
        catch ( RbacManagerException e )
        {
            return false;
        }

        return false;
    }

    public boolean userAssignmentExists( String principal )
    {
        try
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
        }
        catch ( RbacManagerException e )
        {
            return false;
        }

        return false;
    }

    public boolean userAssignmentExists( UserAssignment assignment )
    {
        try
        {
            return getAllUserAssignments().contains( assignment );
        }
        catch ( RbacManagerException e )
        {
            return false;
        }
    }

    /**
     * returns a set of all permissions that are in all active roles for a given
     * principal
     *
     * @param principal
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacManagerException
     */
    public Set getAssignedPermissions( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {

        UserAssignment ua = getUserAssignment( principal.toString() );

        Set permissionSet = new HashSet();

        if ( ua.getRoleNames() != null )
        {
            boolean childRoleNamesUpdated = false;

            Iterator it = ua.getRoleNames().listIterator();
            while ( it.hasNext() )
            {
                String roleName = (String) it.next();
                try
                {
                    Role role = getRole( roleName );
                    gatherUniquePermissions( role, permissionSet );
                }
                catch ( RbacObjectNotFoundException e )
                {
                    // Found a bad role name. remove it!
                    it.remove();
                    childRoleNamesUpdated = true;
                }
            }

            if ( childRoleNamesUpdated )
            {
                saveUserAssignment( ua );
            }
        }

        return permissionSet;
    }

    /**
     * returns a map of assigned permissions keyed off of operations
     *
     * @param principal
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacManagerException
     */
    public Map getAssignedPermissionMap( String principal )
       throws RbacObjectNotFoundException, RbacManagerException
    {
        Cache userPermCache = cacheManager.getCache( USER_PERMISSION_CACHE );

        if ( userPermCache.get( principal ) != null )
        {
            getLogger().info( "using cached user permission map" );
            return (Map)userPermCache.get( principal ).getObjectValue();
        }
        else
        {
            getLogger().info( "building user permission map" );
            Map userPermMap = getPermissionMapByOperation( getAssignedPermissions( principal ) );
            
            userPermCache.put( new Element( principal, userPermMap ) );

            return userPermMap;
        }
    }

    private Map getPermissionMapByOperation( Collection permissions )
    {
        Map userPermMap = new HashMap();

        for ( Iterator i = permissions.iterator(); i.hasNext(); )
        {
            Permission permission = (Permission)i.next();

            List permList = (List)userPermMap.get( permission.getOperation().getName() );

            if ( permList != null )
            {
                permList.add( permission );
            }
            else
            {
                List newPermList = new ArrayList();
                newPermList.add( permission );
                userPermMap.put( permission.getOperation().getName(), newPermList );
            }
        }

        return userPermMap;
    }

    private void gatherUniquePermissions( Role role, Collection coll )
        throws RbacManagerException
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

        if ( role.hasChildRoles() )
        {
            Map childRoles = getChildRoles( role );
            Iterator it = childRoles.values().iterator();
            while ( it.hasNext() )
            {
                Role child = (Role) it.next();
                gatherUniquePermissions( child, coll );
            }
        }
    }

    public List getAllAssignableRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        List allRoles = getAllRoles();
        List assignableRoles = new ArrayList();

        Iterator it = allRoles.iterator();
        while ( it.hasNext() )
        {
            Role role = getRole( ( (Role) it.next() ).getName() );
            if ( role.isAssignable() )
            {
                assignableRoles.add( role );
            }
        }

        return assignableRoles;
    }

    /**
     * returns the active roles for a given principal
     * <p/>
     * NOTE: roles that are returned might have have roles themselves, if
     * you just want all permissions then use {@link #getAssignedPermissions( String principal)}
     *
     * @param principal
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacManagerException
     */
    public Collection getAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        UserAssignment ua = getUserAssignment( principal );

        return getAssignedRoles( ua );
    }

    /**
     * returns only the roles that are assigned, not the roles that might be child roles of the
     * assigned roles.
     *
     * @param ua
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacManagerException
     */
    public Collection getAssignedRoles( UserAssignment ua )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Set roleSet = new HashSet();

        if ( ua.getRoleNames() != null )
        {
            boolean childRoleNamesUpdated = false;

            Iterator it = ua.getRoleNames().listIterator();
            while ( it.hasNext() )
            {
                String roleName = (String) it.next();
                try
                {
                    Role role = getRole( roleName );

                    if ( !roleSet.contains( role ) )
                    {
                        roleSet.add( role );
                    }
                }
                catch ( RbacObjectNotFoundException e )
                {
                    // Found a bad role name. remove it!
                    it.remove();
                    childRoleNamesUpdated = true;
                }
            }

            if ( childRoleNamesUpdated )
            {
                saveUserAssignment( ua );
            }
        }

        return roleSet;
    }

    /**
     * get all of the roles that the give role has as a child into a set
     *
     * @param role
     * @param roleSet
     * @throws RbacObjectNotFoundException
     * @throws RbacManagerException
     */
    private void gatherEffectiveRoles( Role role, Set roleSet )
        throws RbacObjectNotFoundException, RbacManagerException
    {

        if ( role.hasChildRoles() )
        {
            Iterator it = role.getChildRoleNames().listIterator();

            while ( it.hasNext() )
            {
                String roleName = (String) it.next();
                Role crole = getRole( roleName );

                gatherEffectiveRoles( crole, roleSet );
            }

        }

        roleSet.add( role );
    }

    public Collection getEffectivelyAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        UserAssignment ua = getUserAssignment( principal );

        return getEffectivelyAssignedRoles( ua );
    }

    public Collection getEffectivelyAssignedRoles( UserAssignment ua )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Set roleSet = new HashSet();

        if ( ua != null && ua.getRoleNames() != null )
        {
            boolean childRoleNamesUpdated = false;

            Iterator it = ua.getRoleNames().listIterator();
            while ( it.hasNext() )
            {
                String roleName = (String) it.next();
                try
                {
                    Role role = getRole( roleName );

                    gatherEffectiveRoles( role, roleSet );
                }
                catch ( RbacObjectNotFoundException e )
                {
                    // Found a bad role name. remove it!
                    it.remove();
                    childRoleNamesUpdated = true;
                }
            }

            if ( childRoleNamesUpdated )
            {
                saveUserAssignment( ua );
            }
        }
        return roleSet;
    }

    /**
     * @param principal
     * @return
     * @throws RbacManagerException
     * @throws RbacObjectNotFoundException
     */
    public Collection getEffectivelyUnassignedRoles( String principal )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        Collection assignedRoles = getEffectivelyAssignedRoles( principal );
        List allRoles = getAllAssignableRoles();

        getLogger().debug( "UR: assigned " + assignedRoles.size() );
        getLogger().debug( "UR: available " + allRoles.size() );

        return CollectionUtils.subtract( allRoles, assignedRoles );
    }


    /**
     * @param principal
     * @return
     * @throws RbacManagerException
     * @throws RbacObjectNotFoundException
     */
    public Collection getUnassignedRoles( String principal )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        Collection assignedRoles = getAssignedRoles( principal );
        List allRoles = getAllAssignableRoles();

        getLogger().debug( "UR: assigned " + assignedRoles.size() );
        getLogger().debug( "UR: available " + allRoles.size() );

        return CollectionUtils.subtract( allRoles, assignedRoles );
    }

    public Resource getGlobalResource()
        throws RbacManagerException
    {
        if ( globalResource == null )
        {
            globalResource = createResource( Resource.GLOBAL );
            globalResource = saveResource( globalResource );
        }
        return globalResource;
    }

    public void addChildRole( Role role, Role childRole )
        throws RbacObjectInvalidException, RbacManagerException
    {
        saveRole( childRole );
        role.addChildRoleName( childRole.getName() );
    }

    public Map getChildRoles( Role role )
        throws RbacManagerException
    {
        List roleNames = role.getChildRoleNames();
        Map childRoles = new HashMap();

        boolean childRoleNamesUpdated = false;

        Iterator it = roleNames.listIterator();
        while ( it.hasNext() )
        {
            String roleName = (String) it.next();
            try
            {
                Role child = getRole( roleName );
                childRoles.put( child.getName(), child );
            }
            catch ( RbacObjectNotFoundException e )
            {
                // Found a bad roleName! - remove it.
                it.remove();
                childRoleNamesUpdated = true;
            }
        }

        if ( childRoleNamesUpdated )
        {
            saveRole( role );
        }

        return childRoles;
    }

    public Set getEffectiveRoles( Role role )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Set roleSet = new HashSet();
        gatherEffectiveRoles( role, roleSet );

        return roleSet;
    }

    public Map getRoles( Collection roleNames )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Map roleMap = new HashMap();

        Iterator it = roleNames.iterator();
        while ( it.hasNext() )
        {
            String roleName = (String) it.next();
            Role child = getRole( roleName );
            roleMap.put( child.getName(), child );
        }

        return roleMap;
    }


    /**
     * return the appointed cache, should it exist
     *
     * @param cacheName
     * @return
     */
    public Cache getCache( String cacheName )
    {
        return cacheManager.getCache( cacheName );
    }

    public void initialize()
        throws InitializationException
    {
        if ( !cacheManager.cacheExists( USER_PERMISSION_CACHE ) )
        {
            cacheManager.addCache( USER_PERMISSION_CACHE );
        }
    }
}
