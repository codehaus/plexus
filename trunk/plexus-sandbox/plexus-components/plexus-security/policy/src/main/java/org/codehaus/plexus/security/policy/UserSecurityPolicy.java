package org.codehaus.plexus.security.policy;

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

import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;

import java.util.List;

/**
 * User Security Policy Settings.  
 * 
 * @version $Id$
 * @todo roll password management into it's own object.
 */
public interface UserSecurityPolicy
{
    public static final String ROLE = UserSecurityPolicy.class.getName();
    
    // ----------------------------------------------------------------------
    // Password Management
    // ----------------------------------------------------------------------

    /**
     * Gets the password encoder to use.
     * 
     * @return the PasswordEncoder implementation to use.
     */
    public PasswordEncoder getPasswordEncoder();
    
    
    /**
     * Add a Specific Rule to the Password Rules List.
     * 
     * @param rule the rule to add. 
     */
    void addPasswordRule( PasswordRule rule );
    
    /**
     * Get the Password Rules List.
     * 
     * @return the list of {@link PasswordRule} objects.
     */
    List getPasswordRules();

    /**
     * Set the Password Rules List.
     * 
     * @param rules the list of {@link PasswordRule} objects.
     */
    void setPasswordRules( List rules );
    
    /**
     * Gets the count of Previous Passwords that should be tracked.
     * 
     * @return the count of previous passwords to track.
     */
    public int getPreviousPasswordsCount();
    
    /**
     * Sets the count of previous passwords that should be tracked.
     * 
     * @param count the count of previous passwords to track.
     */
    public void setPreviousPasswordsCount( int count );

    /**
     * Gets the count of login attempts to allow.
     * 
     * @return the count of login attempts to allow.
     */
    public int getLoginAttemptCount();
    
    /**
     * Sets the count of login attempts to allow.
     * 
     * @param count the count of login attempts to allow.
     */
    public void setLoginAttemptCount( int count );
    
    /**
     * Change the password of a user.
     * 
     * This method does not check if a user is allowed to change his/her password.
     * Any kind of authorization checks for password change allowed on guest or 
     * anonymous users needs to occur before calling this method.
     * 
     * This method does not persist the newly changed user password.
     * That will require a call to {@link UserManager#updateUser(User)}.
     * 
     * @param user the user password to validate, remember, and encode.
     */
    public void changeUserPassword( User user );
    
    /**
     * Validate the incoming {@link User#getPassword()} against the specified
     * PasswordRules.
     * 
     * @param user the user to validate.
     */
    public void validatePassword( User user );
}
