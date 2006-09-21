package org.codehaus.plexus.security.authentication.user;

/*
 * Copyright 2005 The Codehaus.
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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.Authenticator;
import org.codehaus.plexus.security.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;
import org.codehaus.plexus.security.policy.PasswordEncoder;
import org.codehaus.plexus.security.policy.PolicyViolationException;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * {@link Authenticator} implementation that uses a wrapped {@link UserManager} to authenticate.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a> 
 * @version $Id$
 * @plexus.component
 *   role="org.codehaus.plexus.security.authentication.Authenticator"
 *   role-hint="user-manager"
 */
public class UserManagerAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    /**
     * @plexus.requirement
     */
    private UserManager userManager;
    
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy securityPolicy;
    
    public String getId()
    {
        return "UserManagerAuthenticator";
    }

    /**
     * @throws org.codehaus.plexus.security.policy.AccountLockedException 
     * @throws MustChangePasswordException 
     * @throws PolicyViolationException 
     * @see org.codehaus.plexus.security.authentication.Authenticator#authenticate(org.codehaus.plexus.security.authentication.AuthenticationDataSource)
     */
    public AuthenticationResult authenticate( AuthenticationDataSource ds )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException
    {
        boolean authenticationSuccess = false;
        String username = null;
        Exception resultException = null;
        PasswordBasedAuthenticationDataSource source = (PasswordBasedAuthenticationDataSource) ds;
        
        try
        {
            getLogger().debug( "Authenticate: " + source );
            User user = userManager.findUser( source.getUsername() );
            username = user.getUsername();
            
            if (user.isLocked())
            {
                throw new AccountLockedException( "Account " + source.getUsername() + " is locked.", user );
            }
            
            if (user.isPasswordChangeRequired())
            {
                throw new MustChangePasswordException( "User " + source.getUsername() + " must change their password." );
            }
            
            PasswordEncoder encoder = securityPolicy.getPasswordEncoder();
            getLogger().debug( "PasswordEncoder: " + encoder.getClass().getName() );
            
            boolean isPasswordValid = encoder.isPasswordValid( user.getEncodedPassword(), source.getPassword() );
            if ( isPasswordValid )
            {
                getLogger().debug( "User " + source.getUsername() + " provided a valid password" );
                
                securityPolicy.extensionPasswordExpiration( user );
                
                authenticationSuccess = true;
                user.setCountFailedLoginAttempts( 0 );
                userManager.updateUser( user );
                
                return new AuthenticationResult( true, source.getUsername(), null );
            }
            else
            {
                getLogger().warn( "Password is Invalid for user " + source.getUsername() + "." );
                
                try
                {
                    securityPolicy.extensionExcessiveLoginAttempts( user );
                }
                finally
                {
                    userManager.updateUser( user );
                }
                
                return new AuthenticationResult( false, source.getUsername(), null );
            }
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Login for user " + source.getUsername() + " failed. user not found." );
            resultException = e;
        }
        
        return new AuthenticationResult(authenticationSuccess, username, resultException);
    }

    /**
     * Returns the wrapped {@link UserManager} used by this {@link Authenticator} 
     * implementation for authentication.
     * @return the userManager
     */
    public UserManager getUserManager()
    {
        return userManager;
    }

    /**
     * Sets a {@link UserManager} to be used by this {@link Authenticator} 
     * implementation for authentication.
     * @param userManager the userManager to set
     */
    public void setUserManager( UserManager userManager )
    {
        this.userManager = userManager;
    }

    public boolean supportsDataSource( AuthenticationDataSource source )
    {
        return ( source instanceof PasswordBasedAuthenticationDataSource );
    }

}
