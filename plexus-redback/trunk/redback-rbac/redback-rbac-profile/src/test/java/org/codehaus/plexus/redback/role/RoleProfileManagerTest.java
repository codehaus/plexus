package org.codehaus.plexus.redback.role;

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

import java.io.File;
import java.net.URL;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.Role;


/**
 * RoleProfileTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class RoleProfileManagerTest
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
    
    public void testLoading() throws Exception 
    {
        File resource = new File( getBasedir() + "/target/test-classes/META-INF/role-test-1/redback.xml");
        
        assertNotNull( resource );
        
        roleManager.loadRoleProfiles( resource.getAbsolutePath() );
        
        assertTrue( rbacManager.resourceExists( "*" ) );
        assertTrue( rbacManager.operationExists( "Test Operation" ) );
    }
 
 
}