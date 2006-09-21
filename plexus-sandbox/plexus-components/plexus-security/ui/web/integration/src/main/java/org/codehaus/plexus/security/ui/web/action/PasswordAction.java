package org.codehaus.plexus.security.ui.web.action;

import org.codehaus.plexus.security.policy.PasswordEncoder;
import org.codehaus.plexus.security.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.policy.PasswordRuleViolations;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.util.StringUtils;

import java.util.Iterator;

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

/**
 * PasswordAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action" 
 *                   role-hint="pss-password" 
 *                   instantiation-strategy="per-lookup"
 */
public class PasswordAction
    extends AbstractAuthenticationAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private UserManager manager;
    
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy securityPolicy;
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String existingPassword;

    private String newPassword;

    private String newPasswordConfirm;

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------
    
    public String show()
    {
        return INPUT;
    }
    
    public String submit()
    {
        if ( StringUtils.isEmpty( existingPassword ) )
        {
            addFieldError( "existingPassword", "Existing Password cannot be empty." );
        }

        if ( StringUtils.isEmpty( newPassword ) )
        {
            addFieldError( "newPassword", "New Password cannot be empty." );
        }

        if ( StringUtils.equals( newPassword, newPasswordConfirm ) )
        {
            addFieldError( "existingPassword", "Password confirmation failed.  Passwords do not match." );
            newPassword = "";
            newPasswordConfirm = "";
        }
        
        User user;
        try
        {
            user = getSessionUser();
            securityPolicy.validatePassword( user );
        }
        catch ( PasswordRuleViolationException e )
        {
            PasswordRuleViolations violations = e.getViolations();

            if ( violations != null )
            {
                Iterator it = violations.getLocalizedViolations().iterator();
                while ( it.hasNext() )
                {
                    String violation = (String) it.next();
                    addFieldError( "password", violation );
                }
            }
            return ERROR;
        }

        if ( hasActionErrors() || hasFieldErrors() )
        {
            return ERROR;
        }
        
        PasswordEncoder encoder = securityPolicy.getPasswordEncoder();

        String encodedPassword = encoder.encodePassword( existingPassword ); 
        
        if ( encoder.isPasswordValid( user.getEncodedPassword(), encodedPassword ) )
        {
            // We can save the new password.
            try
            {
                manager.updateUser( user );
            }
            catch ( UserNotFoundException e )
            {
                addActionError( "Unable to update user '" + user.getUsername() + "' not found." );
                addActionError( "Likely occurs because an Administrator deleted your account." );
                
                return ERROR;
            }
        }
        
        return SUCCESS;
    }
    
    private User getSessionUser()
    {
        return (User) session.get( SecuritySystemConstants.USER_KEY );
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getExistingPassword()
    {
        return existingPassword;
    }

    public void setExistingPassword( String existingPassword )
    {
        this.existingPassword = existingPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword( String newPassword )
    {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm()
    {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm( String newPasswordConfirm )
    {
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
