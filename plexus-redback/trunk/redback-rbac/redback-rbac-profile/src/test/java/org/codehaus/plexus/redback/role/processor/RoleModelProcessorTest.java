package org.codehaus.plexus.redback.role.processor;

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
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.role.model.io.stax.RedbackRoleModelStaxReader;

/**
 * RoleProfileTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public class RoleModelProcessorTest
    extends PlexusTestCase
{
    private RBACManager rbacManager;

    private RoleModelProcessor roleProcessor;

    /**
     * Creates a new RbacStore which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        rbacManager = (RBACManager) lookup ( RBACManager.ROLE, "memory" );

        roleProcessor = (RoleModelProcessor) lookup ( RoleModelProcessor.ROLE, "default" );
    }
    
    public void testLoading() throws Exception 
    {
        File resource = new File( getBasedir() + "/src/test/processor-tests/redback-1.xml");
        
        assertNotNull( resource );
        
        RedbackRoleModelStaxReader modelReader = new RedbackRoleModelStaxReader();
        
        RedbackRoleModel redback = modelReader.read( resource.getAbsolutePath() );
        
        assertNotNull( redback );
        
        roleProcessor.process( redback );
        
        assertTrue( rbacManager.resourceExists( "cornflakes" ) );
        
    }
 
 
}