package org.codehaus.plexus.security.user;

/**
 * @author Jason van Zyl
 */
public interface UserManager
{
    String ROLE = UserManager.class.getName();

    User addUser( User user );

    User updateUser( User user );

    User findUser( Object principal )
        throws UserNotFoundException;

    void deleteUser( Object principal );
}
