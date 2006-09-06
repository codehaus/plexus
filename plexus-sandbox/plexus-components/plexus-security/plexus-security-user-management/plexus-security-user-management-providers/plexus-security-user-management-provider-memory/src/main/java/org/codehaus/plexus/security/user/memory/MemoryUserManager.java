package org.codehaus.plexus.security.user.memory;

import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.security.user.UserManager"
 *   role-hint="memory"
 */
public class MemoryUserManager
    implements UserManager
{
    private Map users;

    public MemoryUserManager()
    {
        users = new HashMap();
    }

    public User addUser( User user )
    {
        users.put( user.getPrincipal(), user );

        return user;
    }

    public User updateUser( User user )
    {
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
