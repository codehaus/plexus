package org.codehaus.plexus.security.authentication.memory;

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
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.authentication.AuthenticationResult;

/**
 * MemoryAuthenticatorTest:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class MemoryAuthenticatorTest
    extends PlexusTestCase
{
    public void testSimpleAuthentication()
        throws Exception
    {
        Authenticator authenticator = (Authenticator) lookup( Authenticator.ROLE, "memory" );

        assertNotNull( authenticator );

        MemoryAuthenticationDataSource ds = new MemoryAuthenticationDataSource( "jason", "kungfu" );

        AuthenticationResult ar = authenticator.authenticate( ds );

        assertTrue( ar.isAuthenticated() );

        assertEquals( "jason", ar.getPrincipal() );
    }
}
