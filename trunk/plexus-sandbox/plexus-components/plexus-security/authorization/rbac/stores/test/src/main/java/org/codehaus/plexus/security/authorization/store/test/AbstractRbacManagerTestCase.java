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
import org.codehaus.plexus.security.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.util.Collection;
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

    public RBACManager getRbacManager()
    {
        return rbacManager;
    }

    public void setRbacManager( RBACManager store )
    {
        this.rbacManager = store;
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

    private Role getAdminRole()
    {
        Role role = getRbacManager().createRole( "ADMIN" );
        role.setAssignable( false );

        Permission perm = getRbacManager().createPermission( "EDIT_ANY_USER", "EDIT", "User:*" );

        role.addPermission( perm );

        return role;
    }

    private Role getDeveloperRole()
    {
        Role role = getRbacManager().createRole( "DEVELOPER" );
        role.setAssignable( true );

        Permission perm = getRbacManager().createPermission( "EDIT_MY_USER", "EDIT", "User:Self" );

        role.addPermission( perm );

        return role;
    }
    
    private Role getProjectAdminRole()
    {
        Role role = getRbacManager().createRole( "PROJECT_ADMIN" );
        role.setAssignable( true );

        Permission perm = getRbacManager().createPermission( "EDIT_PROJECT", "EDIT", "Project:Foo" );

        role.addPermission( perm );

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
    }
    
    public void testAddRemovePermission() throws RbacObjectInvalidException, RbacStoreException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );
        
        Role adminRole = getRbacManager().saveRole(getAdminRole());
        getRbacManager().saveRole(getDeveloperRole());

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
    }
    
    public void testAddGetRole() throws RbacStoreException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        Role develRole = getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );

        Role actualAdmin = getRbacManager().getRole( adminRole.getName() );
        Role actualDevel = getRbacManager().getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );
    }
    
    public void testAddGetChildRole() throws RbacStoreException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        Role develRole = getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );

        Role actualAdmin = getRbacManager().getRole( adminRole.getName() );
        Role actualDevel = getRbacManager().getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );
        
        // Now do a child role.
        
        develRole.addChildRole( getProjectAdminRole() );
        
        getRbacManager().saveRole( develRole );
        
        assertEquals( 3, getRbacManager().getAllRoles().size() );
    }
    
    public void testAddGetChildRoleViaName() throws RbacStoreException, RbacObjectNotFoundException
    {
        assertNotNull( getRbacManager() );

        Role adminRole = getRbacManager().saveRole( getAdminRole() );
        Role develRole = getRbacManager().saveRole( getDeveloperRole() );

        assertEquals( 2, getRbacManager().getAllRoles().size() );

        Role actualAdmin = getRbacManager().getRole( adminRole.getName() );
        Role actualDevel = getRbacManager().getRole( develRole.getName() );

        assertEquals( adminRole, actualAdmin );
        assertEquals( develRole, actualDevel );
        
        // Now do a child role.
        Role projectRole = getProjectAdminRole();
        String projectRoleName = projectRole.getName();
        getRbacManager().saveRole( projectRole );
        
        develRole.addChildRole( getRbacManager().getRole( projectRoleName ) );
        
        getRbacManager().saveRole( develRole );
        
        assertEquals( 3, getRbacManager().getAllRoles().size() );
    }
    
    public void testUserAssignmentAddRole() throws RbacStoreException, RbacObjectNotFoundException
    {
        Role adminRole = getRbacManager().saveRole(getAdminRole());
    
        assertEquals( 1, getRbacManager().getAllRoles().size() );
        
        String adminPrincipal = "admin";
        
        UserAssignment assignment = getRbacManager().createUserAssignment( adminPrincipal );
        
        assignment.addRole( adminRole );

        getRbacManager().saveUserAssignment( assignment );
        
        assertEquals( 1, getRbacManager().getAllUserAssignments().size() );
        assertEquals( 1, getRbacManager().getAllRoles().size() );
        
        UserAssignment ua = getRbacManager().getUserAssignment( adminPrincipal );
        assertNotNull( ua );
        
        Role fetched = (Role) getRbacManager().getRole( "ADMIN" );
        assertNotNull( fetched );
    }

    public void testGetAssignedPermissionsNoChildRoles()
        throws RbacStoreException, RbacObjectNotFoundException
    {
        Role admin = getAdminRole();

        admin = getRbacManager().saveRole( admin );

        assertEquals( 1, getRbacManager().getAllRoles().size() );

        String adminPrincipal = "admin";

        UserAssignment ua = getRbacManager().createUserAssignment( adminPrincipal );

        ua.addRole( admin );

        getRbacManager().saveUserAssignment( ua );

        assertEquals( 1, getRbacManager().getAllUserAssignments().size() );

        Set assignedPermissions = getRbacManager().getAssignedPermissions( adminPrincipal );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
    }

    public void testGlobalResource()
    {
        Permission editConfiguration = getRbacManager().createPermission( "Edit Configuration" );
        editConfiguration.setOperation( getRbacManager().createOperation( "edit-configuration" ) );
        editConfiguration.setResource( getRbacManager().getGlobalResource() );
        getRbacManager().savePermission( editConfiguration );
        
        assertEquals( 1, getRbacManager().getAllPermissions().size() );
        assertEquals( 1, getRbacManager().getAllOperations().size() );
        assertEquals( 1, getRbacManager().getAllResources().size() );
        
        Permission deleteConfiguration = getRbacManager().createPermission( "Delete Configuration" );
        deleteConfiguration.setOperation( getRbacManager().createOperation( "delete-configuration" ) );
        deleteConfiguration.setResource( getRbacManager().getGlobalResource() );
        getRbacManager().savePermission( deleteConfiguration );
        
        assertEquals( 2, getRbacManager().getAllPermissions().size() );
        assertEquals( 2, getRbacManager().getAllOperations().size() );
        assertEquals( 1, getRbacManager().getAllResources().size() );
    }
    
    public void testGlobalResourceOneLiner()
    {
        getRbacManager().savePermission( getRbacManager().createPermission( "Edit Configuration", "edit-configuration", Resource.GLOBAL ) );
        getRbacManager().savePermission( getRbacManager().createPermission( "Delete Configuration", "delete-configuration", Resource.GLOBAL ) );
    }
    
    public void testUserAssignmentAddSecondRole() throws RbacStoreException, RbacObjectNotFoundException
    {
        // Setup User / Assignment with 1 role.
        String username = "bob";
        
        UserAssignment assignment = getRbacManager().createUserAssignment( username );
        assignment.addRole( getDeveloperRole() );
        assignment = getRbacManager().saveUserAssignment( assignment );
        
        assertEquals( 1, getRbacManager().getAllUserAssignments().size() );
        assertEquals( 1, getRbacManager().getAllRoles().size() );

        // Create another role add it to manager.
        Role projectAdmin = getProjectAdminRole();
        String roleName = projectAdmin.getName();
        projectAdmin = getRbacManager().saveRole( projectAdmin );
        
        // Perform Second Role process.
        UserAssignment bob = getRbacManager().createUserAssignment( username );
        bob.addRole( getRbacManager().getRole( roleName ) );
        getRbacManager().saveUserAssignment( bob );
    }
    
    public void testGetAssignedRoles() throws RbacStoreException, RbacObjectNotFoundException
    {
        // Setup 3 roles.
        getRbacManager().saveRole( getAdminRole() );
        getRbacManager().saveRole( getProjectAdminRole() );
        Role added = getRbacManager().saveRole( getDeveloperRole() );
        String roleName = added.getName();

        assertEquals( 3, getRbacManager().getAllRoles().size() );

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = getRbacManager().createUserAssignment( username );
        assignment.addRole( getRbacManager().getRole( roleName ) );
        assignment = getRbacManager().saveUserAssignment( assignment );

        assertEquals( 1, getRbacManager().getAllUserAssignments().size() );
        assertEquals( 3, getRbacManager().getAllRoles().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedRoles = getRbacManager().getAssignedRoles( username );

        assertNotNull( assignedRoles );
        assertEquals( 1, assignedRoles.size() );
    }
    
    public void testGetAssignedPermissions() throws RbacStoreException, RbacObjectNotFoundException
    {
        // Setup 3 roles.
        getRbacManager().saveRole( getAdminRole() );
        getRbacManager().saveRole( getProjectAdminRole() );
        Role added = getRbacManager().saveRole( getDeveloperRole() );
        String roleName = added.getName();

        assertEquals( 3, getRbacManager().getAllRoles().size() );
        assertEquals( 3, getRbacManager().getAllPermissions().size() );

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = getRbacManager().createUserAssignment( username );
        assignment.addRole( getRbacManager().getRole( roleName ) );
        assignment = getRbacManager().saveUserAssignment( assignment );

        assertEquals( 1, getRbacManager().getAllUserAssignments().size() );
        assertEquals( 3, getRbacManager().getAllRoles().size() );
        assertEquals( 3, getRbacManager().getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = getRbacManager().getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
    }
    
    public void testGetRolesDeep()
        throws RbacStoreException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRole( manager.getRole( "Developer" ) );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 4, manager.getAllRoles().size() );
        assertEquals( 6, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Role devel = manager.getRole( "Developer" );
        assertNotNull( devel );

        // First Depth.
        List childDevel = devel.getChildRoles();
        assertNotNull( childDevel );
        assertEquals( 1, childDevel.size() );
        Role trusted = (Role) childDevel.get( 0 );
        assertNotNull( trusted );
        assertEquals( "Trusted Developer", trusted.getName() );

        // Second Depth.
        List childTrusted = trusted.getChildRoles();
        assertNotNull( childTrusted );
        assertEquals( 1, childTrusted.size() );
        Role sysAdmin = (Role) childTrusted.get( 0 );
        assertNotNull( sysAdmin );
        assertEquals( "System Administrator", sysAdmin.getName() );

        // Third Depth.
        List childSysAdmin = sysAdmin.getChildRoles();
        assertNotNull( childSysAdmin );
        assertEquals( 1, childSysAdmin.size() );
        Role userAdmin = (Role) childSysAdmin.get( 0 );
        assertNotNull( userAdmin );
        assertEquals( "User Administrator", userAdmin.getName() );

    }
    
    public void testGetAssignedPermissionsDeep() throws RbacStoreException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();

        // Setup User / Assignment with 1 role.
        String username = "bob";

        UserAssignment assignment = manager.createUserAssignment( username );
        assignment.addRole( manager.getRole( "Developer" ) );
        assignment = manager.saveUserAssignment( assignment );

        assertEquals( 1, manager.getAllUserAssignments().size() );
        assertEquals( 4, manager.getAllRoles().size() );
        assertEquals( 6, manager.getAllPermissions().size() );

        // Get the List of Assigned Roles for user bob.
        Collection assignedPermissions = manager.getAssignedPermissions( username );

        assertNotNull( assignedPermissions );
        assertEquals( 1, assignedPermissions.size() );
    }
    
    public void testLargeApplicationInit() throws RbacStoreException, RbacObjectNotFoundException
    {
        RBACManager manager = getArchivaDefaults();
        
        assertEquals( 6, manager.getAllPermissions().size() );
        assertEquals( 11, manager.getAllOperations().size() );
        assertEquals( 4, manager.getAllRoles().size() );
    }
    
    private RBACManager getArchivaDefaults()
        throws RbacObjectNotFoundException
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
            admin.addChildRole( manager.getRole( "User Administrator" ) );
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
            developer.addChildRole( manager.getRole( "System Administrator" ) );
            developer.addPermission( manager.getPermission( "Run Indexer" ) );
            developer.setAssignable( true );
            manager.saveRole( developer );
        }
        
        if ( !manager.roleExists( "Developer" ) )
        {
            Role developer = manager.createRole( "Developer" );
            developer.addChildRole( manager.getRole( "Trusted Developer" ) );
            developer.addPermission( manager.getPermission( "Run Indexer" ) );
            developer.setAssignable( true );
            manager.saveRole( developer );
        }
        
        return manager;
    }
}
