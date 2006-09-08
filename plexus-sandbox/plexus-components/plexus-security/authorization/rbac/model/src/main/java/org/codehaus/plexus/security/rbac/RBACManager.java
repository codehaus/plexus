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
     * Method addRole
     *
     * @param role
     */
    public Role addRole( Role role )
        throws RbacStoreException;

    /**
     *
     *
     * @param roleId
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Role getRole( int roleId )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Method getRoles
     */
    public List getRoles()
        throws RbacStoreException;

    /**
     * Set The roles available to be assigned
     *
     * @param roles
     */
    public void setRoles( List roles )
        throws RbacStoreException;

    /**
     * Method removeRole
     *
     * @param role
     */
    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     *
     * @param role
     * @return
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Role updateRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException;

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
     * @param operation the {@link Operation#setName(String)} value
     * @param resource the {@link Resource#setIdentifier(String)} value
     * @return the new Permission.
     */
    public Permission createPermission( String name, String description, String operation, String resource );

    public Permission addPermission( Permission permission )
        throws RbacStoreException;

    public Permission getPermission( int permissionId )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getPermissions()
        throws RbacStoreException;

    public void setPermissions( List permissions )
        throws RbacStoreException;

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException;

    public Permission updatePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException;

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

    public Operation addOperation( Operation operation )
        throws RbacStoreException;

    public Operation getOperation( int operationId )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getOperations()
        throws RbacStoreException;

    public void setOperations( List operation )
        throws RbacStoreException;

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException;

    public Operation updateOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException;

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

    public Resource addResource( Resource resource )
        throws RbacStoreException;

    public Resource getResource( int resourceId )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getResources()
        throws RbacStoreException;

    public void setResources( List resources )
        throws RbacStoreException;

    public void removeResource( Resource resource )
        throws RbacStoreException;

    public Resource updateResource( Resource resource )
        throws RbacStoreException;

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
    public UserAssignment createUserAssignment( Object principal );

    /**
     * Method addUserAssignment
     *
     * @param userAssignment
     */
    public UserAssignment addUserAssignment( UserAssignment userAssignment )
        throws RbacStoreException;

    public UserAssignment getUserAssignment( Object principal )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Method getAssignments
     */
    public List getUserAssignments()
        throws RbacStoreException;

    /**
     * Set null
     *
     * @param assignments
     */
    public void setUserAssignments( List assignments )
        throws RbacStoreException;

    /**
     * Method removeAssignment
     *
     * @param userAssignment
     */
    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException;

    public UserAssignment updateUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException;


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
    public List getAssignedRoles( Object principal )
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
    public Set getAssignedPermissions( Object principal )
        throws RbacObjectNotFoundException, RbacStoreException;

}