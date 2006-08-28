package org.codehaus.plexus.components.password;

/*
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Facilitates a secure storage of passwords, accessed by an indentifier.
 *
 * @author Brett Porter
 * @version $Id$
 */
public interface PasswordStore
{
    static final String ROLE = PasswordStore.class.getName();

    /**
     * Retrieve the unencrypted password by it's key, eg <code>wagon.http.proxy.password</code>.
     * This method will require the entry of the master password if the store is locked.
     *
     * @param id the key to lookup the password by
     * @return the plaintext password, or <code>null</code> if there is none under that key
     * @throws UnsupportedOperationException if the configuration of the store does not allow password retrieval
     * @throws PasswordStoreException if there was a problem reading the password from the store
     */
    String getPassword( String id )
        throws UnsupportedOperationException, PasswordStoreException;

    /**
     * Checks whether a password is correct. This can be used when the configuration of the store
     * does not allow direct retrieval of the password.
     *
     * @param id the key to lookup the password by
     * @param attempt the attempt at the password (plaintext)
     * @return whether the password was correct or not
     * @throws PasswordStoreException if there was a problem reading the password from the store
     */
    boolean checkPassword( String id, String attempt )
        throws PasswordStoreException;

    /**
     * Change a stored password. May or may not require the master password depending on whether
     * the store is unlocked, or the configuration of the store requires a master password to change
     * a stored password when the old one can be given.
     *
     * @param id the key to lookup the password by
     * @param oldPassword the previously stored password (plaintext)
     * @param newPassword the replacement password (plaintext)
     * @throws PasswordStoreException if there was a problem reading the password from the store, or the old password was incorrect
     */
    void setPassword( String id, String oldPassword, String newPassword )
        throws PasswordStoreException;

    /**
     * Change or create the password stored for a particular identifier.
     * May require the store to be unlocked, and may fail if the id already exists
     * dependending on the configuration of the store.
     *
     * @param id the key to store the password under
     * @param newPassword the password (plaintext)
     * @throws PasswordStoreException if there was a problem reading the password from the store, or the password was already in the store with that key
     */
    void setPassword( String id, String newPassword )
        throws PasswordStoreException;

    /**
     * Remove the given password from the store.
     * @param id the key to lookup the password by
     */
    void removePassword( String id )
        throws PasswordStoreException;

    /**
     * Unlock the store to authorise further actions.
     * @param masterPassword the master password required to unlock the store
     */
    void unlock( String masterPassword );

    /**
     * Unlock the store to authorise further actions. If required, the master password will be prompted for.
     */
    void unlock();

    /**
     * Lock the store to prevent further unauthorised actions.
     */
    void lock();
}
