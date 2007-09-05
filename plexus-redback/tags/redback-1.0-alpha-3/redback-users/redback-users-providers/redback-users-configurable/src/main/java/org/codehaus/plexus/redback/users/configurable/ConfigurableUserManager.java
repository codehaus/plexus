package org.codehaus.plexus.redback.users.configurable;

/*
 * Copyright 2001-2007 The Apache Software Foundation.
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

import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.redback.configuration.UserConfiguration;
import org.codehaus.plexus.redback.users.AbstractUserManager;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.users.UserQuery;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id:$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.users.UserManager" role-hint="configurable"
 */
public class ConfigurableUserManager extends AbstractUserManager implements Initializable
{
    /**
     * @plexus.requirement
     */
    private UserConfiguration config;
    
    /**
     * @plexus.requirement role-hint="default"
     */
    private PlexusContainer container;
    
    private UserManager userManagerImpl;  
    
    public static final String USER_MANAGER_IMPL = "user.manager.impl"; 
    
    public void initialize() throws InitializationException
    {
       String userManagerRole = config.getString( USER_MANAGER_IMPL );
       
       if ( userManagerRole == null )
       {
           throw new InitializationException( "User Manager Configuration Missing: " + USER_MANAGER_IMPL + " configuration property"  );
       }
       
       try
       {
           userManagerImpl = (UserManager)container.lookup( UserManager.class.getName(), userManagerRole );
       }
       catch ( ComponentLookupException e )
       {
           throw new InitializationException( "unable to resolve user manager implementation", e );
       }
    }
 
    public User addUser( User user )
    {
        return userManagerImpl.addUser( user );
    }

    public void addUserUnchecked( User user )
    {
        userManagerImpl.addUserUnchecked( user );
    }

    public User createUser( String username, String fullName, String emailAddress )
    {
        return userManagerImpl.createUser( username, fullName, emailAddress );
    }

    public UserQuery createUserQuery()
    {
        return userManagerImpl.createUserQuery();
    }

    public void deleteUser( Object principal ) throws UserNotFoundException
    {
        userManagerImpl.deleteUser( principal );
    }

    public void deleteUser( String username ) throws UserNotFoundException
    {
        userManagerImpl.deleteUser( username );
    }

    public void eraseDatabase()
    {
        userManagerImpl.eraseDatabase();
    }

    public User findUser( String username ) throws UserNotFoundException
    {
        return userManagerImpl.findUser( username );
    }

    public User findUser( Object principal ) throws UserNotFoundException
    {
        return userManagerImpl.findUser( principal );
    }

    public List findUsersByEmailKey( String emailKey, boolean orderAscending )
    {
        return userManagerImpl.findUsersByEmailKey( emailKey, orderAscending );
    }

    public List findUsersByFullNameKey( String fullNameKey, boolean orderAscending )
    {
        return userManagerImpl.findUsersByFullNameKey( fullNameKey, orderAscending );
    }

    public List findUsersByQuery( UserQuery query )
    {
        return userManagerImpl.findUsersByQuery( query );
    }

    public List findUsersByUsernameKey( String usernameKey, boolean orderAscending )
    {
        return userManagerImpl.findUsersByUsernameKey( usernameKey, orderAscending );
    }

    public String getId()
    {
        return ConfigurableUserManager.class.getName() + " wrapping " + userManagerImpl.getId();
    }

    public List getUsers()
    {
        return userManagerImpl.getUsers();
    }

    public List getUsers( boolean orderAscending )
    {
        return userManagerImpl.getUsers( orderAscending );
    }

    public boolean isReadOnly()
    {
        return userManagerImpl.isReadOnly();
    }

    public User updateUser( User user ) throws UserNotFoundException
    {
        return userManagerImpl.updateUser( user );
    }

    public boolean userExists( Object principal )
    {
        return userManagerImpl.userExists( principal );
    }
}
