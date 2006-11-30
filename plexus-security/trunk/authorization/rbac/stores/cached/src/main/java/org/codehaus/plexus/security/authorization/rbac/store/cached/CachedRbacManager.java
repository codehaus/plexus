package org.codehaus.plexus.security.authorization.rbac.store.cached;

/*
 * Copyright 2001-2006 The Codehaus.
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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.codehaus.plexus.ehcache.EhcacheComponent;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RBACManagerListener;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CachedRbacManager is a wrapped RBACManager with caching.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.rbac.RBACManager" role-hint="cached"
 */
public class CachedRbacManager
    extends AbstractLogEnabled
    implements RBACManager, RBACManagerListener
{
    /**
     * @plexus.requirement
     */
    private RBACManager rbacImpl;

    /**
     * cache used for operations
     * 
     * @plexus.requirement role-hint="operations"
     */
    private EhcacheComponent operationsCache;

    /**
     * cache used for permissions
     * 
     * @plexus.requirement role-hint="permissions"
     */
    private EhcacheComponent permissionsCache;

    /**
     * cache used for resources
     * 
     * @plexus.requirement role-hint="resources"
     */
    private EhcacheComponent resourcesCache;

    /**
     * cache used for roles
     * 
     * @plexus.requirement role-hint="roles"
     */
    private EhcacheComponent rolesCache;

    /**
     * cache used for user assignments
     * 
     * @plexus.requirement role-hint="userAssignments"
     */
    private EhcacheComponent userAssignmentsCache;

    /**
     * cache used for user permissions
     * 
     * @plexus.requirement role-hint="userPermissions"
     */
    private EhcacheComponent userPermissionsCache;

    public void addChildRole( Role role, Role childRole )
        throws RbacObjectInvalidException, RbacManagerException
    {
        try
        {
            this.rbacImpl.addChildRole( role, childRole );
        }
        finally
        {
            invalidateCachedRole( role );
            invalidateCachedRole( childRole );
        }
    }

    public void addListener( RBACManagerListener listener )
    {
        this.rbacImpl.addListener( listener );
    }

    public Operation createOperation( String name )
        throws RbacManagerException
    {
        operationsCache.invalidateKey( name );
        return this.rbacImpl.createOperation( name );
    }

    public Permission createPermission( String name )
        throws RbacManagerException
    {
        permissionsCache.invalidateKey( name );
        return this.rbacImpl.createPermission( name );
    }

    public Permission createPermission( String name, String operationName, String resourceIdentifier )
        throws RbacManagerException
    {
        permissionsCache.invalidateKey( name );
        return this.rbacImpl.createPermission( name, operationName, resourceIdentifier );
    }

    public Resource createResource( String identifier )
        throws RbacManagerException
    {
        resourcesCache.invalidateKey( identifier );
        return this.rbacImpl.createResource( identifier );
    }

    public Role createRole( String name )
    {
        rolesCache.invalidateKey( name );
        return this.rbacImpl.createRole( name );
    }

    public UserAssignment createUserAssignment( String principal )
        throws RbacManagerException
    {
        invalidateCachedUserAssignment( principal );
        return this.rbacImpl.createUserAssignment( principal );
    }

    public void clearAllCaches()
    {
        try
        {
            CacheManager cacheManager = CacheManager.getInstance();

            String names[] = cacheManager.getCacheNames();
            for ( int i = 0; i < names.length; i++ )
            {
                try
                {
                    Cache cache = cacheManager.getCache( names[i] );
                    cache.removeAll();
                }
                catch ( IllegalStateException e )
                {
                    getLogger().warn( "Unable to remove all elements from cache [" + names[i] + "]", e );
                }
                catch ( CacheException e )
                {
                    getLogger().warn( "Unable to remove all elements from cache [" + names[i] + "]", e );
                }
                catch ( IOException e )
                {
                    getLogger().warn( "Unable to remove all elements from cache [" + names[i] + "]", e );
                }
            }
        }
        catch ( IllegalStateException e )
        {
            getLogger().error( "Unable to clear all caches: ", e );
        }
    }

    public void eraseDatabase()
    {
        try
        {
            this.rbacImpl.eraseDatabase();
        }
        finally
        {
            clearAllCaches();
        }
    }

    public List getAllAssignableRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        getLogger().debug( "NOT CACHED - .getAllAssignableRoles()" );
        return this.rbacImpl.getAllAssignableRoles();
    }

    public List getAllOperations()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAllOperations()" );
        return this.rbacImpl.getAllOperations();
    }

    public List getAllPermissions()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAllPermissions()" );
        return this.rbacImpl.getAllPermissions();
    }

    public List getAllResources()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAllResources()" );
        return this.rbacImpl.getAllResources();
    }

    public List getAllRoles()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAllRoles()" );
        return this.rbacImpl.getAllRoles();
    }

    public List getAllUserAssignments()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAllUserAssignments()" );
        return this.rbacImpl.getAllUserAssignments();
    }

    public Map getAssignedPermissionMap( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = userPermissionsCache.getElement( principal );

        if ( el != null )
        {
            getLogger().debug( "using cached user permission map" );
            return (Map) el.getObjectValue();
        }
        else
        {
            getLogger().debug( "building user permission map" );
            Map userPermMap = this.rbacImpl.getAssignedPermissionMap( principal );
            userPermissionsCache.putElement( new Element( principal, userPermMap ) );
            return userPermMap;
        }
    }

    public Set getAssignedPermissions( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAssignedPermissions(String)" );
        return this.rbacImpl.getAssignedPermissions( principal );
    }

    public Collection getAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAssignedRoles(String)" );
        return this.rbacImpl.getAssignedRoles( principal );
    }

    public Collection getAssignedRoles( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getAssignedRoles(UserAssignment)" );
        return this.rbacImpl.getAssignedRoles( userAssignment );
    }

    public Map getChildRoles( Role role )
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getChildRoles(Role)" );
        return this.rbacImpl.getChildRoles( role );
    }

    public Collection getEffectivelyAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getEffectivelyAssignedRoles(String)" );
        return this.rbacImpl.getEffectivelyAssignedRoles( principal );
    }

    public Collection getEffectivelyUnassignedRoles( String principal )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        getLogger().debug( "NOT CACHED - .getEffectivelyUnassignedRoles(String)" );
        return this.rbacImpl.getEffectivelyUnassignedRoles( principal );
    }

    public Set getEffectiveRoles( Role role )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getEffectiveRoles(Role)" );
        return this.rbacImpl.getEffectiveRoles( role );
    }

    public Resource getGlobalResource()
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getGlobalResource()" );
        return this.rbacImpl.getGlobalResource();
    }

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = operationsCache.getElement( operationName );
        if ( el != null )
        {
            return (Operation) el.getObjectValue();
        }
        else
        {
            Operation operation = this.rbacImpl.getOperation( operationName );
            operationsCache.putElement( new Element( operationName, operation ) );
            return operation;
        }
    }

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = permissionsCache.getElement( permissionName );
        if ( el != null )
        {
            return (Permission) el.getObjectValue();
        }
        else
        {
            Permission permission = this.rbacImpl.getPermission( permissionName );
            permissionsCache.putElement( new Element( permissionName, permission ) );
            return permission;
        }
    }

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = resourcesCache.getElement( resourceIdentifier );
        if ( el != null )
        {
            return (Resource) el.getObjectValue();
        }
        else
        {
            Resource resource = this.rbacImpl.getResource( resourceIdentifier );
            resourcesCache.putElement( new Element( resourceIdentifier, resource ) );
            return resource;
        }
    }

    public Role getRole( String roleName )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = rolesCache.getElement( roleName );
        if ( el != null )
        {
            return (Role) el.getObjectValue();
        }
        else
        {
            Role role = this.rbacImpl.getRole( roleName );
            rolesCache.putElement( new Element( roleName, role ) );
            return role;
        }
    }

    public Map getRoles( Collection roleNames )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getRoles(Collection)" );
        return this.rbacImpl.getRoles( roleNames );
    }

    public Collection getUnassignedRoles( String principal )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        getLogger().debug( "NOT CACHED - .getUnassignedRoles(String)" );
        return this.rbacImpl.getUnassignedRoles( principal );
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacManagerException
    {
        Element el = userAssignmentsCache.getElement( principal );
        if ( el != null )
        {
            return (UserAssignment) el.getObjectValue();
        }
        else
        {
            UserAssignment userAssignment = this.rbacImpl.getUserAssignment( principal );
            userAssignmentsCache.putElement( new Element( principal, userAssignment ) );
            return userAssignment;
        }
    }

    public List getUserAssignmentsForRoles( Collection roleNames )
        throws RbacManagerException
    {
        getLogger().debug( "NOT CACHED - .getUserAssignmentsForRoles(Collection)" );
        return this.rbacImpl.getUserAssignmentsForRoles( roleNames );
    }

    public boolean operationExists( Operation operation )
    {
        if ( operation == null )
        {
            return false;
        }

        if ( operationsCache.hasKey( operation.getName() ) )
        {
            return true;
        }

        return this.rbacImpl.operationExists( operation );
    }

    public boolean operationExists( String name )
    {
        if ( operationsCache.hasKey( name ) )
        {
            return true;
        }

        return this.rbacImpl.operationExists( name );
    }

    public boolean permissionExists( Permission permission )
    {
        if ( permission == null )
        {
            return false;
        }

        if ( permissionsCache.hasKey( permission.getName() ) )
        {
            return true;
        }

        return this.rbacImpl.permissionExists( permission );
    }

    public boolean permissionExists( String name )
    {
        if ( permissionsCache.hasKey( name ) )
        {
            return true;
        }

        return this.rbacImpl.permissionExists( name );
    }

    public void rbacInit( boolean freshdb )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacInit( freshdb );
        }

        clearAllCaches();
    }

    public void rbacPermissionRemoved( Permission permission )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacPermissionRemoved( permission );
        }

        invalidateCachedPermission( permission );
    }

    public void rbacPermissionSaved( Permission permission )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacPermissionSaved( permission );
        }

        invalidateCachedPermission( permission );
    }

    public void rbacRoleRemoved( Role role )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacRoleRemoved( role );
        }

        invalidateCachedRole( role );
    }

    public void rbacRoleSaved( Role role )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacRoleSaved( role );
        }

        invalidateCachedRole( role );
    }

    public void rbacUserAssignmentRemoved( UserAssignment userAssignment )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacUserAssignmentRemoved( userAssignment );
        }

        invalidateCachedUserAssignment( userAssignment );
    }

    public void rbacUserAssignmentSaved( UserAssignment userAssignment )
    {
        if ( rbacImpl instanceof RBACManagerListener )
        {
            ( (RBACManagerListener) this.rbacImpl ).rbacUserAssignmentSaved( userAssignment );
        }

        invalidateCachedUserAssignment( userAssignment );
    }

    public void removeListener( RBACManagerListener listener )
    {
        this.rbacImpl.removeListener( listener );
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedOperation( operation );
        this.rbacImpl.removeOperation( operation );
    }

    public void removeOperation( String operationName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        operationsCache.invalidateKey( operationName );
        this.rbacImpl.removeOperation( operationName );
    }

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedPermission( permission );
        this.rbacImpl.removePermission( permission );
    }

    public void removePermission( String permissionName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        permissionsCache.invalidateKey( permissionName );
        this.rbacImpl.removePermission( permissionName );
    }

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedResource( resource );
        this.rbacImpl.removeResource( resource );
    }

    public void removeResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        resourcesCache.invalidateKey( resourceIdentifier );
        this.rbacImpl.removeResource( resourceIdentifier );
    }

    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedRole( role );
        this.rbacImpl.removeRole( role );
    }

    public void removeRole( String roleName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        rolesCache.invalidateKey( roleName );
        this.rbacImpl.removeRole( roleName );
    }

    public void removeUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedUserAssignment( principal );
        this.rbacImpl.removeUserAssignment( principal );
    }

    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedUserAssignment( userAssignment );
        this.rbacImpl.removeUserAssignment( userAssignment );
    }

    public boolean resourceExists( Resource resource )
    {
        if ( resourcesCache.hasKey( resource.getIdentifier() ) )
        {
            return true;
        }

        return this.rbacImpl.resourceExists( resource );
    }

    public boolean resourceExists( String identifier )
    {
        if ( resourcesCache.hasKey( identifier ) )
        {
            return true;
        }

        return this.rbacImpl.resourceExists( identifier );
    }

    public boolean roleExists( Role role )
    {
        if ( rolesCache.hasKey( role.getName() ) )
        {
            return true;
        }

        return this.rbacImpl.roleExists( role );
    }

    public boolean roleExists( String name )
    {
        if ( rolesCache.hasKey( name ) )
        {
            return true;
        }

        return this.rbacImpl.roleExists( name );
    }

    public Operation saveOperation( Operation operation )
        throws RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedOperation( operation );
        return this.rbacImpl.saveOperation( operation );
    }

    public Permission savePermission( Permission permission )
        throws RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedPermission( permission );
        return this.rbacImpl.savePermission( permission );
    }

    public Resource saveResource( Resource resource )
        throws RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedResource( resource );
        return this.rbacImpl.saveResource( resource );
    }

    public Role saveRole( Role role )
        throws RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedRole( role );
        return this.rbacImpl.saveRole( role );
    }

    public void saveRoles( Collection roles )
        throws RbacObjectInvalidException, RbacManagerException
    {
        Iterator it = roles.iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            invalidateCachedRole( role );
        }
        this.rbacImpl.saveRoles( roles );
    }

    public UserAssignment saveUserAssignment( UserAssignment userAssignment )
        throws RbacObjectInvalidException, RbacManagerException
    {
        invalidateCachedUserAssignment( userAssignment );
        return this.rbacImpl.saveUserAssignment( userAssignment );
    }

    public boolean userAssignmentExists( String principal )
    {
        if ( userAssignmentsCache.hasKey( principal ) )
        {
            return true;
        }

        return this.rbacImpl.userAssignmentExists( principal );
    }

    public boolean userAssignmentExists( UserAssignment assignment )
    {
        if ( userAssignmentsCache.hasKey( assignment.getPrincipal() ) )
        {
            return true;
        }

        return this.rbacImpl.userAssignmentExists( assignment );
    }

    private void invalidateCachedRole( Role role )
    {
        if ( role != null )
        {
            rolesCache.invalidateKey( role.getName() );
        }

    }

    private void invalidateCachedOperation( Operation operation )
    {
        if ( operation != null )
        {
            operationsCache.invalidateKey( operation.getName() );
        }
    }

    private void invalidateCachedPermission( Permission permission )
    {
        if ( permission != null )
        {
            permissionsCache.invalidateKey( permission.getName() );
        }
    }

    private void invalidateCachedResource( Resource resource )
    {
        if ( resource != null )
        {
            resourcesCache.invalidateKey( resource.getIdentifier() );
        }
    }

    private void invalidateCachedUserAssignment( UserAssignment userAssignment )
    {
        if ( userAssignment != null )
        {
            userAssignmentsCache.invalidateKey( userAssignment.getPrincipal() );
            userPermissionsCache.invalidateKey( userAssignment.getPrincipal() );
        }
    }

    private void invalidateCachedUserAssignment( String principal )
    {
        userAssignmentsCache.invalidateKey( principal );
        userPermissionsCache.invalidateKey( principal );
    }
}
