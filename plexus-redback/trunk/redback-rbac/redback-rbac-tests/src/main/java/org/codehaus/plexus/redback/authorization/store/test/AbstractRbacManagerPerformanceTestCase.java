package org.codehaus.plexus.redback.authorization.store.test;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.redback.authorization.store.test.utils.RBACDefaults;
import org.codehaus.plexus.redback.rbac.Operation;
import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;

import java.util.Collections;

public class AbstractRbacManagerPerformanceTestCase
    extends PlexusTestCase
{
    private RBACManager rbacManager;

    private RBACDefaults rbacDefaults;

    public void setRbacManager( RBACManager store )
    {
        this.rbacManager = store;
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

    private Role getDeveloperRole()
        throws RbacManagerException
    {
        Role role = rbacManager.createRole( "DEVELOPER" );
        role.setAssignable( true );

        Permission perm = rbacManager.createPermission( "EDIT_MY_USER", "EDIT", "User:Self" );

        role.addPermission( perm );

        return role;
    }
    
    private Role getSuperDeveloperRole()
    {
        Role role = rbacManager.createRole( "SUPER_DEVELOPER" );
        role.setAssignable( true );

        return role;
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

            fail( "Performance Error: " + msg + " expecting greater than [" + threshold + "], actual [" + opsPerSecond
                + "]" );
        }
    }

    public void testPerformanceResource()
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

    public void testPerformanceUserAssignment()
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

    public void testPerformanceRoles()
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

    public void testPerformancePermissions()
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

    public void testPerformanceOperations()
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
}
