package org.codehaus.plexus.security.acegi;

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
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;

import java.util.HashMap;
import java.util.Map;

/**
 * TestAcegiAuthenticationStore:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class TestAcegiAuthenticator
    extends PlexusTestCase
{

    public void testLookup()
        throws Exception
    {
        Authenticator component = (Authenticator) lookup( Authenticator.ROLE, "acegi" );
        assertNotNull( component );
    }

    /**
     * 
     */
    public void testUsernamePasswordAuthentication()
        throws Exception
    {

        Map tokens = new HashMap();
        tokens.put( "authTokenType", "org.acegisecurity.providers.UsernamePasswordAuthenticationToken" );
        tokens.put( "username", "foo" );
        tokens.put( "password", "bar" );

        // test case of valid credentials 
        AcegiAuthenticationDataSource ds = new AcegiAuthenticationDataSource( "foo", "bar", tokens );
        Authenticator authenticator = (Authenticator) lookup( Authenticator.ROLE, "acegi" );
        try
        {
            AuthenticationResult result = authenticator.authenticate( ds );
            assertTrue( result.isAuthenticated() );
        }
        catch ( AuthenticationException e )
        {
            fail( "Unexpected Exception!" );
        }

        // test case of invalid credentials
        ds = new AcegiAuthenticationDataSource( "kill", "bill", tokens );
        try
        {
            AuthenticationResult result = authenticator.authenticate( ds );
            assertFalse( result.isAuthenticated() );
        }
        catch ( AuthenticationException e )
        {
            fail( "Unexpected Exception!" );
        }

    }

}
