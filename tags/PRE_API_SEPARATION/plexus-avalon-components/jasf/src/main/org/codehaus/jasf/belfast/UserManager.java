package org.codehaus.jasf.belfast;

import java.util.List;

import org.codehaus.jasf.belfast.exception.BackendException;
import org.codehaus.jasf.belfast.exception.EntityExistsException;
import org.codehaus.jasf.belfast.exception.PasswordMismatchException;
import org.codehaus.jasf.entities.web.User;
import org.codehaus.jasf.exception.UnknownEntityException;

/**
 * A standard interface for managing users that trys to be backend agnostic.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public interface UserManager
{
    public static final String ROLE = UserManager.class.getName();
    
    public static final String SELECTOR = UserManager.class.getName() + "Selector";
    
    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    boolean accountExists(User user)
        throws BackendException;

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    boolean accountExists(String userName)
        throws BackendException;
        
    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @param password The password to use for the object creation
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    void createAccount(User user, String password)
        throws EntityExistsException, BackendException;
        
    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    void removeUser(User user)
        throws UnknownEntityException, BackendException;
    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password suplied by the user.
     * @param newPassword the current password requested by the user.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    void changePassword(User user, String oldPassword, String newPassword)
        throws PasswordMismatchException, UnknownEntityException,
               BackendException;

    /**
     * Forcibly sets new password for an User.
     *
     * This is supposed by the administrator to change the forgotten or
     * compromised passwords. Certain implementatations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    void forcePassword(User user, String password)
        throws UnknownEntityException, BackendException;

    
    User retrieveEntity( long id );
    
    List getUsers();
}
