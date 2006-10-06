package org.codehaus.plexus.security.authorization.store.test;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacPermanentException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * AbstractRbacManagerTestCase 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractRbacManagerTestCase
    extends PlexusTestCase
{
    private RBACManager rbacManager = null;

    private RbacManagerEventTracker eventTracker = null;

    public RBACManager getRbacManager()
    {
        return rbacManager;
    }

    public void setRbacManager( RBACManager store )
    {
        this.rbacManager = store;
        if ( this.rbacManager != null )
        {
            this.eventTracker = new RbacManagerEventTracker();
            this.rbacManager.addListener( eventTracker );
        }
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    protected void tearDown()
        throws Exception
    {
        if ( getRbacManager() != null )
        {
            release( getRbacManager() );
        }
        super.tearDown();
    }

    private Role getAdminRole() throws RbacManagerException
    {
        Role role = getRbacManager().createRole( "ADMIN" );
        role.setAssignable( false );

        Permission perm = getRbacManager().createPermission( "EDIT_ANY_USER", "EDIT", "User:*" );

        role.addPermission( perm );

        return role;
    }

    private Role getDeveloperRole() throws RbacManagerException
    {
        Role role = getRbacManager().createRole( "DEVELOPER" );
        role.setAssignable( true );

        Permission perm = getRbacManager().createPermission( "EDIT_MY_USER", "EDIT", "User:Self" );

        role.addPermission( perm );

        return role;
    }

    private Role getProjectAdminRole() throws RbacManagerException
    {
        Role role = getRbacManager().createRole( "PROJECT_ADMIN" );
        role.setAssignable( true );

        Permission perm = getRbacManager().createPermission( "EDIT_PROJECT", "EDIT", "Project:Foo" );

        role.addPermission( perm );

        return role;
    }

    private Role getSuperDeveloperRole()
    {
        Role role = getRbacManager().createRole( "SUPER_DEVELOPER" );
        role.setAssignable( true );

        return role;
    }

    public void testStoreInitialization()
        throws Exception
    {
        assertNotNull( getRbacManager() );

        Role role = getAdminRole();

        assertNotNull( role );

        Role added = getRbacManager().saveRole( role );

        assertEquals( 1, getRbacManager().getAllRoles().size() );

        assertNotNull( added );

        getRbacManager().removeRole( added );

        assertEquals( 0, getRbacManager().getAllRoles().size() );

        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 1, eventTracker.addedRoleNames.size() );
        assertEquals( 1, eventTracker.removedRoleNames.size() );
        assertEquals( 1, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testResources()
        throws Exception
    {
        assertNotNull( getRbacManager() );

        Resource resource = getRbacManager().createResource( "foo" );
        Resource resource2 = getRbacManager().createResource( "bar" );

        assertNotNull( resource );

        Resource added = getRbacManager().saveResource( resource );
        assertNotNull( added );
        Resource added2 = getRbacManager().saveResource( resource2 );
        assertNotNull( added2 );

        assertEquals( 2, getRbacManager().getAllResources().size() );

        getRbacManager().removeResource( added );

        assertEquals( 1, getRbacManager().getAllResources().size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 0, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 0, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testAddGetPermission()
        throws RbacObjectInvalidException, RbacManagerException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );
        assertEquals( 2, getRbacManager().getAllPermissions().size() );

        Permission createUserPerm = getRbacManager().createPermission( "CREATE_USER", "CREATE", "User" );

        // perm shouldn't exist in manager (yet)
        assertEquals( 2, getRbacManager().getAllPermissions().size() );

        adminRole.addPermission( createUserPerm );
        getRbacManager().saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 3, getRbacManager().getAllPermissions().size() );
        Permission fetched = getRbacManager().getPermission( "CREATE_USER" );
        assertNotNull( fetched );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 3, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testAddGetRole()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        Role develRole = getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );

        Role actualAdmin = getRbacManager().getRole( adminRole.getName() );
        Role actualDevel = getRbacManager().getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 2, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }
    
    public void testAllowRoleWithoutPermissions()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );
        
        String rolename = "Test Role";
        
        Role testRole = getRbacManager().createRole( rolename );
        testRole = getRbacManager().saveRole( testRole );
        
        assertNotNull( testRole );
        assertEquals( 1, getRbacManager().getAllRoles().size() );
        assertEquals( 0, getRbacManager().getAllPermissions().size() );
        
        Role actualRole = getRbacManager().getRole( rolename );

        assertEquals( testRole, actualRole );
        assertEquals( 1, getRbacManager().getAllRoles().size() );
        assertEquals( 0, getRbacManager().getAllPermissions().size() );

        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );

        assertEquals( 1, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 0, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testAddGetChildRole()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        assertNotNull( manager );

        Role adminRole = manager.saveRole( getAdminRole() );
        Role develRole = manager.saveRole( getDeveloperRole() );

        assertEquals( 2, manager.getAllRoles().size() );

        Role actualAdmin = manager.getRole( adminRole.getName() );
        Role actualDevel = manager.getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );

        // Now add a child role.
        manager.addChildRole( develRole, getProjectAdminRole() );

        manager.saveRole( develRole );

        assertEquals( 3, manager.getAllRoles().size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 3, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 3, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testAddGetChildRoleViaName()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        assertNotNull( manager );

        Role adminRole = manager.saveRole( getAdminRole() );
        Role develRole = manager.saveRole( getDeveloperRole() );

        assertEquals( 2, manager.getAllRoles().size() );

        Role actualAdmin = manager.getRole( adminRole.getName() );
        Role actualDevel = manager.getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );

        // Now do a child role.
        Role projectRole = getProjectAdminRole();
        String projectRoleName = projectRole.getName();
        manager.saveRole( projectRole );

        develRole.addChildRoleName( projectRoleName );

        manager.saveRole( develRole );

        assertEquals( 3, manager.getAllRoles().size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 3, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 3, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testUserAssignmentAddRole()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        Role adminRole = manager.saveRole( getAdminRole() );

        assertEquals( 1, manager.getAllRoles().size() );

        String adminPrincipal = "admin";

        UserAssignment assignment = manager.createUserAssignment( adminPrincipal );

        assignment.addRoleName( adminRole );

        manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 1, manager.getAllRoles().size() );

        UserAssignment ua = manager.getUserAssignment( adminPrincipal );
        assertNotNull( ua );

        Role fetched = (Role) manager.getRole( "ADMIN" );
        assertNotNull( fetched );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 1, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 1, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testUserAssignmentWithChildRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        Role developerRole = manager.saveRole( getDeveloperRole() );

        Role adminRole = getAdminRole();

        adminRole.addChildRoleName( developerRole.getName() );

        adminRole = manager.saveRole( adminRole );

        String adminPrincipal = "admin";
        UserAssignment assignment = manager.createUserAssignment( adminPrincipal );
        assignment.addRoleName( adminRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, assignment.getRoleNames().size() );
        assertEquals( 1, manager.getAssignedRoles( adminPrincipal ).size() );
    }

    public void testGetAssignedPermissionsNoChildRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        Role admin = getAdminRole();

        admin = manager.saveRole( admin );

        assertEquals( 1, manager.getAllRoles().size() );

        String adminPrincipal = "admin";

        UserAssignment ua = manager.createUserAssignment( adminPrincipal );

        ua.addRoleName( admin );

        manager.saveUserAssignment( ua );

        assertEquals( 1, manager.getAllUserAssignments().size() );

        Set assignedPermissions = manager.getAssignedPermissions( adminPrincipal );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 1, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 1, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testGlobalResource() throws RbacManagerException
    {
        RBACManager manager = getRbacManager();
        Permission editConfiguration = manager.createPermission( "Edit Configuration" );
        editConfiguration.setOperation( manager.createOperation( "edit-configuration" ) );
        editConfiguration.setResource( manager.getGlobalResource() );
        manager.savePermission( editConfiguration );

        assertEquals( 1, manager.getAllPermissions().size() );
        assertEquals( 1, manager.getAllOperations().size() );
        assertEquals( 1, manager.getAllResources().size() );

        Permission deleteConfiguration = manager.createPermission( "Delete Configuration" );
        deleteConfiguration.setOperation( manager.createOperation( "delete-configuration" ) );
        deleteConfiguration.setResource( manager.getGlobalResource() );
        manager.savePermission( deleteConfiguration );

        assertEquals( 2, manager.getAllPermissions().size() );
        assertEquals( 2, manager.getAllOperations().size() );
        assertEquals( 1, manager.getAllResources().size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 0, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 2, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testGlobalResourceOneLiner() throws RbacObjectInvalidException, RbacManagerException
    {
        RBACManager manager = getRbacManager();
        manager.savePermission( manager.createPermission( "Edit Configuration", "edit-configuration", Resource.GLOBAL ) );
        manager.savePermission( manager.createPermission( "Delete Configuration", "delete-configuration",
                                                          Resource.GLOBAL ) );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 0, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 2, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testUserAssignmentAddRemoveSecondRole()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        // Setup User / Assignment with 1 role.
        String username = "bob";

        Role developerRole = getDeveloperRole();
        manager.saveRole( developerRole );

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( developerRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 1, manager.getAllRoles().size() );

        // Create another role add it to manager.
        Role projectAdmin = getProjectAdminRole();
        String projectAdminRoleName = projectAdmin.getName();
        projectAdmin = manager.saveRole( projectAdmin );

        // Get User Assignment, add a second role
        UserAssignment bob = manager.getUserAssignment( username );
        bob.addRoleName( projectAdminRoleName );
        bob = manager.saveUserAssignment( bob );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 2, manager.getAllRoles().size() );
        assertEquals( 2, bob.getRoleNames().size() );
        assertEquals( 0, manager.getUnassignedRoles( bob.getPrincipal() ).size() );

        List roles = bob.getRoleNames();
        assertEquals( 2, roles.size() );

        // Remove 1 role from bob, end up with 1 role for bob.
        roles.remove( projectAdminRoleName );
        assertEquals( 1, roles.size() );
        bob.setRoleNames( roles );
        bob = manager.saveUserAssignment( bob );
        assertEquals( "Should only have 1 role under bob now.", 1, bob.getRoleNames().size() );
        assertEquals( "Should have 2 total roles still.", 2, manager.getAllRoles().size() );
        assertEquals( "Should have 1 assignable role", 1, manager.getUnassignedRoles( bob.getPrincipal() ).size() );

        // Fetch bob again. see if role is missing.
        UserAssignment cousin = manager.getUserAssignment( username );
        assertEquals( 1, cousin.getRoleNames().size() );

        assertEquals( "Should only have 1 role under bob now.", 1, cousin.getRoleNames().size() );
        assertEquals( "Should have 2 total roles still.", 2, manager.getAllRoles().size() );

        // remove the last role
        roles.remove( developerRole.getName() );
        bob.setRoleNames( roles );
        bob = manager.saveUserAssignment( bob );        
        assertEquals( "Should have 2 assignable roles.", 2, manager.getUnassignedRoles( bob.getPrincipal() ).size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 2, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testUserAssignmentMultipleRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        // Setup User / Assignment with 1 role.
        String username = "bob";

        Role devRole = getDeveloperRole();
        manager.saveRole( devRole );

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 1, manager.getAllRoles().size() );

        // assign the same role again to the same user
        assignment.addRoleName( devRole.getName() );
        manager.saveUserAssignment( assignment );

        // we certainly shouldn't have 2 roles here now
        assertEquals( 1, assignment.getRoleNames().size() );
        
        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );
        
        assertEquals( 1, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 1, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testUserAssignmentMultipleRolesWithChildRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        // Setup User / Assignment with 1 role.
        String username = "bob";

        Role devRole = getDeveloperRole();
        Role devPlusRole = getSuperDeveloperRole();
        devPlusRole.setChildRoleNames( Collections.singletonList( devRole.getName() ) );
        manager.saveRole( devRole );
        manager.saveRole( devPlusRole );

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( "should be only one role assigned", 1, manager.getAssignedRoles( assignment.getPrincipal() ).size() );
        assertEquals( "should be one role left to assign", 1, manager.getUnassignedRoles( assignment.getPrincipal() ).size() );
        assertEquals( 2, manager.getAllRoles().size() );

        // assign the same role again to the same user
        assignment.addRoleName( devRole.getName() );
        manager.saveUserAssignment( assignment );

        // we certainly shouldn't have 2 roles here now
        assertEquals( 1, assignment.getRoleNames().size() );

        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );

        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 1, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }

    public void testGetAssignedRoles()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();

        Role adminRole = manager.saveRole( getAdminRole() );
        Role projectRole = manager.saveRole( getProjectAdminRole() );
        Role developerRole = manager.saveRole( getDeveloperRole() );

        // Setup 3 roles.
        String roleName = developerRole.getName();

        assertEquals( 3, manager.getAllRoles().size() );

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( roleName );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, assignment.getRoleNames().size() );
        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 3, manager.getAllRoles().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedRoles = manager.getAssignedRoles( username );

        assertNotNull( assignedRoles );
        assertEquals( 1, assignedRoles.size() );
    }

    public void testGetAssignedPermissions()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getRbacManager();
        // Setup 3 roles.
        manager.saveRole( getAdminRole() );
        manager.saveRole( getProjectAdminRole() );
        Role added = manager.saveRole( getDeveloperRole() );
        String roleName = added.getName();

        assertEquals( 3, manager.getAllRoles().size() );
        assertEquals( 3, manager.getAllPermissions().size() );

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( roleName );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 3, manager.getAllRoles().size() );
        assertEquals( 3, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = manager.getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
    }

    public Role getChildRole( RBACManager manager, Role role, String expectedChildRoleName, int childRoleCount )
        throws RbacManagerException, RbacObjectNotFoundException
    {
        assertTrue( role.hasChildRoles() );
        List childNames = role.getChildRoleNames();
        assertNotNull( childNames );
        assertEquals( 1, childNames.size() );
        String childName = (String) childNames.get( 0 );
        assertNotNull( childName );
        Role childRole = (Role) manager.getRole( childName );
        assertNotNull( childRole );
        assertEquals( expectedChildRoleName, childRole.getName() );

        return childRole;
    }

    public void testGetRolesDeep()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( "Developer" );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 4, manager.getAllRoles().size() );
        assertEquals( 6, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Role devel = manager.getRole( "Developer" );
        assertNotNull( devel );

        // First Depth.
        Role trusted = getChildRole( manager, devel, "Trusted Developer", 1 );

        // Second Depth.
        Role sysAdmin = getChildRole( manager, trusted, "System Administrator", 1 );

        // Third Depth.
        Role userAdmin = getChildRole( manager, sysAdmin, "User Administrator", 1 );
    }

    public void testGetAssignedPermissionsDeep()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( "Developer" );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 4, manager.getAllRoles().size() );
        assertEquals( 6, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = manager.getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 6, assignedPermissions.size() );
    }

    public void testLargeApplicationInit()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();

        assertEquals( 6, manager.getAllPermissions().size() );
        assertEquals( 11, manager.getAllOperations().size() );
        assertEquals( 4, manager.getAllRoles().size() );
    }

    private RBACManager getArchivaDefaults()
        throws RbacObjectNotFoundException, RbacObjectInvalidException, RbacManagerException
    {
        RBACManager manager = getRbacManager();

        if ( !manager.operationExists( "add-repository" ) )
        {
            Operation operation = manager.createOperation( "add-repository" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "edit-repository" ) )
        {
            Operation operation = manager.createOperation( "edit-repository" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "delete-repository" ) )
        {
            Operation operation = manager.createOperation( "delete-repository" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "edit-configuration" ) )
        {
            Operation operation = manager.createOperation( "edit-configuration" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "run-indexer" ) )
        {
            Operation operation = manager.createOperation( "run-indexer" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "regenerate-index" ) )
        {
            Operation operation = manager.createOperation( "regenerate-index" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "get-reports" ) )
        {
            Operation operation = manager.createOperation( "get-reports" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "regenerate-reports" ) )
        {
            Operation operation = manager.createOperation( "regenerate-reports" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "edit-user" ) )
        {
            Operation operation = manager.createOperation( "edit-user" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "edit-all-users" ) )
        {
            Operation operation = manager.createOperation( "edit-all-users" );
            manager.saveOperation( operation );
        }

        if ( !manager.operationExists( "remove-roles" ) )
        {
            Operation operation = manager.createOperation( "remove-roles" );
            manager.saveOperation( operation );
        }

        if ( !manager.permissionExists( "Edit Configuration" ) )
        {
            Permission editConfiguration = manager.createPermission( "Edit Configuration", "edit-configuration",
                                                                     manager.getGlobalResource().getIdentifier() );
            manager.savePermission( editConfiguration );
        }

        if ( !manager.permissionExists( "Run Indexer" ) )
        {
            Permission runIndexer = manager.createPermission( "Run Indexer", "run-indexer", manager.getGlobalResource()
                .getIdentifier() );

            manager.savePermission( runIndexer );
        }

        if ( !manager.permissionExists( "Add Repository" ) )
        {
            Permission runIndexer = manager.createPermission( "Add Repository", "add-repository", manager
                .getGlobalResource().getIdentifier() );
            manager.savePermission( runIndexer );
        }

        if ( !manager.permissionExists( "Edit All Users" ) )
        {
            Permission editAllUsers = manager.createPermission( "Edit All Users", "edit-all-users", manager
                .getGlobalResource().getIdentifier() );

            manager.savePermission( editAllUsers );
        }

        if ( !manager.permissionExists( "Remove Roles" ) )
        {
            Permission editAllUsers = manager.createPermission( "Remove Roles", "remove-roles", manager
                .getGlobalResource().getIdentifier() );

            manager.savePermission( editAllUsers );
        }

        if ( !manager.permissionExists( "Regenerate Index" ) )
        {
            Permission regenIndex = manager.createPermission( "Regenerate Index", "regenerate-index", manager
                .getGlobalResource().getIdentifier() );

            manager.savePermission( regenIndex );
        }

        if ( !manager.roleExists( "User Administrator" ) )
        {
            Role userAdmin = manager.createRole( "User Administrator" );
            userAdmin.addPermission( manager.getPermission( "Edit All Users" ) );
            userAdmin.addPermission( manager.getPermission( "Remove Roles" ) );
            userAdmin.setAssignable( true );
            manager.saveRole( userAdmin );
        }

        if ( !manager.roleExists( "System Administrator" ) )
        {
            Role admin = manager.createRole( "System Administrator" );
            admin.addChildRoleName( "User Administrator" );
            admin.addPermission( manager.getPermission( "Edit Configuration" ) );
            admin.addPermission( manager.getPermission( "Run Indexer" ) );
            admin.addPermission( manager.getPermission( "Add Repository" ) );
            admin.addPermission( manager.getPermission( "Regenerate Index" ) );
            admin.setAssignable( true );
            manager.saveRole( admin );
        }

        if ( !manager.roleExists( "Trusted Developer" ) )
        {
            Role developer = manager.createRole( "Trusted Developer" );
            developer.addChildRoleName( "System Administrator" );
            developer.addPermission( manager.getPermission( "Run Indexer" ) );
            developer.setAssignable( true );
            manager.saveRole( developer );
        }

        if ( !manager.roleExists( "Developer" ) )
        {
            Role developer = manager.createRole( "Developer" );
            developer.addChildRoleName( "Trusted Developer" );
            developer.addPermission( manager.getPermission( "Run Indexer" ) );
            developer.setAssignable( true );
            manager.saveRole( developer );
        }

        return manager;
    }
    
    public void testAddRemovePermanentPermission()
        throws RbacObjectInvalidException, RbacManagerException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );
        assertEquals( 2, getRbacManager().getAllPermissions().size() );

        Permission createUserPerm = getRbacManager().createPermission( "CREATE_USER", "CREATE", "User" );
        createUserPerm.setPermanent( true );

        // perm shouldn't exist in manager (yet)
        assertEquals( 2, getRbacManager().getAllPermissions().size() );

        adminRole.addPermission( createUserPerm );
        getRbacManager().saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 3, getRbacManager().getAllPermissions().size() );
        Permission fetched = getRbacManager().getPermission( "CREATE_USER" );
        assertNotNull( fetched );
        
        // Attempt to remove perm now.
        try
        {
            // Use permission name technique first.
            getRbacManager().removePermission( "CREATE_USER" );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use permission object technique next.
            getRbacManager().removePermission( fetched );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }
        
        // Assert some event tracker stuff
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );

        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 3, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }
    
    public void testAddRemovePermanentRole()
        throws RbacManagerException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getAdminRole();
        adminRole.setPermanent( true );
        
        adminRole = getRbacManager().saveRole( adminRole );
        Role develRole = getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );

        Role actualAdmin = getRbacManager().getRole( adminRole.getName() );
        Role actualDevel = getRbacManager().getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );
        
        // Attempt to remove perm now.
        try
        {
            // Use role name technique first.
            getRbacManager().removeRole( adminRole.getName() );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use role object technique next.
            getRbacManager().removeRole( adminRole );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        /* Assert some event tracker stuff */
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );

        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 2, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }
    
    public void testAddRemovePermanentOperation()
    throws RbacManagerException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );
        assertEquals( 2, getRbacManager().getAllPermissions().size() );

        Permission createUserPerm = getRbacManager().createPermission( "CREATE_USER", "CREATE", "User" );
        createUserPerm.getOperation().setPermanent( true );
        
        // perm shouldn't exist in manager (yet)
        assertEquals( 2, getRbacManager().getAllPermissions().size() );
        assertEquals( 1, getRbacManager().getAllOperations().size() );

        adminRole.addPermission( createUserPerm );
        getRbacManager().saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 2, getRbacManager().getAllOperations().size() );
        Operation fetched = getRbacManager().getOperation( "CREATE" );
        assertNotNull( fetched );
        
        // Attempt to remove operation now.
        try
        {
            // Use operation name technique first.
            getRbacManager().removeOperation( "CREATE" );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use operation object technique next.
            getRbacManager().removeOperation( fetched );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }
        
        // Assert some event tracker stuff
        assertNotNull( eventTracker );
        assertEquals( 1, eventTracker.initCount );
        assertTrue( eventTracker.lastDbFreshness.booleanValue() );

        assertEquals( 2, eventTracker.addedRoleNames.size() );
        assertEquals( 0, eventTracker.removedRoleNames.size() );
        assertEquals( 3, eventTracker.addedPermissionNames.size() );
        assertEquals( 0, eventTracker.removedPermissionNames.size() );
    }
}
