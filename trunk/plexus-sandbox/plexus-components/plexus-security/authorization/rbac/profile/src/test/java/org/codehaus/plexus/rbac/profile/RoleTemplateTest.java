package org.codehaus.plexus.rbac.profile;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.Role;
/*
 * Copyright 2005 The Codehaus.
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

/**
 * RoleTemplateTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class RoleTemplateTest
    extends PlexusTestCase
{
    private RBACManager rbacManager;

    private RoleProfileManager roleManager;

    /**
     * Creates a new RbacStore which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        rbacManager = (RBACManager) lookup ( RBACManager.ROLE, "memory" );

        roleManager = (RoleProfileManager) lookup ( RoleProfileManager.ROLE, "default" );
    }

    public void testNothing()
    {
        assertTrue( true );
    }

    public void testRoleTemplate()
        throws Exception
    {
        Role bogusRole = roleManager.getRole( "bogus" );

        System.out.println( "test - >" + bogusRole.getName() );

        System.out.println( "rbacmanager1 " + rbacManager.toString());
        assertEquals( 1, rbacManager.getAllRoles().size() );

        assertTrue( rbacManager.roleExists( bogusRole.getName() ) );

    }

    public void testDynamicRoleTemplate()
        throws Exception
    {
        Role bogusRole = roleManager.getDynamicRole( "bogus", "one" );
System.out.println( "rbacmanager2 " + rbacManager.toString());
        assertTrue( rbacManager.roleExists( bogusRole.getName() ) );

        Role bogusRole2 = roleManager.getDynamicRole( "bogus", "two" );

        assertTrue( rbacManager.roleExists( bogusRole2.getName() ) );
    } 
}