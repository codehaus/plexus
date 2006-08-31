package org.codehaus.plexus.security.user;

/**
 * @author Jason van Zyl
 */
public interface UserManager
{
    String ROLE = UserManager.class.getName();

    User findUser( Object principal )
        throws UserNotFoundException;
}
