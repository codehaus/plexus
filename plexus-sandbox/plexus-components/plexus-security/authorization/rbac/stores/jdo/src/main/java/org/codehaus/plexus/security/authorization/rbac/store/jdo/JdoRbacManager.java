package org.codehaus.plexus.security.authorization.rbac.store.jdo;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;
import org.codehaus.plexus.jdo.PlexusStoreException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoOperation;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoPermission;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoResource;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoRole;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoUserAssignment;
import org.codehaus.plexus.security.rbac.AbstractRBACManager;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACObjectAssertions;
import org.codehaus.plexus.security.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

/**
 * JdoRbacManager:
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.rbac.RBACManager"
 *   role-hint="jdo"
 */
public class JdoRbacManager
    extends AbstractRBACManager
    implements Initializable
{
    /**
     * @plexus.requirement
     */
    private JdoFactory jdoFactory;

    private PersistenceManagerFactory pmf;

    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Role}.
     *
     * Note: this method does not add the {@link Role} to the underlying store.
     *       a call to {@link #addRole(Role)} is required to track the role created with this
     *       method call.
     *
     * @param name the name.
     * @return the new {@link Role} object with an empty (non-null) {@link Role#getChildRoles()} object.
     */
    public Role createRole( String name )
    {
        Role role;
        
        try
        {
            role = getRole( name );
        }
        catch ( RbacObjectNotFoundException e )
        {
            role = new JdoRole();
            role.setName( name );
        }
        
        return role;
    }

    /**
     * Method addRole
     *
     * @param role
     */
    public Role saveRole( Role role )
        throws RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( role );
        
        return (Role) saveObject( role );
    }
    
    public boolean roleExists( Role role )
    {
        return objectExistsInJdo( role );
    }

    public boolean roleExists( String name )
    {
        return objectExistsInJdoById( JdoRole.class, name );
    }

    /**
     *
     *
     * @param roleName
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Role getRole( String roleName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Role) getObjectById( JdoRole.class, roleName );
    }

    /**
     * Method getRoles
     */
    public List getAllRoles()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoRole.class );
    }

    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( role );
        removeObject( role );
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Permission}.
     *
     * Note: this method does not add the {@link Permission} to the underlying store.
     *       a call to {@link #addPermission(Permission)} is required to track the permission created
     *       with this method call.
     *
     * @param name the name.
     * @return the new Permission.
     */
    public Permission createPermission( String name )
    {
        Permission permission;
        
        try
        {
            permission = getPermission( name );
            getLogger().debug( "Create Permission [" + name + "] Returning Existing." );
        }
        catch ( RbacObjectNotFoundException e )
        {
            permission = new JdoPermission();
            permission.setName( name );
            getLogger().debug( "Create Permission [" + name + "] New JdoPermission." );
        }
        
        return permission;
    }

    /**
     * Creates an implementation specific {@link Permission} with specified {@link Operation},
     * and {@link Resource} identifiers.
     *
     * Note: this method does not add the Permission, Operation, or Resource to the underlying store.
     *       a call to {@link #addPermission(Permission)} is required to track the permission, operation,
     *       or resource created with this method call.
     *
     * @param name the name.
     * @param operationName the {@link Operation#setName(String)} value
     * @param resourceIdentifier the {@link Resource#setIdentifier(String)} value
     * @return the new Permission.
     */
    public Permission createPermission( String name, String operationName, String resourceIdentifier )
    {
        Permission permission = new JdoPermission();
        permission.setName( name );

        Operation operation;
        try
        {
            operation = getOperation( operationName );
        }
        catch ( RbacObjectNotFoundException e )
        {
            operation = new JdoOperation();
            operation.setName( operationName );
        }
        permission.setOperation( operation );

        Resource resource;
        try
        {
            resource = getResource( resourceIdentifier );
        }
        catch ( RbacObjectNotFoundException e )
        {
            resource = new JdoResource();
            resource.setIdentifier( resourceIdentifier );
        }
        permission.setResource( resource );

        return permission;
    }

    public Permission savePermission( Permission permission )
        throws RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( permission );
        
        return (Permission) saveObject( permission );
    }

    public boolean permissionExists( Permission permission )
    {
        return objectExistsInJdo( permission );
    }

    public boolean permissionExists( String name )
    {
        return objectExistsInJdoById( JdoPermission.class, name );
    }

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Permission) getObjectById( JdoPermission.class, permissionName );
    }

    public List getAllPermissions()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoPermission.class );
    }

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( permission );
        removeObject( permission );
    }

    public Set getAssignedPermissions( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        List list = getAllObjectsDetached( JdoPermission.class, "name ascending" );

        // TODO: figure out how to query using UNIQUE for this from jdo directly.
        Set uniquePerms = new HashSet();
        Iterator it = list.iterator();
        while ( it.hasNext() )
        {
            Permission perm = (Permission) it.next();
            if ( !uniquePerms.contains( perm ) )
            {
                uniquePerms.add( perm );
            }
        }

        return uniquePerms;
    }

    // ----------------------------------------------------------------------
    // Operation methods
    // ----------------------------------------------------------------------
    /**
     * Creates an implementation specific {@link Operation}.
     *
     * Note: this method does not add the {@link Operation} to the underlying store.
     *       a call to {@link #addOperation(Operation)} is required to track the operation created
     *       with this method call.
     *
     * @param name the name.
     * @return the new Operation.
     */
    public Operation createOperation( String name )
    {
        Operation operation;
        
        try
        {
            operation = getOperation( name );
        }
        catch ( RbacObjectNotFoundException e )
        {
            operation = new JdoOperation();
            operation.setName( name );
        }

        return operation;
    }

    public Operation saveOperation( Operation operation )
        throws RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( operation );
        return (Operation) saveObject( operation );
    }
    
    public boolean operationExists( Operation operation )
    {
        return objectExistsInJdo( operation );
    }

    public boolean operationExists( String name )
    {
        return objectExistsInJdoById( JdoOperation.class, name );
    }

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Operation) getObjectById( JdoOperation.class, operationName );
    }

    public List getAllOperations()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoOperation.class );
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( operation );
        removeObject( operation );
    }

    // ----------------------------------------------------------------------
    // Resource methods
    // ----------------------------------------------------------------------
    /**
     * Creates an implementation specific {@link Resource}.
     *
     * Note: this method does not add the {@link Resource} to the underlying store.
     *       a call to {@link #addResource(Resource)} is required to track the resource created
     *       with this method call.
     *
     * @param identifier the identifier.
     * @return the new Resource.
     */
    public Resource createResource( String identifier )
    {
        Resource resource;
        
        try
        {
            resource = getResource( identifier );
            getLogger().debug( "Create Resource [" + identifier + "] Returning Existing." );
        }
        catch ( RbacObjectNotFoundException e )
        {
            resource = new JdoResource();
            resource.setIdentifier( identifier );
            getLogger().debug( "Create Resource [" + identifier + "] New JdoResource." );
        }
        
        return resource;
    }

    public Resource saveResource( Resource resource )
        throws RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( resource );
        return (Resource) saveObject( resource );
    }
    
    public boolean resourceExists( Resource resource )
    {
        return objectExistsInJdo( resource );
    }

    public boolean resourceExists( String identifier )
    {
        return objectExistsInJdoById( JdoResource.class, identifier );
    }

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Resource) getObjectById( JdoResource.class, resourceIdentifier );
    }

    public List getAllResources()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoResource.class );
    }

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( resource );
        removeObject( resource );
    }

    // ----------------------------------------------------------------------
    // User Assignment methods
    // ----------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link UserAssignment}.
     *
     * Note: this method does not add the {@link UserAssignment} to the underlying store.
     *       a call to {@link #addUserAssignment(UserAssignment)} is required to track the user
     *       assignment created with this method call.
     *
     * @param principal the principal reference to the user.
     * @return the new UserAssignment with an empty (non-null) {@link UserAssignment#getRoles()} object.
     */
    public UserAssignment createUserAssignment( String principal )
    {
        UserAssignment ua;
        
        try
        {
            ua = getUserAssignment( principal );
        }
        catch ( RbacObjectNotFoundException e )
        {
            ua = new JdoUserAssignment();
            ua.setPrincipal( principal );
        }

        return ua;
    }

    /**
     * Method addUserAssignment
     *
     * @param userAssignment
     */
    public UserAssignment saveUserAssignment( UserAssignment userAssignment )
        throws RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( userAssignment );
        return (UserAssignment) saveObject( userAssignment );
    }
    
    public boolean userAssignmentExists( String principal )
    {
        return objectExistsInJdoById( JdoUserAssignment.class, principal );
    }

    public boolean userAssignmentExists( UserAssignment assignment )
    {
        return objectExistsInJdo( assignment );
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (UserAssignment) getObjectById( JdoUserAssignment.class, principal );
    }

    /**
     * Method getAssignments
     */
    public List getAllUserAssignments()
        throws RbacStoreException
    {
        return getAllObjectsDetached( JdoUserAssignment.class );
    }

    /**
     * Method removeAssignment
     *
     * @param userAssignment
     */
    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( userAssignment );
        removeObject( userAssignment );
    }

    // ------------------------------------------------------------------
    // UserAssignment Utility Methods
    // ------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();

        /* DEBUG STUFF
         System.err.println( "PersistenceManagerFactory.retainValues = " + pmf.getRetainValues() );
         System.err.println( "PersistenceManagerFactory.restoreValues = " + pmf.getRestoreValues() );

         Collection coll = pmf.supportedOptions();
         Iterator it = coll.iterator();
         while ( it.hasNext() )
         {
         System.err.println( "Supported Option: " + it.next() );
         }
         */
    }

    // ----------------------------------------------------------------------
    // jdo utility methods
    // ----------------------------------------------------------------------
    
    private Object saveObject( Object object )
    {
        if( objectExistsInJdo( object ) )
        {
            return updateObject( object );
        }
        else
        {
            return addObject( object );
        }
    }

    private boolean objectExistsInJdo( Object object )
    {
        return ( JDOHelper.getObjectId( object ) != null );
    }

    private boolean objectExistsInJdoById( Class clazz, Object id )
    {
        try
        {
            Object o = getObjectById( clazz, id );
            return ( o != null );
        }
        catch ( RbacObjectNotFoundException e )
        {
            return false;
        }
    }
    
    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }

    private Object getObjectById( Class clazz, Object id )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        try
        {
            if ( id == null )
            {
                throw new RbacObjectNotFoundException( "Unable to find RBAC object because id was null" );
            }
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id.toString() );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new RbacObjectNotFoundException( "Unable to find RBAC Object '" + id + "' of type " + clazz.getName(),
                                                   e );
        }
        catch ( PlexusStoreException e )
        {
            throw new RbacStoreException( "Unable to get rbac object id '" + id + "' of type " + clazz.getName(), e );
        }
    }

    private Object getObjectById( Class clazz, Object id, String fetchGroup )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        try
        {
            if ( id == null )
            {
                throw new RbacObjectNotFoundException( "Unable to find RBAC object because id was null" );
            }

            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id.toString(), fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new RbacObjectNotFoundException( "Unable to find RBAC Object '" + id + "' of type " + clazz.getName()
                + " using fetch-group '" + fetchGroup + "'", e );
        }
        catch ( PlexusStoreException e )
        {
            throw new RbacStoreException( "Unable to get rbac object id '" + id + "' of type " + clazz.getName()
                + " using fetch-group '" + fetchGroup + "'", e );
        }
    }

    private List getAllObjectsDetached( Class clazz )
    {
        return getAllObjectsDetached( clazz, null );
    }

    private List getAllObjectsDetached( Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( clazz, null, fetchGroup );
    }

    private List getAllObjectsDetached( Class clazz, String ordering, String fetchGroup )
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), clazz, ordering, fetchGroup );
    }

    public Object removeObject( Object o )
    {
        if ( o == null )
        {
            throw new RbacStoreException( "Unable to remove null object '" + o.getClass().getName() + "'" );
        }

        PlexusJdoUtils.removeObject( getPersistenceManager(), o );
        return o;
    }

    private Object updateObject( Object object )
        throws RbacStoreException
    {
        try
        {
            return PlexusJdoUtils.updateObject( getPersistenceManager(), object );
        }
        catch ( PlexusStoreException e )
        {
            throw new RbacStoreException( "Unable to update the '" + object.getClass().getName()
                + "' object in the jdo database.", e );
        }
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    }

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }

}
