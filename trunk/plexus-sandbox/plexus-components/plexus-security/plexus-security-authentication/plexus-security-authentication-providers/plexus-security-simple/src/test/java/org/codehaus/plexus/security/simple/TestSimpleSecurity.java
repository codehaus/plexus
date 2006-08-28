package org.codehaus.plexus.security.simple;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.PlexusSecurityRealm;
import org.codehaus.plexus.security.PlexusSecuritySession;

import java.util.HashMap;
import java.util.Map;
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
 * TestSimpleSecurity:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class TestSimpleSecurity
    extends PlexusTestCase
{

    public void setUp()
        throws Exception
    {

        super.setUp();
    }

    public void tearDown()
        throws Exception
    {
        super.tearDown();
    }


    public void testSimpleAuthentication()
        throws Exception
    {
        PlexusSecurityRealm psr = (PlexusSecurityRealm) lookup( PlexusSecurityRealm.ROLE );

        assertTrue( psr != null );

        Map authenticationCreds = new HashMap();

        authenticationCreds.put("username", "test");

        authenticationCreds.put("password", "password");

        assertTrue( psr.isAuthenticated( authenticationCreds ) );

        PlexusSecuritySession session = psr.authenticate( authenticationCreds );

        assertTrue( session.isAuthentic() );

        assertTrue( psr.isAuthorized( session , authenticationCreds ) );
    }

}
