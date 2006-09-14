package org.codehaus.plexus.security.user.provider.test;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * AbstractUserManagerTestCase 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractUserManagerTestCase
    extends PlexusTestCase
{
    /**
     * This value is set by the sub classes of this test case.
     * They should override .setUp() and inject this value via
     * the {@link #setUserManager(UserManager)} method call.
     */
    private UserManager userManager;

    private UserSecurityPolicy securityPolicy;

    public UserManager getUserManager()
    {
        return userManager;
    }

    public void setUserManager( UserManager um )
    {
        this.userManager = um;
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        securityPolicy = (UserSecurityPolicy) lookup( UserSecurityPolicy.ROLE );
    }

    protected void tearDown()
        throws Exception
    {
        if ( getUserManager() != null )
        {
            release( getUserManager() );
        }
        super.tearDown();
    }

    private void assertCleanUserManager()
    {
        assertNotNull( getUserManager() );

        assertEquals( "New UserManager should contain no users.", 0, userManager.getUsers().size() );
    }

    public void testAddFindUserByPrincipal()
        throws UserNotFoundException
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        User smcqueen = getUserManager().createUser( "smcqueen", "Steve McQueen", "the cooler king" );

        /* Keep a reference to the object that was added.
         * Since it has the actual principal that was managed by jpox/jdo.
         */
        User added = userManager.addUser( smcqueen );

        assertEquals( 1, userManager.getUsers().size() );

        /* Fetch user from userManager using principal returned earlier */
        User actual = userManager.findUser( added.getPrincipal() );
        assertEquals( added, actual );
    }

    public void testAddFindUserByUsername()
        throws UserNotFoundException
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        User smcqueen = getUserManager().createUser( "smcqueen", "Steve McQueen", "the cooler king" );

        User added = userManager.addUser( smcqueen );

        assertEquals( 1, userManager.getUsers().size() );

        User actual = userManager.findUser( "smcqueen" );
        assertEquals( added, actual );
    }

    public void testCreateUser()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();
        User user = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        user.setPassword( "adminpass" );
        um.addUser( user );

        assertEquals( 1, um.getUsers().size() );
    }

    public void testAddUser()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();
        assertNotNull( um.getUsers() );
        assertEquals( 0, um.getUsers().size() );

        User user = um.createUser( "tommy123", "Tommy Traddles", "tommy.traddles@somedomain.com" );
        user.setPassword( "hillybilly" );
        um.addUser( user );

        assertNotNull( um.getUsers() );
        assertEquals( 1, um.getUsers().size() );
    }

    public void testDeleteUser()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();
        User user = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        user.setPassword( "adminpass" );
        um.addUser( user );

        assertEquals( 1, um.getUsers().size() );

        um.deleteUser( user.getPrincipal() );
        assertEquals( 0, um.getUsers().size() );

        // attempt finding a non-existent user
        try
        {
            um.findUser( "admin" );
            fail( "Expected UserNotFoundException!" );
        }
        catch ( UserNotFoundException e )
        {
            // do nothing, expected!
        }
    }

    public void testFindUser()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();

        // create and add a few users
        User u1 = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        u1.setPassword( "adminpass" );
        um.addUser( u1 );

        u1 = um.createUser( "administrator", "Administrator User", "administrator@somedomain.com" );
        u1.setPassword( "password" );
        um.addUser( u1 );

        u1 = um.createUser( "root", "Root User", "root@somedomain.com" );
        u1.setPassword( "rootpass" );
        um.addUser( u1 );

        assertEquals( 3, um.getUsers().size() );

        // find an existing user
        User user = um.findUser( "root" );
        assertNotNull( user );
        assertEquals( "root@somedomain.com", user.getEmail() );
        assertEquals( "root", user.getPrincipal() );
        assertEquals( "Root User", user.getFullName() );
        // test if the plain string password is encoded and NULL'ified
        assertNull( user.getPassword() );
        // test if encoded password was as expected 
        assertTrue( securityPolicy.getPasswordEncoder().isPasswordValid( user.getEncodedPassword(), "rootpass" ) );

        // attempt finding a non-existent user
        try
        {
            um.findUser( "non-existent" );
            fail( "Expected UserNotFoundException!" );
        }
        catch ( UserNotFoundException e )
        {
            // do nothing, expected!
        }
    }

    public void testUserExists()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();

        // create and add a few users
        User u1 = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        u1.setPassword( "adminpass" );
        um.addUser( u1 );

        assertTrue( um.userExists( "admin" ) );
        assertFalse( um.userExists( "voodoohatrack" ) );

    }

    public void testUpdateUser()
        throws Exception
    {
        assertCleanUserManager();
        securityPolicy.setEnabled( false );

        UserManager um = getUserManager();

        // create and add a user
        User u1 = um.createUser( "root", "Root User", "root@somedomain.com" );
        u1.setPassword( "rootpass" );
        u1 = um.addUser( u1 );

        // find user
        User user = um.findUser( "root" );
        assertNotNull( user );
        assertEquals( u1, user );

        user.setEmail( "superuser@somedomain.com" );
        user.setPassword( "superpass" );
        user.setFullName( "Super User" );

        um.updateUser( user );

        // find updated user
        user = um.findUser( "root" );
        assertNotNull( user );
        assertEquals( "superuser@somedomain.com", user.getEmail() );
        assertEquals( "Super User", user.getFullName() );
        assertTrue( securityPolicy.getPasswordEncoder().isPasswordValid( user.getEncodedPassword(), "superpass" ) );
    }
}
