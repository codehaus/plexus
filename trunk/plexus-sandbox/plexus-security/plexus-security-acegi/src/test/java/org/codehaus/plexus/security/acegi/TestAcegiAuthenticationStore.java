package org.codehaus.plexus.security.acegi;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.PlexusSecurityRealm;

import java.util.Map;
import java.util.HashMap;
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
 * TestAcegiAuthenticationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class TestAcegiAuthenticationStore
    extends PlexusTestCase
{

    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }


    /**
     * 
     */
    public void testUsernamePasswordAuthentication()
    {
        try
        {
            Map tokens = new HashMap();
            tokens.put( "authTokenType", "org.acegisecurity.providers.UsernamePasswordAuthenticationToken" );
            tokens.put( "username", "foo" );
            tokens.put( "password", "bar" );
            PlexusSecurityRealm psr = (PlexusSecurityRealm)lookup( "org.codehaus.plexus.security.PlexusSecurityRealm");

            assertTrue( psr.isAuthenticated( tokens ) );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

}
