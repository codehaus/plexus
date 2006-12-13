package org.codehaus.plexus.security.ui.web.action;

import org.codehaus.plexus.security.policy.PasswordEncoder;
import org.codehaus.plexus.security.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.policy.PasswordRuleViolations;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
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
    extends AbstractSecurityAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String existingPassword;

    private String newPassword;

    private String newPasswordConfirm;
    
    private boolean provideExisting;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------
    
    public String show()
    {
        SecuritySession session = getSecuritySession();
        
        provideExisting = StringUtils.isNotEmpty( session.getUser().getEncodedPassword() );
        
        return INPUT;
    }
    
    public String submit()
    {
        SecuritySession session = getSecuritySession();

        provideExisting = StringUtils.isNotEmpty( session.getUser().getEncodedPassword() );
        
        if ( provideExisting && StringUtils.isEmpty( existingPassword ) )
        {
            addFieldError( "existingPassword", "Existing Password cannot be empty." );
        }

        if ( StringUtils.isEmpty( newPassword ) )
        {
            addFieldError( "newPassword", "New Password cannot be empty." );
        }

        if ( !StringUtils.equals( newPassword, newPasswordConfirm ) )
        {
            addFieldError( "newPassword", "Password confirmation failed.  Passwords do not match." );
        }


        User user = session.getUser();

        // Test existing Password.
        PasswordEncoder encoder = securitySystem.getPolicy().getPasswordEncoder();

        if ( provideExisting )
        {
            if ( !encoder.isPasswordValid( user.getEncodedPassword(), existingPassword ) )
            {
                addFieldError( "existingPassword", "Password does not match existing." );
            }
        }

        // Validate the Password.
        try
        {
            User tempUser = securitySystem.getUserManager().createUser( "temp", "temp", "temp" );
            tempUser.setPassword( newPassword );
            securitySystem.getPolicy().validatePassword( tempUser );
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
                    addFieldError( "newPassword", violation );
                }
            }
        }
        
        // Toss error (if any exists)
        if ( hasActionErrors() || hasFieldErrors() || hasActionMessages() )
        {
            newPassword = "";
            newPasswordConfirm = "";
            existingPassword = "";
            return ERROR;
        }

        // We can save the new password.
        try
        {
            String encodedPassword = encoder.encodePassword( newPassword );
            user.setEncodedPassword( encodedPassword );
            user.setPassword( newPassword );            
            securitySystem.getUserManager().updateUser( user );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to update user '" + user.getUsername() + "' not found." );
            addActionError( "Likely occurs because an Administrator deleted your account." );
            
            return ERROR;
        }
        
        getLogger().info( "Password Change Request Success." );

        if ( !session.isAuthenticated() )
        {
            getLogger().debug( "User is not authenticated." );
            return REQUIRES_AUTHENTICATION;
        }

        /*
        *  If provide existing is true, then this was a normal password change flow, if it is
        * false then it is changing the password from the registration flow in which case direct to
         * external link
         */
        if ( !provideExisting )
        {
           return RegisterAction.REGISTER_SUCCESS; 
        }
        else
        {
            return SUCCESS;
        }
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

    public boolean isProvideExisting()
    {
        return provideExisting;
    }

    public void setProvideExisting( boolean provideExisting )
    {
        // Do nothing.
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.AUTHONLY;
    }
}
