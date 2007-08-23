package org.codehaus.plexus.redback.authentication.users;

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
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authentication.Authenticator;
import org.codehaus.plexus.redback.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationConstants;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.policy.MustChangePasswordException;
import org.codehaus.plexus.redback.policy.PasswordEncoder;
import org.codehaus.plexus.redback.policy.PolicyViolationException;
import org.codehaus.plexus.redback.policy.UserSecurityPolicy;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;

import java.util.Map;
import java.util.HashMap;

/**
 * {@link Authenticator} implementation that uses a wrapped {@link UserManager} to authenticate.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a> 
 * @version $Id$
 * @plexus.component
 *   role="org.codehaus.plexus.redback.authentication.Authenticator"
 *   role-hint="user-manager"
 */
public class UserManagerAuthenticator
    extends AbstractLogEnabled
    implements Authenticator
{
    /**
     * @plexus.requirement role-hint="configurable"
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
     * @throws org.codehaus.plexus.redback.policy.AccountLockedException 
     * @throws MustChangePasswordException 
     * @throws PolicyViolationException 
     * @see org.codehaus.plexus.redback.authentication.Authenticator#authenticate(org.codehaus.plexus.redback.authentication.AuthenticationDataSource)
     */
    public AuthenticationResult authenticate( AuthenticationDataSource ds )
        throws AuthenticationException, AccountLockedException
    {
        boolean authenticationSuccess = false;
        String username = null;
        Exception resultException = null;
        PasswordBasedAuthenticationDataSource source = (PasswordBasedAuthenticationDataSource) ds;
        Map authnResultExceptionsMap = new HashMap();
        
        try
        {
            getLogger().debug( "Authenticate: " + source );
            User user = userManager.findUser( source.getPrincipal() );
            username = user.getUsername();
            
            if ( user.isLocked() && !user.isPasswordChangeRequired() )
            {
                throw new AccountLockedException( "Account " + source.getPrincipal() + " is locked.", user );
            }
            
            PasswordEncoder encoder = securityPolicy.getPasswordEncoder();
            getLogger().debug( "PasswordEncoder: " + encoder.getClass().getName() );
            
            boolean isPasswordValid = encoder.isPasswordValid( user.getEncodedPassword(), source.getPassword() );
            if ( isPasswordValid )
            {
                getLogger().debug( "User " + source.getPrincipal() + " provided a valid password" );
                
                try
                {
                    securityPolicy.extensionPasswordExpiration( user );
                }
                catch ( MustChangePasswordException e )
                {
                    user.setPasswordChangeRequired( true );
                }
                
                authenticationSuccess = true;
                user.setCountFailedLoginAttempts( 0 );
                userManager.updateUser( user );
                
                return new AuthenticationResult( true, source.getPrincipal(), null );
            }
            else
            {
                getLogger().warn( "Password is Invalid for user " + source.getPrincipal() + "." );
                authnResultExceptionsMap.put( AuthenticationConstants.AUTHN_NO_SUCH_USER,
                    "Password is Invalid for user " + source.getPrincipal() + "." );
                
                try
                {
                    securityPolicy.extensionExcessiveLoginAttempts( user );
                }
                finally
                {
                    userManager.updateUser( user );
                }
                
                return new AuthenticationResult( false, source.getPrincipal(), null, authnResultExceptionsMap );
            }
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Login for user " + source.getPrincipal() + " failed. user not found." );
            resultException = e;
            authnResultExceptionsMap.put( AuthenticationConstants.AUTHN_NO_SUCH_USER, 
                "Login for user \" + source.getPrincipal() + \" failed. user not found." );
        }
        
        return new AuthenticationResult(authenticationSuccess, username, resultException, authnResultExceptionsMap );
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
