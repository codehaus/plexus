package org.codehaus.plexus.security.ui.web.action;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.security.policy.PasswordEncoder;
import org.codehaus.plexus.security.policy.PasswordRuleViolationException;
import org.codehaus.plexus.security.system.DefaultSecuritySession;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.model.EditUserCredentials;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * AccountAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-account"
 * instantiation-strategy="per-lookup"
 */
public class AccountAction
    extends AbstractUserCredentialsAction
    implements CancellableAction
{
    private static final String ACCOUNT_SUCCESS = "security-account-success";

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private EditUserCredentials user;
    
    private String oldPassword;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        SecuritySession session = getSecuritySession();

        if ( !session.isAuthenticated() )
        {
            addActionError( "Unable to show your account. Not logged in." );
            return REQUIRES_AUTHENTICATION;
        }

        String username = session.getUser().getUsername();

        if ( username == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        UserManager manager = super.securitySystem.getUserManager();

        if ( !manager.userExists( username ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + username + "' does not exist." );
            return ERROR;
        }

        internalUser = user;

        try
        {
            User u = manager.findUser( username );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }

            user = new EditUserCredentials( u );
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to get User '" + username + "': " + e.getMessage() );
            return ERROR;
        }

        return INPUT;
    }

    public String submit()
    {
        SecuritySession session = getSecuritySession();

        if ( !session.isAuthenticated() )
        {
            addActionError( "Unable to show your account. Not logged in." );
            return REQUIRES_AUTHENTICATION;
        }

        String username = session.getUser().getUsername();

        if ( username == null )
        {
            addActionError( "Unable to edit user with null username." );
            return ERROR;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( "Unable to edit user with empty username." );
            return ERROR;
        }

        if ( user == null )
        {
            addActionError( "Unable to edit user with null user credentials." );
            return ERROR;
        }

        if ( !user.getPassword().equals( user.getConfirmPassword() ) )
        {
            addFieldError( "user.confirmPassword", "Password confirmation failed.  Passwords do not match." );
            return ERROR;
        }

        UserManager manager = super.securitySystem.getUserManager();

        if ( !manager.userExists( username ) )
        {
            // Means that the role name doesn't exist.
            // We need to fail fast and return to the previous page.
            addActionError( "User '" + username + "' does not exist." );
            return ERROR;
        }

        internalUser = user;

        try
        {
            User u = manager.findUser( username );
            if ( u == null )
            {
                addActionError( "Unable to operate on null user." );
                return ERROR;
            }
            
            if ( StringUtils.isNotEmpty( user.getPassword() ) )
            {
                PasswordEncoder encoder = securitySystem.getPolicy().getPasswordEncoder();

                if ( !encoder.isPasswordValid( u.getEncodedPassword(), oldPassword ) )
                {
                    addFieldError( "oldPassword", "Password provided does not match existing." );
                    return ERROR;
                }
                
                u.setPassword( user.getPassword() );
            }

            u.setFullName( user.getFullName() );
            u.setEmail( user.getEmail() );
            u.setPassword( user.getPassword() );

            manager.updateUser( u );

            //check if current user then update the session
            if ( getSecuritySession().getUser().getUsername().equals( u.getUsername() ) )
            {
                SecuritySession securitySession =
                    new DefaultSecuritySession( getSecuritySession().getAuthenticationResult(), u );

                this.session.put( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );

                setSession( this.session );
            }
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find User '" + username + "': " + e.getMessage() );
            return ERROR;
        }
        catch ( PasswordRuleViolationException e )
        {
            processPasswordRuleViolations( e );
            return ERROR;
        }

        return ACCOUNT_SUCCESS;
    }
    
    public String cancel()
    {
		return CANCEL;
	}

	// ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public EditUserCredentials getUser()
    {
        return user;
    }

    public void setUser( EditUserCredentials user )
    {
        this.user = user;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        return bundle;
    }

    public void setOldPassword( String oldPassword )
    {
        this.oldPassword = oldPassword;
    }
    
    public boolean isSelf()
    {
        return true;
    }
}
