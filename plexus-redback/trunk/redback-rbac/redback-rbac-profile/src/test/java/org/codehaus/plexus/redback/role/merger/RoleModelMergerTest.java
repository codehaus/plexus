package org.codehaus.plexus.redback.role.merger;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;

/**
 * RoleModelMergerTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public class RoleModelMergerTest
    extends PlexusTestCase
{

    /**
     * Creates a new RbacStore which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }
    
    public void testLoading() throws Exception 
    {
        File resource1 = new File( getBasedir() + "/src/test/merge-tests/redback-1.xml");
        File resource2 = new File( getBasedir() + "/src/test/merge-tests/redback-2.xml");
        
        assertNotNull( resource1 );
        assertNotNull( resource2 );
        
        RedbackRoleModelStaxReader modelReader = new RedbackRoleModelStaxReader();
        
        RedbackRoleModel redback1 = modelReader.read( resource1.getAbsolutePath() );
        RedbackRoleModel redback2 = modelReader.read( resource2.getAbsolutePath() );
        
        assertNotNull( redback1 );
        assertNotNull( redback2 );
        
        RoleModelMerger modelMerger = (RoleModelMerger)lookup( RoleModelMerger.ROLE, "default" );
        
        RedbackRoleModel mergedModel = modelMerger.merge( redback1, redback2 );
        
        assertNotNull( mergedModel );
        assertEquals( 2, mergedModel.getOperations().size() );
        assertEquals( 2, mergedModel.getResources().size() );
        assertEquals( 2, mergedModel.getRoles().size() );
        assertEquals( 1, mergedModel.getTemplates().size() );
    }
 
 
}