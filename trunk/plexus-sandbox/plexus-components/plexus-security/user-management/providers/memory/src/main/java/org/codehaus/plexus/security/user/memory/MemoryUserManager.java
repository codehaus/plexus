package org.codehaus.plexus.security.user.memory;

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

import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.security.user.UserManager"
 *   role-hint="memory"
 */
public class MemoryUserManager
    implements UserManager
{
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy userSecurityPolicy;

    private Map users = new HashMap();

    public User addUser( User user )
    {
        users.put( user.getPrincipal(), user );

        // If there exists no encoded password, then this is a new user setup 
        if ( StringUtils.isEmpty( user.getEncodedPassword() ) )
        {
            userSecurityPolicy.changeUserPassword( user );
        }

        return user;
    }

    public User updateUser( User user )
    {
        // If password is supplied, assume changing of password.
        // TODO: Consider adding a boolean to the updateUser indicating a password change or not.
        if ( StringUtils.isNotEmpty( user.getPassword() ) )
        {
            userSecurityPolicy.changeUserPassword( user );
        }

        return addUser( user );
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        User user = (User) users.get( principal );

        if ( user == null )
        {
            throw new UserNotFoundException( "Cannot find the user with the principal '" + principal + "'." );
        }

        return user;
    }

    public boolean userExists( Object principal )
    {
        try
        {
            findUser( principal );
            return true;
        }
        catch ( UserNotFoundException ne )
        {
            return false;
        }
    }

    public void deleteUser( Object principal )
    {
        users.remove( principal );
    }

    public User createUser( String username, String fullName, String emailAddress )
    {
        User user = new SimpleUser();
        user.setUsername( username );
        user.setFullName( fullName );
        user.setEmail( emailAddress );

        return user;
    }

    public void deleteUser( String username )
        throws UserNotFoundException
    {
        User user = findUser( username );
        deleteUser( user.getPrincipal() );
    }

    public User findUser( String username )
        throws UserNotFoundException
    {
        User user = null;

        Iterator it = users.values().iterator();
        while ( it.hasNext() )
        {
            User u = (User) it.next();
            if ( u.getUsername().equals( username ) )
            {
                user = u;
            }
        }

        if ( user == null )
        {
            throw new UserNotFoundException( "Unable to find user '" + username + "'" );
        }

        return user;
    }

    public List getUsers()
    {
        return new ArrayList( users.values() );
    }
}
