package org.codehaus.plexus.security.user.memory;

import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.security.user.policy.UserSecurityPolicy;
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
            getUserSecurityPolicy().changeUserPassword( user );
        }

        return user;
    }

    public User updateUser( User user )
    {
        // If password is supplied, assume changing of password.
        // TODO: Consider adding a boolean to the updateUser indicating a password change or not.
        if ( StringUtils.isNotEmpty( user.getPassword() ) )
        {
            getUserSecurityPolicy().changeUserPassword( user );
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

    public UserSecurityPolicy getUserSecurityPolicy()
    {
        return userSecurityPolicy;
    }

    public void setUserSecurityPolicy( UserSecurityPolicy securityPolicy )
    {
        userSecurityPolicy = securityPolicy;
    }
}
