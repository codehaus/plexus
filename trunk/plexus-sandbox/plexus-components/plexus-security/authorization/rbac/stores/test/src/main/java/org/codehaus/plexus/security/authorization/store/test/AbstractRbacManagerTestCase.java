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
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;

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
}
