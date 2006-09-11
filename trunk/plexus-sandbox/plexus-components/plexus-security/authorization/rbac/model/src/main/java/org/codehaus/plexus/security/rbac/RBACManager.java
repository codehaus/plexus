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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RBACManager 
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface RBACManager
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = RBACManager.class.getName();

    // ------------------------------------------------------------------
    // Role Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Role}.
     *
     * Note: this method does not add the {@link Role} to the underlying store.
     *       a call to {@link #addRole(Role)} is required to track the role created with this
     *       method call.
     *
     * @param name the name.
     * @param description the description.
     * @return the new {@link Role} object with an empty (non-null) {@link Role#getChildRoles()} object.
     */
    public Role createRole( String name, String description );

    /**
     * Tests for the existance of a Role.
     *
     * @return true if role exists in store.
     */
    public boolean roleExists( String name );

    public boolean roleExists( Role role );

    /**
     * Method addRole
     *
     * @param role
     */
    public Role addRole( Role role )
        throws RbacObjectInvalidException, RbacStoreException;

    /**
     *
     *
     * @param roleName
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Role getRole( String roleName )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Method getRoles
     */
    public List getAllRoles()
        throws RbacStoreException;

   /**
     * Method removeRole
     *
     * @param role
     */
    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    /**
     * Method removeRole
     *
     * @param roleName
     */
    public void removeRole( String roleName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    /**
     *
     * @param role
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Role updateRole( Role role )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // Permission Methods
    // ------------------------------------------------------------------
    /**
     * Creates an implementation specific {@link Permission}.
     *
     * Note: this method does not add the {@link Permission} to the underlying store.
     *       a call to {@link #addPermission(Permission)} is required to track the permission created
     *       with this method call.
     *
     * @param name the name.
     * @param description the description.
     * @return the new Permission.
     */
    public Permission createPermission( String name, String description );

    /**
     * Creates an implementation specific {@link Permission} with specified {@link Operation},
     * and {@link Resource} identifiers.
     *
     * Note: this method does not add the Permission, Operation, or Resource to the underlying store.
     *       a call to {@link #addPermission(Permission)} is required to track the permission, operation,
     *       or resource created with this method call.
     *
     * @param name the name.
     * @param description the description.
     * @param operationName the {@link Operation#setName(String)} value
     * @param resourceIdentifier the {@link Resource#setIdentifier(String)} value
     * @return the new Permission.
     */
    public Permission createPermission( String name, String description, String operationName, String resourceIdentifier );

    /**
     * Tests for the existance of a permission.
     *
     * @param name the name to test for.
     * @return true if permission exists.
     */
    public boolean permissionExists( String name );

    public boolean permissionExists( Permission permission );

    public Permission addPermission( Permission permission )
        throws RbacObjectInvalidException, RbacStoreException;

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllPermissions()
        throws RbacStoreException;

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removePermission( String permissionName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public Permission updatePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // Operation Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Operation}.
     *
     * Note: this method does not add the {@link Operation} to the underlying store.
     *       a call to {@link #addOperation(Operation)} is required to track the operation created
     *       with this method call.
     *
     * @param name the name.
     * @param description the description.
     * @return the new Operation.
     */
    public Operation createOperation( String name, String description );

    public boolean operationExists( String name );

    public boolean operationExists( Operation operation );

    public Operation addOperation( Operation operation )
        throws RbacObjectInvalidException, RbacStoreException;

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllOperations()
        throws RbacStoreException;

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removeOperation( String operationName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public Operation updateOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // Resource Methods
    // ------------------------------------------------------------------

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
    public Resource createResource( String identifier );

    public boolean resourceExists( String identifier );

    public boolean resourceExists( Resource resource );

    public Resource addResource( Resource resource )
        throws RbacObjectInvalidException, RbacStoreException;

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllResources()
        throws RbacStoreException;

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removeResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public Resource updateResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // UserAssignment Methods
    // ------------------------------------------------------------------

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
    public UserAssignment createUserAssignment( String principal );

    public boolean userAssignmentExists( String principal );

    public boolean userAssignmentExists( UserAssignment assignment );

    /**
     * Method addUserAssignment
     *
     * @param userAssignment
     */
    public UserAssignment addUserAssignment( UserAssignment userAssignment )
        throws RbacObjectInvalidException, RbacStoreException;

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Method getAssignments
     */
    public List getAllUserAssignments()
        throws RbacStoreException;

    /**
     * Method removeAssignment
     *
     * @param userAssignment
     */
    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    /**
     * Method removeAssignment
     *
     * @param principal
     */
    public void removeUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public UserAssignment updateUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // UserAssignment Utility Methods
    // ------------------------------------------------------------------

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
        throws RbacObjectNotFoundException, RbacStoreException;

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
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * returns a list of all assignable roles
     * 
     * @return
     * @throws RbacStoreException
     */
    public List getAllAssignableRoles()
        throws RbacStoreException;

    /**
     * returns the global resource object
     *
     * @return
     * @throws RbacStoreException
     */
    public Resource getGlobalResource()
        throws RbacStoreException;
}