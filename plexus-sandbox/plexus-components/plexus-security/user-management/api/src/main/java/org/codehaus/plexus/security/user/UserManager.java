package org.codehaus.plexus.security.user;

import org.codehaus.plexus.security.user.policy.UserSecurityPolicy;

import java.util.List;

/**
 * User Manager Interface
 * 
 * @author Jason van Zyl
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 */
public interface UserManager
{
    public static final String ROLE = UserManager.class.getName();
    
    /**
     * Factory method to create new User Objects based on provider specific
     * implementation.
     * 
     * User objects created this way do not exist in the provider's underlying
     * data store until a call to {@link #addUser(User)} is made.
     * 
     * @param username the username for this user.
     * @param fullName the full name for this user.
     * @param emailAddress the email address for this user.
     * 
     * @return the new user object ready to use.
     */
    User createUser(String username, String fullName, String emailAddress);
    
    /**
     * Get the List of {@link User} objects.
     * 
     * @return the List of {@link User} Objects.
     */
    List getUsers();

    /**
     * Add a User.
     * 
     * @param user the user to add.
     * @return the user that was just added.
     */
    User addUser( User user );

    /**
     * Update a User.
     * 
     * @param user the user to update.
     * @return the user that was just updated.
     * @throws UserNotFoundException if the user was not found to update.
     */
    User updateUser( User user )
        throws UserNotFoundException;
    
    /**
     * Find a User using a User name.
     * 
     * @param username the username to find.
     * @return the user.
     * @throws UserNotFoundException if the user was not found.
     */
    User findUser( String username )
        throws UserNotFoundException;

    /**
     * Find a User using the principal.
     * 
     * @param principal the principal to look for.
     * @return the user.
     * @throws UserNotFoundException if the user was not found.
     */
    User findUser( Object principal )
        throws UserNotFoundException;

    /**
     * true if the user exists, false if it doesn't
     *
     * @param principal
     * @return true, if user exists
     */
    boolean userExists( Object principal );

    /**
     * Delete a user using the principal.
     * 
     * @param principal the principal to look for.
     * @throws UserNotFoundException the user was not found.
     */
    void deleteUser( Object principal )
        throws UserNotFoundException;
    
    /**
     * Delete a user using the username.
     * 
     * @param principal the principal to look for.
     * @throws UserNotFoundException the user was not found.
     */
    void deleteUser( String username )
        throws UserNotFoundException;
    
    /**
     * Set the User Security Policy.
     * 
     * @param securityPolicy the security policy.
     */
    void setUserSecurityPolicy(UserSecurityPolicy securityPolicy);
    
    /**
     * Get the User Security Policy.
     * 
     * @return the user security policy.
     */
    UserSecurityPolicy getUserSecurityPolicy();
}
