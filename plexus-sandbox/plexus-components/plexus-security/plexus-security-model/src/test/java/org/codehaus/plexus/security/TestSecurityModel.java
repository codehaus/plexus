package org.codehaus.plexus.security.rbac;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.model.io.xpp3.PlexusSecurityModelXpp3Reader;
import org.codehaus.plexus.security.model.Security;
import org.codehaus.plexus.security.model.Role;
import org.codehaus.plexus.security.model.Permission;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TestSecurityModel:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class TestSecurityModel
    extends PlexusTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }


    public void testSimpleModel()
    {
        try
        {

            FileReader freader =
                new FileReader( new File( getBasedir(), "/target/test-classes/security-model-test/security-model-test.xml" ) );

            PlexusSecurityModelXpp3Reader reader = new PlexusSecurityModelXpp3Reader();

            Security securityModel = reader.read( freader );

            List roleList = securityModel.getRoles();

            Role role = (Role) roleList.get( 0 );

            assertTrue( "roleA".equals( role.getId() ) );

            List permissions = new ArrayList( role.getPermissions() );
            Permission permission = (Permission) permissions.get( 0 );

            assertTrue( "permA".equals( permission.getId() ) );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
