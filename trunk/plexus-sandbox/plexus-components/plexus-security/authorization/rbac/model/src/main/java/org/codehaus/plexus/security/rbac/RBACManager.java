package org.codehaus.plexus.security.rbac;

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

import java.util.Collection;
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
    
    public void addListener( RBACManagerListener listener );
    
    public void removeListener( RBACManagerListener listener );

    // ------------------------------------------------------------------
    // Role Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Role}, or return an existing {@link Role}, depending
     * on the provided <code>name</code> parameter.
     *
     * Note: Be sure to use {@link #saveRole(Role)} in order to persist any changes to the Role. 
     *
     * @param name the name.
     * @return the new {@link Role} object.
     */
    public Role createRole( String name );

    /**
     * Tests for the existance of a Role.
     *
     * @return true if role exists in store.
     */
    public boolean roleExists( String name );

    public boolean roleExists( Role role );

    public Role saveRole( Role role )
        throws RbacObjectInvalidException, RbacStoreException;

    public void saveRoles( Collection roles )
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

    public Map getRoles( Collection roleNames )
        throws RbacObjectNotFoundException, RbacStoreException;

    public void addChildRole( Role role, Role childRole )
        throws RbacObjectInvalidException, RbacStoreException;

    public Map getChildRoles( Role role )
        throws RbacStoreException;

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

    // ------------------------------------------------------------------
    // Permission Methods
    // ------------------------------------------------------------------
    /**
     * Creates an implementation specific {@link Permission}, or return an existing {@link Permission}, depending
     * on the provided <code>name</code> parameter.
     *
     * Note: Be sure to use {@link #savePermission(Permission)} in order to persist any changes to the Role.
     *
     * @param name the name.
     * @return the new Permission.
     */
    public Permission createPermission( String name );

    /**
     * Creates an implementation specific {@link Permission} with specified {@link Operation},
     * and {@link Resource} identifiers.
     *
     * Note: Be sure to use {@link #savePermission(Permission)} in order to persist any changes to the Role.
     *
     * @param name the name.
     * @param operationName the {@link Operation#setName(String)} value
     * @param resourceIdentifier the {@link Resource#setIdentifier(String)} value
     * @return the new Permission.
     */
    public Permission createPermission( String name, String operationName, String resourceIdentifier );

    /**
     * Tests for the existance of a permission.
     *
     * @param name the name to test for.
     * @return true if permission exists.
     */
    public boolean permissionExists( String name );

    public boolean permissionExists( Permission permission );

    public Permission savePermission( Permission permission )
        throws RbacObjectInvalidException, RbacStoreException;

    public Permission getPermission( String permissionName )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllPermissions()
        throws RbacStoreException;

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removePermission( String permissionName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // Operation Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Operation}, or return an existing {@link Operation}, depending
     * on the provided <code>name</code> parameter.
     *
     * Note: Be sure to use {@link #saveOperation(Operation)} in order to persist any changes to the Role.
     *
     * @param name the name.
     * @return the new Operation.
     */
    public Operation createOperation( String name );

    public boolean operationExists( String name );

    public boolean operationExists( Operation operation );

    /**
     * Save the new or existing operation to the store.
     * 
     * @param operation the operation to save (new or existing)
     * @return the Operation that was saved.
     * @throws RbacObjectInvalidException
     * @throws RbacStoreException
     */
    public Operation saveOperation( Operation operation )
        throws RbacObjectInvalidException, RbacStoreException;

    public Operation getOperation( String operationName )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllOperations()
        throws RbacStoreException;

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removeOperation( String operationName )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // Resource Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link Resource}, or return an existing {@link Resource}, depending
     * on the provided <code>identifier</code> parameter.
     *
     * Note: Be sure to use {@link #saveResource(Resource)} in order to persist any changes to the Role.
     *
     * @param identifier the identifier.
     * @return the new Resource.
     */
    public Resource createResource( String identifier );

    public boolean resourceExists( String identifier );

    public boolean resourceExists( Resource resource );

    public Resource saveResource( Resource resource )
        throws RbacObjectInvalidException, RbacStoreException;

    public Resource getResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacStoreException;

    public List getAllResources()
        throws RbacStoreException;

    public void removeResource( Resource resource )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    public void removeResource( String resourceIdentifier )
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacStoreException;

    // ------------------------------------------------------------------
    // UserAssignment Methods
    // ------------------------------------------------------------------

    /**
     * Creates an implementation specific {@link UserAssignment}, or return an existing {@link UserAssignment}, 
     * depending on the provided <code>identifier</code> parameter.
     *
     * Note: Be sure to use {@link #saveUserAssignment(UserAssignment)} in order to persist any changes to the Role.
     *
     * @param principal the principal reference to the user.
     * @return the new UserAssignment object.
     */
    public UserAssignment createUserAssignment( String principal );

    public boolean userAssignmentExists( String principal );

    public boolean userAssignmentExists( UserAssignment assignment );

    /**
     * Method saveUserAssignment
     *
     * @param userAssignment
     */
    public UserAssignment saveUserAssignment( UserAssignment userAssignment )
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
     * @return Collection of {@link Role} objects.
     * @throws RbacObjectNotFoundException
     * @throws RbacStoreException
     */
    public Collection getAssignedRoles( String principal )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Get the Collection of {@link Role} objects for this UserAssignment.
     *
     * @param userAssignment
     * @return Collection of {@link Role} objects for the provided UserAssignment.
     */
    public Collection getAssignedRoles( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException;

    /**
     * Get a list of all assignable roles that are currently not assigned to the specific user.
     * 
     * @param principal
     * @return
     * @throws RbacStoreException
     * @throws RbacObjectNotFoundException
     */
    public Collection getUnassignedRoles( String principal )
        throws RbacStoreException, RbacObjectNotFoundException;

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
     * @throws RbacObjectNotFoundException 
     */
    public List getAllAssignableRoles()
        throws RbacStoreException, RbacObjectNotFoundException;

    /**
     * returns the global resource object
     *
     * @return
     * @throws RbacStoreException
     */
    public Resource getGlobalResource()
        throws RbacStoreException;
}