package org.codehaus.plexus.redback.user.memory;

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

import org.codehaus.plexus.redback.policy.UserSecurityPolicy;
import org.codehaus.plexus.redback.user.AbstractUserManager;
import org.codehaus.plexus.redback.user.PermanentUserException;
import org.codehaus.plexus.redback.user.User;
import org.codehaus.plexus.redback.user.UserManager;
import org.codehaus.plexus.redback.user.UserNotFoundException;
import org.codehaus.plexus.redback.user.memory.util.UserSorter;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @plexus.component role="org.codehaus.plexus.redback.user.UserManager"
 * role-hint="memory"
 */
public class MemoryUserManager
    extends AbstractUserManager
    implements UserManager
{
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy userSecurityPolicy;

    public String getId()
    {
        Properties props = new Properties();
        URL url = this
            .getClass()
            .getResource(
                "META-INF/maven/org.codehaus.plexus.redback/plexus-security-user-management-provider-memory/pom.properties" );

        if ( url != null )
        {
            try
            {
                props.load( url.openStream() );
                return "MemoryUserManager - " + props.getProperty( "version" );
            }
            catch ( IOException e )
            {
                // Fall thru
            }
        }
        return "MemoryUserManager - (unknown version)";
    }

    private Map users = new HashMap();

    public User addUser( User user )
    {
        saveUser( user );
        fireUserManagerUserAdded( user );

        // If there exists no encoded password, then this is a new user setup 
        if ( StringUtils.isEmpty( user.getEncodedPassword() ) )
        {
            userSecurityPolicy.extensionChangePassword( user );
        }

        return user;
    }

    private void saveUser( User user )
    {
        triggerInit();
        users.put( user.getPrincipal(), user );
    }

    public User updateUser( User user )
    {
        // If password is supplied, assume changing of password.
        // TODO: Consider adding a boolean to the updateUser indicating a password change or not.
        if ( StringUtils.isNotEmpty( user.getPassword() ) )
        {
            userSecurityPolicy.extensionChangePassword( user );
        }

        saveUser( user );

        fireUserManagerUserUpdated( user );

        return user;
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        triggerInit();
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
        throws UserNotFoundException
    {
        deleteUser( principal.toString() );
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

        if ( user.isPermanent() )
        {
            throw new PermanentUserException( "Cannot delete permanent user." );
        }

        users.remove( user.getPrincipal() );

        fireUserManagerUserRemoved( user );
    }

    public void addUserUnchecked( User user )
    {
        addUser( user );
    }

    public void eraseDatabase()
    {
        users.clear();
    }

    public User findUser( String username )
        throws UserNotFoundException
    {
        triggerInit();
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

    public List findUsersByUsernameKey( String usernameKey, boolean orderAscending )
    {
        triggerInit();

        List userList = new ArrayList();

        Iterator it = users.values().iterator();
        while ( it.hasNext() )
        {
            User u = (User) it.next();
            if ( u.getUsername().indexOf( usernameKey ) > -1 )
            {
                userList.add( u );
            }
        }

        Collections.sort( userList, new UserSorter( orderAscending ) );

        return userList;
    }

    public List findUsersByFullNameKey( String fullNameKey, boolean orderAscending )
    {
        triggerInit();

        List userList = new ArrayList();

        Iterator it = users.values().iterator();
        while ( it.hasNext() )
        {
            User u = (User) it.next();
            if ( u.getFullName().indexOf( fullNameKey ) > -1 )
            {
                userList.add( u );
            }
        }

        Collections.sort( userList, new UserSorter( orderAscending ) );

        return userList;
    }

    public List findUsersByEmailKey( String emailKey, boolean orderAscending )
    {
        triggerInit();

        List userList = new ArrayList();

        Iterator it = users.values().iterator();
        while ( it.hasNext() )
        {
            User u = (User) it.next();
            if ( u.getEmail().indexOf( emailKey ) > -1 )
            {
                userList.add( u );
            }
        }

        Collections.sort( userList, new UserSorter( orderAscending ) );

        return userList;
    }

    public List getUsers()
    {
        triggerInit();
        return new ArrayList( users.values() );
    }

    public List getUsers( boolean ascendingUsername )
    {
        return getUsers();
    }

    private boolean hasTriggeredInit = false;

    public void triggerInit()
    {
        if ( !hasTriggeredInit )
        {
            fireUserManagerInit( users.isEmpty() );
            hasTriggeredInit = true;
        }
    }
}
