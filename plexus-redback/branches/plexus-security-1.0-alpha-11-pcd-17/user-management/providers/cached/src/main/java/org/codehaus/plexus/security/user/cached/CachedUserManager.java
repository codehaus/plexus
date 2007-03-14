package org.codehaus.plexus.security.user.cached;

/*
 * Copyright 2001-2006 The Codehaus.
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

import net.sf.ehcache.Element;

import org.codehaus.plexus.ehcache.EhcacheComponent;
import org.codehaus.plexus.ehcache.EhcacheUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserManagerListener;
import org.codehaus.plexus.security.user.UserNotFoundException;

import java.util.List;

/**
 * CachedUserManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.user.UserManager" role-hint="cached"
 */
public class CachedUserManager
    extends AbstractLogEnabled
    implements UserManager, UserManagerListener
{
    /**
     * @plexus.requirement
     */
    private UserManager userImpl;

    /**
     * Cache used for users
     * 
     * @plexus.requirement role-hint="users"
     */
    private EhcacheComponent usersCache;

    public User addUser( User user )
    {
        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
        return this.userImpl.addUser( user );
    }

    public void addUserManagerListener( UserManagerListener listener )
    {
        this.userImpl.addUserManagerListener( listener );
    }

    public void addUserUnchecked( User user )
    {
        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
        this.userImpl.addUserUnchecked( user );
    }

    public User createUser( String username, String fullName, String emailAddress )
    {
        usersCache.invalidateKey( username );
        return this.userImpl.createUser( username, fullName, emailAddress );
    }

    public void deleteUser( Object principal )
        throws UserNotFoundException
    {
        usersCache.invalidateKey( principal );
        this.userImpl.deleteUser( principal );
    }

    public void deleteUser( String username )
        throws UserNotFoundException
    {
        usersCache.invalidateKey( username );
        this.userImpl.deleteUser( username );
    }

    public void eraseDatabase()
    {
        try
        {
            this.userImpl.eraseDatabase();
        }
        finally
        {
            EhcacheUtils.clearAllCaches( getLogger() );
        }
    }

    public User findUser( String username )
        throws UserNotFoundException
    {
        Element el = usersCache.getElement( username );
        if ( el != null )
        {
            return (User) el.getObjectValue();
        }
        else
        {
            User user = this.userImpl.findUser( username );
            usersCache.putElement( new Element( username, user ) );
            return user;
        }
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        Element el = usersCache.getElement( principal );
        if ( el != null )
        {
            return (User) el.getObjectValue();
        }
        else
        {
            User user = this.userImpl.findUser( principal );
            usersCache.putElement( new Element( principal, user ) );
            return user;
        }
    }

    public List findUsersByEmailKey( String emailKey, boolean orderAscending )
    {
        getLogger().debug( "NOT CACHED - .findUsersByEmailKey(String, boolean)" );
        return this.userImpl.findUsersByEmailKey( emailKey, orderAscending );
    }

    public List findUsersByFullNameKey( String fullNameKey, boolean orderAscending )
    {
        getLogger().debug( "NOT CACHED - .findUsersByFullNameKey(String, boolean)" );
        return this.userImpl.findUsersByFullNameKey( fullNameKey, orderAscending );
    }

    public List findUsersByUsernameKey( String usernameKey, boolean orderAscending )
    {
        getLogger().debug( "NOT CACHED - .findUsersByUsernameKey(String, boolean)" );
        return this.userImpl.findUsersByUsernameKey( usernameKey, orderAscending );
    }

    public String getId()
    {
        return "Cached User Manager [" + this.userImpl.getId() + "]";
    }

    public List getUsers()
    {
        getLogger().debug( "NOT CACHED - .getUsers()" );
        return this.userImpl.getUsers();
    }

    public List getUsers( boolean orderAscending )
    {
        getLogger().debug( "NOT CACHED - .getUsers(boolean)" );
        return this.userImpl.getUsers( orderAscending );
    }

    public void removeUserManagerListener( UserManagerListener listener )
    {
        this.userImpl.removeUserManagerListener( listener );
    }

    public User updateUser( User user )
        throws UserNotFoundException
    {
        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
        return this.userImpl.updateUser( user );
    }

    public boolean userExists( Object principal )
    {
        if ( usersCache.hasKey( principal ) )
        {
            return true;
        }

        return this.userImpl.userExists( principal );
    }

    public void userManagerInit( boolean freshDatabase )
    {
        if ( userImpl instanceof UserManager )
        {
            ( (UserManagerListener) this.userImpl ).userManagerInit( freshDatabase );
        }

        EhcacheUtils.clearAllCaches( getLogger() );
    }

    public void userManagerUserAdded( User user )
    {
        if ( userImpl instanceof UserManager )
        {
            ( (UserManagerListener) this.userImpl ).userManagerUserAdded( user );
        }

        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
    }

    public void userManagerUserRemoved( User user )
    {
        if ( userImpl instanceof UserManager )
        {
            ( (UserManagerListener) this.userImpl ).userManagerUserRemoved( user );
        }

        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
    }

    public void userManagerUserUpdated( User user )
    {
        if ( userImpl instanceof UserManager )
        {
            ( (UserManagerListener) this.userImpl ).userManagerUserUpdated( user );
        }

        if ( user != null )
        {
            usersCache.invalidateKey( user.getPrincipal() );
        }
    }
}
