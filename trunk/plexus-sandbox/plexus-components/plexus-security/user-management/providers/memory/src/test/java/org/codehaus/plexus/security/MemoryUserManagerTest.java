package org.codehaus.plexus.security;

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
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.security.user.memory.MemoryUserManager;
import org.codehaus.plexus.security.user.memory.SimpleUser;

/**
 * {@link MemoryUserManager} test:
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class MemoryUserManagerTest
    extends PlexusTestCase
{

    public void testLookup()
        throws Exception
    {
        UserManager component = (UserManager) lookup( UserManager.ROLE );
        assertNotNull( component );
        assertEquals( MemoryUserManager.class.getName(), component.getClass().getName() );
        assertNotNull( component.getUserSecurityPolicy() );
    }

    public void testCreateUser()
        throws Exception
    {
        UserManager um = (UserManager) lookup( UserManager.ROLE );
        User user = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        user.setPassword( "adminpass" );
        um.addUser( user );

        assertEquals( 1, um.getUsers().size() );
    }

    public void testAddUser()
        throws Exception
    {
        UserManager um = (UserManager) lookup( UserManager.ROLE );
        assertNotNull( um.getUsers() );
        assertEquals( 0, um.getUsers().size() );

        SimpleUser user = new SimpleUser();
        user.setFullName( "Tommy Traddles" );
        user.setUsername( "tommy123" );
        user.setPassword( "hillybilly" );
        user.setEmail( "tommy.traddles@somedomain.com" );
        um.addUser( user );

        assertNotNull( um.getUsers() );
        assertEquals( 1, um.getUsers().size() );
    }

    public void testDeleteUser()
        throws Exception
    {
        UserManager um = (UserManager) lookup( UserManager.ROLE );
        User user = um.createUser( "admin", "Administrator", "admin@somedomain.com" );
        // If there is not password set we get an ugly NullPointer
        user.setPassword( "adminpass" );
        um.addUser( user );

        assertEquals( 1, um.getUsers().size() );

        um.deleteUser( user.getPrincipal() );
        assertEquals( 0, um.getUsers().size() );

        // attempt finding a non-existent user
        try
        {
            um.findUser( "admin" );
            fail( "Ëxpected UserNotFoundException!" );
        }
        catch ( UserNotFoundException e )
        {
            // do nothing, expected!
        }
    }

    public void testFindUser()
        throws Exception
    {
        UserManager um = (UserManager) lookup( UserManager.ROLE );

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
        assertTrue( um.getUserSecurityPolicy().getPasswordEncoder().isPasswordValid( user.getEncodedPassword(),
                                                                                     "rootpass" ) );

        // attempt finding a non-existent user
        try
        {
            um.findUser( "non-existent" );
            fail( "Ëxpected UserNotFoundException!" );
        }
        catch ( UserNotFoundException e )
        {
            // do nothing, expected!
        }
    }

    public void testUpdateUser()
        throws Exception
    {
        UserManager um = (UserManager) lookup( UserManager.ROLE );

        // create and add a user
        User u1 = um.createUser( "root", "Root User", "root@somedomain.com" );
        u1.setPassword( "rootpass" );
        um.addUser( u1 );

        // find user
        User user = um.findUser( "root" );
        assertNotNull( user );
        assertSame( u1, user );

        user.setUsername( "superuser" );
        user.setEmail( "superuser@somedomain.com" );
        user.setPassword( "superpass" );
        user.setFullName( "Super User" );

        um.updateUser( user );

        // find updated user
        user = um.findUser( "superuser" );
        assertNotNull( user );
        assertEquals( "superuser", user.getUsername() );
        assertEquals( "superuser@somedomain.com", user.getEmail() );
        assertEquals( "Super User", user.getFullName() );
        assertTrue( um.getUserSecurityPolicy().getPasswordEncoder().isPasswordValid( user.getEncodedPassword(),
                                                                                     "superpass" ) );
    }
}
