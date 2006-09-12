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
import org.codehaus.plexus.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
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
{
    /**
     * @plexus.requirement
     */
    private JdoTool jdo;
    
    private static final String ROLE_DETAIL = "role-child-detail";
    
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
        
        return (Role) jdo.saveObject( role, new String[] { ROLE_DETAIL } );
    }
    
    public boolean roleExists( Role role )
    {
        return jdo.objectExists( role );
    }

    public boolean roleExists( String name )
    {
        return jdo.objectExistsById( JdoRole.class, name );
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
        return (Role) jdo.getObjectById( JdoRole.class, roleName, ROLE_DETAIL );
    }

    /**
     * Method getRoles
     */
    public List getAllRoles()
        throws RbacStoreException
    {
        return jdo.getAllObjects( JdoRole.class );
    }

    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( role );
        jdo.removeObject( role );
    }
    
    public void saveRoles( Collection roles )
        throws RbacObjectInvalidException, RbacStoreException
    {
        if ( roles == null )
        {
            // Nothing to do.
            return;
        }

        // This is done in JdoRbacManager as opposed to JdoTool as we need to assertValid() on each role and
        // also wrap the entire collection into a single atomic save/makePersistent.

        PersistenceManager pm = jdo.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Iterator it = roles.iterator();
            while ( it.hasNext() )
            {
                Role role = (Role) it.next();

                if ( ( JDOHelper.getObjectId( role ) != null ) && !JDOHelper.isDetached( role ) )
                {
                    // This is a fatal error that means we need to fix our code.
                    // Leave it as a JDOUserException, it's intentional.
                    throw new RbacStoreException( "Existing Role is not detached: " + role );
                }

                RBACObjectAssertions.assertValid( role );

                pm.makePersistent( role );
            }

            tx.commit();
        }
        finally
        {
            jdo.rollbackIfActive( tx );
        }
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
        
        return (Permission) jdo.saveObject( permission, null );
    }

    public boolean permissionExists( Permission permission )
    {
        return jdo.objectExists( permission );
    }

    public boolean permissionExists( String name )
    {
        return jdo.objectExistsById( JdoPermission.class, name );
    }

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Permission) jdo.getObjectById( JdoPermission.class, permissionName, null );
    }

    public List getAllPermissions()
        throws RbacStoreException
    {
        return jdo.getAllObjects( JdoPermission.class );
    }

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( permission );
        jdo.removeObject( permission );
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
        return (Operation) jdo.saveObject( operation, null );
    }
    
    public boolean operationExists( Operation operation )
    {
        return jdo.objectExists( operation );
    }

    public boolean operationExists( String name )
    {
        return jdo.objectExistsById( JdoOperation.class, name );
    }

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Operation) jdo.getObjectById( JdoOperation.class, operationName, null );
    }

    public List getAllOperations()
        throws RbacStoreException
    {
        return jdo.getAllObjects( JdoOperation.class );
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( operation );
        jdo.removeObject( operation );
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
        return (Resource) jdo.saveObject( resource, null );
    }
    
    public boolean resourceExists( Resource resource )
    {
        return jdo.objectExists( resource );
    }

    public boolean resourceExists( String identifier )
    {
        return jdo.objectExistsById( JdoResource.class, identifier );
    }

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (Resource) jdo.getObjectById( JdoResource.class, resourceIdentifier, null );
    }

    public List getAllResources()
        throws RbacStoreException
    {
        return jdo.getAllObjects( JdoResource.class );
    }

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException
    {
        RBACObjectAssertions.assertValid( resource );
        jdo.removeObject( resource );
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
        assertValid( "Save User Assignment", userAssignment );
        
        JdoTool.dumpObjectState( System.err, userAssignment );
        
        return (UserAssignment) jdo.saveObject( userAssignment, new String[] { ROLE_DETAIL } );
    }
    
    public boolean userAssignmentExists( String principal )
    {
        return jdo.objectExistsById( JdoUserAssignment.class, principal );
    }

    public boolean userAssignmentExists( UserAssignment assignment )
    {
        return jdo.objectExists( assignment );
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        return (UserAssignment) jdo.getObjectById( JdoUserAssignment.class, principal, ROLE_DETAIL );
    }
    
    public void assertValid( String scope, UserAssignment assignment )
        throws RbacObjectInvalidException
    {
        if ( assignment == null )
        {
            throw new RbacObjectInvalidException( scope, "Null UserAssigment object is invalid." );
        }

        if ( StringUtils.isEmpty( assignment.getPrincipal() ) )
        {
            throw new RbacObjectInvalidException( scope, "UserAssigment.principal cannot be empty." );
        }

        if ( assignment.getRoles() == null )
        {
            throw new RbacObjectInvalidException( scope, "UserAssignment.roles cannot be null." );
        }

        if ( assignment.getRoles().isEmpty() )
        {
            throw new RbacObjectInvalidException( scope, "UserAssignment.roles cannot be empty." );
        }

        int i = 0;
        Iterator it = assignment.getRoles().iterator();
        while ( it.hasNext() )
        {
            Role role = (Role) it.next();
            JdoTool.dumpObjectState( System.err, role );
            RBACObjectAssertions.assertValid( "UserAssignment.roles[" + i + "]", role );
            i++;
        }
    }

    /**
     * Method getAssignments
     */
    public List getAllUserAssignments()
        throws RbacStoreException
    {
        return jdo.getAllObjects( JdoUserAssignment.class );
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
        jdo.removeObject( userAssignment );
    }

}
