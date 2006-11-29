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

import net.sf.ehcache.Cache;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.authorization.store.test.utils.RBACDefaults;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.RbacPermanentException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

/**
 * AbstractRbacManagerTestCase
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractRbacManagerTestCase
    extends PlexusTestCase
{
    private RBACManager rbacManager;

    private RbacManagerEventTracker eventTracker;

    private RBACDefaults rbacDefaults;

    public void setRbacManager( RBACManager store )
    {
        this.rbacManager = store;
        if ( this.rbacManager != null )
        {
            this.eventTracker = new RbacManagerEventTracker();
            this.rbacManager.addListener( eventTracker );
        }
        rbacDefaults = new RBACDefaults( rbacManager );
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    protected void tearDown()
        throws Exception
    {
        if ( rbacManager != null )
        {
            release( rbacManager );
        }
        super.tearDown();
    }

    private Role getAdminRole()
        throws RbacManagerException
    {
        Role role = rbacManager.createRole( "ADMIN" );
        role.setAssignable( false );

        Permission perm = rbacManager.createPermission( "EDIT_ANY_USER", "EDIT", "User:*" );

        role.addPermission( perm );

        return role;
    }

    private Role getDeveloperRole()
        throws RbacManagerException
    {
        Role role = rbacManager.createRole( "DEVELOPER" );
        role.setAssignable( true );

        Permission perm = rbacManager.createPermission( "EDIT_MY_USER", "EDIT", "User:Self" );

        role.addPermission( perm );

        return role;
    }

    private Role getProjectAdminRole()
        throws RbacManagerException
    {
        Role role = rbacManager.createRole( "PROJECT_ADMIN" );
        role.setAssignable( true );

        Permission perm = rbacManager.createPermission( "EDIT_PROJECT", "EDIT", "Project:Foo" );

        role.addPermission( perm );

        return role;
    }

    private Role getSuperDeveloperRole()
    {
        Role role = rbacManager.createRole( "SUPER_DEVELOPER" );
        role.setAssignable( true );

        return role;
    }

    public void testStoreInitialization()
        throws Exception
    {
        assertNotNull( rbacManager );

        Role role = getAdminRole();

        assertNotNull( role );

        Role added = rbacManager.saveRole( role );

        assertEquals( 1, rbacManager.getAllRoles().size() );

        assertNotNull( added );

        rbacManager.removeRole( added );

        assertEquals( 0, rbacManager.getAllRoles().size() );

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
        assertNotNull( rbacManager );

        Resource resource = rbacManager.createResource( "foo" );
        Resource resource2 = rbacManager.createResource( "bar" );

        assertNotNull( resource );

        Resource added = rbacManager.saveResource( resource );
        assertNotNull( added );
        Resource added2 = rbacManager.saveResource( resource2 );
        assertNotNull( added2 );

        assertEquals( 2, rbacManager.getAllResources().size() );

        rbacManager.removeResource( added );

        assertEquals( 1, rbacManager.getAllResources().size() );

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
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        Role adminRole = rbacManager.saveRole( getAdminRole() );
        rbacManager.saveRole( getDeveloperRole() );

        assertEquals( 2, rbacManager.getAllRoles().size() );
        assertEquals( 2, rbacManager.getAllPermissions().size() );

        Permission createUserPerm = rbacManager.createPermission( "CREATE_USER", "CREATE", "User" );

        // perm shouldn't exist in manager (yet)
        assertEquals( 2, rbacManager.getAllPermissions().size() );

        adminRole.addPermission( createUserPerm );
        rbacManager.saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 3, rbacManager.getAllPermissions().size() );
        Permission fetched = rbacManager.getPermission( "CREATE_USER" );
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
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        Role adminRole = rbacManager.saveRole( getAdminRole() );
        Role develRole = rbacManager.saveRole( getDeveloperRole() );

        assertEquals( 2, rbacManager.getAllRoles().size() );

        Role actualAdmin = rbacManager.getRole( adminRole.getName() );
        Role actualDevel = rbacManager.getRole( develRole.getName() );

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
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        String rolename = "Test Role";

        Role testRole = rbacManager.createRole( rolename );
        testRole = rbacManager.saveRole( testRole );

        assertNotNull( testRole );
        assertEquals( 1, rbacManager.getAllRoles().size() );
        assertEquals( 0, rbacManager.getAllPermissions().size() );

        Role actualRole = rbacManager.getRole( rolename );

        assertEquals( testRole, actualRole );
        assertEquals( 1, rbacManager.getAllRoles().size() );
        assertEquals( 0, rbacManager.getAllPermissions().size() );

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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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

        Role fetched = manager.getRole( "ADMIN" );
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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

    public void testGlobalResource()
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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

    public void testGlobalResourceOneLiner()
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
        manager
            .savePermission( manager.createPermission( "Edit Configuration", "edit-configuration", Resource.GLOBAL ) );
        manager.savePermission(
            manager.createPermission( "Delete Configuration", "delete-configuration", Resource.GLOBAL ) );

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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;

        Role developerRole = getDeveloperRole();
        manager.saveRole( developerRole );

        // Setup User / Assignment with 1 role.
        String username = "bob";
        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( developerRole );
        manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 1, manager.getAllRoles().size() );

        // Create another role add it to manager.
        Role projectAdmin = getProjectAdminRole();
        String projectAdminRoleName = projectAdmin.getName();
        manager.saveRole( projectAdmin );

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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;

        Role devRole = getDeveloperRole();
        manager.saveRole( devRole );

        // Setup User / Assignment with 1 role.
        String username = "bob";
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;

        Role devRole = getDeveloperRole();
        Role devPlusRole = getSuperDeveloperRole();
        devPlusRole.setChildRoleNames( Collections.singletonList( devRole.getName() ) );
        manager.saveRole( devRole );
        manager.saveRole( devPlusRole );

        // Setup User / Assignment with 1 role.
        String username = "bob";
        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( "should be only one role assigned", 1, manager.getAssignedRoles( assignment.getPrincipal() )
            .size() );
        assertEquals( "should be one role left to assign", 1, manager.getUnassignedRoles( assignment.getPrincipal() )
            .size() );
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;

        manager.saveRole( getAdminRole() );
        manager.saveRole( getProjectAdminRole() );
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
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;
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
        manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 3, manager.getAllRoles().size() );
        assertEquals( 3, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = manager.getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
    }

    public Role getChildRole( RBACManager manager, Role role, String expectedChildRoleName, int childRoleCount )
        throws RbacManagerException
    {
        assertTrue( role.hasChildRoles() );
        List childNames = role.getChildRoleNames();
        assertNotNull( childNames );
        assertEquals( 1, childNames.size() );
        String childName = (String) childNames.get( 0 );
        assertNotNull( childName );
        Role childRole = manager.getRole( childName );
        assertNotNull( childRole );
        assertEquals( expectedChildRoleName, childRole.getName() );

        return childRole;
    }

    public void testGetRolesDeep()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = rbacManager.createUserAssignment( username );
        assignment.addRoleName( "Developer" );
        rbacManager.saveUserAssignment( assignment );

        assertEquals( 1, rbacManager.getAllUserAssignments().size() );
        assertEquals( 4, rbacManager.getAllRoles().size() );
        assertEquals( 6, rbacManager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Role devel = rbacManager.getRole( "Developer" );
        assertNotNull( devel );

        // First Depth.
        Role trusted = getChildRole( rbacManager, devel, "Trusted Developer", 1 );

        // Second Depth.
        Role sysAdmin = getChildRole( rbacManager, trusted, "System Administrator", 1 );

        // Third Depth.
        getChildRole( rbacManager, sysAdmin, "User Administrator", 1 );
    }

    public void testGetAssignedPermissionsDeep()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = rbacManager.createUserAssignment( username );
        assignment.addRoleName( "Developer" );
        rbacManager.saveUserAssignment( assignment );

        assertEquals( 1, rbacManager.getAllUserAssignments().size() );
        assertEquals( 4, rbacManager.getAllRoles().size() );
        assertEquals( 6, rbacManager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = rbacManager.getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 6, assignedPermissions.size() );
    }

    public void testLargeApplicationInit()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        assertEquals( 6, rbacManager.getAllPermissions().size() );
        assertEquals( 11, rbacManager.getAllOperations().size() );
        assertEquals( 4, rbacManager.getAllRoles().size() );
    }

    public void testAddRemovePermanentPermission()
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        Role adminRole = rbacManager.saveRole( getAdminRole() );
        rbacManager.saveRole( getDeveloperRole() );

        assertEquals( 2, rbacManager.getAllRoles().size() );
        assertEquals( 2, rbacManager.getAllPermissions().size() );

        Permission createUserPerm = rbacManager.createPermission( "CREATE_USER", "CREATE", "User" );
        createUserPerm.setPermanent( true );

        // perm shouldn't exist in manager (yet)
        assertEquals( 2, rbacManager.getAllPermissions().size() );

        adminRole.addPermission( createUserPerm );
        rbacManager.saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 3, rbacManager.getAllPermissions().size() );
        Permission fetched = rbacManager.getPermission( "CREATE_USER" );
        assertNotNull( fetched );

        // Attempt to remove perm now.
        try
        {
            // Use permission name technique first.
            rbacManager.removePermission( "CREATE_USER" );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use permission object technique next.
            rbacManager.removePermission( fetched );
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
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        Role adminRole = getAdminRole();
        adminRole.setPermanent( true );

        adminRole = rbacManager.saveRole( adminRole );
        Role develRole = rbacManager.saveRole( getDeveloperRole() );

        assertEquals( 2, rbacManager.getAllRoles().size() );

        Role actualAdmin = rbacManager.getRole( adminRole.getName() );
        Role actualDevel = rbacManager.getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );

        // Attempt to remove perm now.
        try
        {
            // Use role name technique first.
            rbacManager.removeRole( adminRole.getName() );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use role object technique next.
            rbacManager.removeRole( adminRole );
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
        assertNotNull( rbacManager );

        Role adminRole = rbacManager.saveRole( getAdminRole() );
        rbacManager.saveRole( getDeveloperRole() );

        assertEquals( 2, rbacManager.getAllRoles().size() );
        assertEquals( 2, rbacManager.getAllPermissions().size() );

        Permission createUserPerm = rbacManager.createPermission( "CREATE_USER", "CREATE", "User" );
        createUserPerm.getOperation().setPermanent( true );

        // perm shouldn't exist in manager (yet)
        assertEquals( 2, rbacManager.getAllPermissions().size() );
        assertEquals( 1, rbacManager.getAllOperations().size() );

        adminRole.addPermission( createUserPerm );
        rbacManager.saveRole( adminRole );

        // perm should exist in manager now.
        assertEquals( 2, rbacManager.getAllOperations().size() );
        Operation fetched = rbacManager.getOperation( "CREATE" );
        assertNotNull( fetched );

        // Attempt to remove operation now.
        try
        {
            // Use operation name technique first.
            rbacManager.removeOperation( "CREATE" );
        }
        catch ( RbacPermanentException e )
        {
            // expected path.
        }

        try
        {
            // Use operation object technique next.
            rbacManager.removeOperation( fetched );
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

    private static final int ITERATIONS = 10000;

    private static final int ONESECOND = 1000;

    public void assertPerformance( String msg, long startTime, long endTime, int iterations, double threshold )
    {
        long elapsed = endTime - startTime;
        double ratio = (double) elapsed / (double) ONESECOND; // ratio of time to 1 second.
        double opsPerSecond = (double) iterations / ratio;

        System.out.println( "Performance " + msg + ": " + opsPerSecond + " operations per second. (effective)" );

        if ( opsPerSecond < threshold )
        {
            // Failure

            StringBuffer stats = new StringBuffer();

            stats.append( "Stats on " ).append( msg );
            stats.append( "\nStart Time (ms): " ).append( Long.toString( startTime ) );
            stats.append( "\nEnd Time (ms)  : " ).append( Long.toString( endTime ) );
            stats.append( "\nElapsed (ms)   : " ).append( Long.toString( elapsed ) );
            stats.append( "\nRatio          : " ).append( Double.toString( ratio ) );
            stats.append( "\nOps per second : " ).append( Double.toString( opsPerSecond ) );

            System.out.println( stats.toString() );

            fail( "Performance Error: " + msg + " expecting greater than [" + threshold + "], actual [" + opsPerSecond +
                "]" );
        }
    }

    public void xtestPerformanceResource()
        throws RbacManagerException
    {
        assertNotNull( rbacManager );

        Resource resource = rbacManager.createResource( "foo" );
        Resource resource2 = rbacManager.createResource( "bar" );

        assertNotNull( resource );

        Resource added = rbacManager.saveResource( resource );
        assertNotNull( added );
        Resource added2 = rbacManager.saveResource( resource2 );
        assertNotNull( added2 );

        assertEquals( 2, rbacManager.getAllResources().size() );

        String resFooId = resource.getIdentifier();
        String resBarId = resource2.getIdentifier();
        long startTime = System.currentTimeMillis();

        for ( int i = 0; i <= ITERATIONS; i++ )
        {
            Resource resFoo = rbacManager.getResource( resFooId );
            Resource resBar = rbacManager.getResource( resBarId );

            assertNotNull( resFoo );
            assertNotNull( resBar );

            assertEquals( "foo", resFoo.getIdentifier() );
            assertEquals( "bar", resBar.getIdentifier() );
        }

        long endTime = System.currentTimeMillis();

        assertPerformance( "Resource", startTime, endTime, ITERATIONS, 500.0 );
    }

    public void xtestPerformanceUserAssignment()
        throws RbacManagerException
    {
        RBACManager manager = rbacManager;

        Role devRole = getDeveloperRole();
        Role devPlusRole = getSuperDeveloperRole();
        devPlusRole.setChildRoleNames( Collections.singletonList( devRole.getName() ) );
        devRole = manager.saveRole( devRole );
        devPlusRole = manager.saveRole( devPlusRole );

        // Setup User / Assignment with 1 role.
        String username = "bob";
        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( "should be only one role assigned", 1, manager.getAssignedRoles( assignment.getPrincipal() )
            .size() );
        assertEquals( "should be one role left to assign", 1, manager.getUnassignedRoles( assignment.getPrincipal() )
            .size() );
        assertEquals( 2, manager.getAllRoles().size() );

        // assign the same role again to the same user
        assignment.addRoleName( devRole.getName() );
        manager.saveUserAssignment( assignment );

        // we certainly shouldn't have 2 roles here now
        assertEquals( 1, assignment.getRoleNames().size() );

        String bobId = assignment.getPrincipal();

        username = "janet";

        devPlusRole.setChildRoleNames( Collections.singletonList( devRole.getName() ) );
        devRole = manager.saveRole( devRole );
        manager.saveRole( devPlusRole );

        assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 2, manager.getAllUserAssignments().size() );
        assertEquals( "should be only one role assigned", 1, manager.getAssignedRoles( assignment.getPrincipal() )
            .size() );
        assertEquals( "should be one role left to assign", 1, manager.getUnassignedRoles( assignment.getPrincipal() )
            .size() );
        assertEquals( 2, manager.getAllRoles().size() );

        // assign the same role again to the same user
        assignment.addRoleName( devRole.getName() );
        manager.saveUserAssignment( assignment );

        // we certainly shouldn't have 2 roles here now
        assertEquals( 1, assignment.getRoleNames().size() );

        String janetId = assignment.getPrincipal();

        long startTime = System.currentTimeMillis();

        for ( int i = 0; i <= ITERATIONS; i++ )
        {
            UserAssignment uaBob = rbacManager.getUserAssignment( bobId );
            UserAssignment uaJanet = rbacManager.getUserAssignment( janetId );

            assertNotNull( uaBob );
            assertNotNull( uaJanet );

            assertEquals( "bob", uaBob.getPrincipal() );
            assertEquals( "janet", uaJanet.getPrincipal() );
        }

        long endTime = System.currentTimeMillis();
        assertPerformance( "UserAssignment", startTime, endTime, ITERATIONS, 350.0 );
    }

    public void xtestPerformanceRoles()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        String roleIdSysAdmin = "System Administrator";
        String roleIdUserAdmin = "User Administrator";

        long startTime = System.currentTimeMillis();

        for ( int i = 0; i <= ITERATIONS; i++ )
        {
            Role roleSysAdmin = rbacManager.getRole( roleIdSysAdmin );
            Role roleUserAdmin = rbacManager.getRole( roleIdUserAdmin );

            assertNotNull( roleSysAdmin );
            assertNotNull( roleUserAdmin );

            assertEquals( roleIdSysAdmin, roleSysAdmin.getName() );
            assertEquals( roleIdUserAdmin, roleUserAdmin.getName() );
        }

        long endTime = System.currentTimeMillis();

        assertPerformance( "Roles", startTime, endTime, ITERATIONS, 130 );
    }

    public void xtestPerformancePermissions()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        String permIdRunIndexer = "Run Indexer";
        String permIdAddRepo = "Add Repository";

        long startTime = System.currentTimeMillis();

        for ( int i = 0; i <= ITERATIONS; i++ )
        {
            Permission permRunIndex = rbacManager.getPermission( permIdRunIndexer );
            Permission permAddRepo = rbacManager.getPermission( permIdAddRepo );

            assertNotNull( permRunIndex );
            assertNotNull( permAddRepo );

            assertEquals( permIdRunIndexer, permRunIndex.getName() );
            assertEquals( permIdAddRepo, permAddRepo.getName() );
        }

        long endTime = System.currentTimeMillis();

        assertPerformance( "Permissions", startTime, endTime, ITERATIONS, 350 );
    }

    public void xtestPerformanceOperations()
        throws RbacManagerException
    {
        rbacDefaults.createDefaults();

        String operIdEditRepo = "edit-repository";
        String operIdDelRepo = "delete-repository";

        long startTime = System.currentTimeMillis();

        for ( int i = 0; i <= ITERATIONS; i++ )
        {
            Operation operEditRepo = rbacManager.getOperation( operIdEditRepo );
            Operation operDelRepo = rbacManager.getOperation( operIdDelRepo );

            assertNotNull( operEditRepo );
            assertNotNull( operDelRepo );

            assertEquals( operIdEditRepo, operEditRepo.getName() );
            assertEquals( operIdDelRepo, operDelRepo.getName() );
        }

        long endTime = System.currentTimeMillis();

        assertPerformance( "Operations", startTime, endTime, ITERATIONS, 500 );
    }

    public void testUserPermissionCache()
        throws Exception
    {
        RBACManager manager = rbacManager;
        Cache userPermCache = rbacManager.getCache( RBACManager.USER_PERMISSION_CACHE );

        assertNotNull( userPermCache );

        Role adminRole = rbacManager.saveRole( getAdminRole() );
        Role devRole = rbacManager.saveRole( getDeveloperRole() );

        // Setup User / Assignment with 1 role.
        String username = "bob";
        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRoleName( devRole );
        assignment = manager.saveUserAssignment( assignment );

        assertNull( "user perm cache for bob should be null", userPermCache.get( username ) );

        Map permMap = manager.getAssignedPermissionMap( username );

        assertNotNull( "user perm cache should not be null", userPermCache.get( username ) );

        assertNotNull( "permission map should not be null", permMap );

        assertEquals( "permission map should have one operation", 1, permMap.keySet().size() );

        List permissionList = (ArrayList)permMap.get( "EDIT" );

        assertNotNull( "permission list should not be null", permissionList );

        assertEquals( "permission list for operation should have 1 entry", 1, permissionList.size() );

        assignment.addRoleName( adminRole.getName() );
        assignment = manager.saveUserAssignment( assignment );

        assertNull( "user perm cache for bob should be null after adding new role", userPermCache.get( username ) );

        permMap = manager.getAssignedPermissionMap( username );

        assertEquals( "permission map should have one operation still", 1, permMap.keySet().size() );

        permissionList = (ArrayList)permMap.get( "EDIT" );

        assertEquals( "we should have two permission now", 2, permissionList.size() );

    }
}
