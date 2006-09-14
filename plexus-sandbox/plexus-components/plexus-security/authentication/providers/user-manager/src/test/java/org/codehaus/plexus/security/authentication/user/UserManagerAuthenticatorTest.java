package org.codehaus.plexus.security.authentication.user;

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
import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * Tests for {@link UserManagerAuthenticator} implementation.
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class UserManagerAuthenticatorTest
    extends PlexusTestCase
{
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy userSecurityPolicy;
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        userSecurityPolicy = (UserSecurityPolicy) lookup( UserSecurityPolicy.ROLE );
        userSecurityPolicy.setEnabled( false );
    }

    public void testLookup()
        throws Exception
    {
        Authenticator component = (Authenticator) lookup( Authenticator.ROLE, "user-manager" );
        assertNotNull( component );
        assertEquals( UserManagerAuthenticator.class.getName(), component.getClass().getName() );
    }

    public void testAuthenticate()
        throws Exception
    {
        // Set up a few users for the Authenticator
        
        UserManager um = (UserManager) lookup( UserManager.ROLE, "memory" );
        User user = um.createUser( "test", "Test User", "testuser@somedomain.com" );
        user.setPassword( "testpass" );
        um.addUser( user );

        user = um.createUser( "guest", "Guest User", "testuser@somedomain.com" );
        user.setPassword( "guestpass" );
        um.addUser( user );

        user = um.createUser( "anonymous", "Anonymous User", "testuser@somedomain.com" );
        user.setPassword( "nopass" );
        um.addUser( user );

        // test with valid credentials
        Authenticator auth = (Authenticator) lookup( Authenticator.ROLE, "user-manager" );
        assertNotNull( auth );

        AuthenticationResult result = auth.authenticate( new AuthenticationDataSource( "anonymous", "nopass" ) );
        assertTrue( result.isAuthenticated() );

        // test with invalid password
        result = auth.authenticate( new AuthenticationDataSource( "anonymous", "wrongpass" ) );
        assertFalse( result.isAuthenticated() );
        assertNull( result.getException() );

        // test with unknown user
        result = auth.authenticate( new AuthenticationDataSource( "unknownuser", "wrongpass" ) );
        assertFalse( result.isAuthenticated() );
        assertNotNull( result.getException() );
        assertEquals( result.getException().getClass().getName(), UserNotFoundException.class.getName() );
    }
}
