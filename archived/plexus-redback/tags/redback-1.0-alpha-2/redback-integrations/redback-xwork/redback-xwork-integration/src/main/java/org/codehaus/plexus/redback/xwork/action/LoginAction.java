package org.codehaus.plexus.redback.xwork.action;

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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.codehaus.plexus.redback.authentication.AuthenticationConstants;
import org.codehaus.plexus.redback.authentication.AuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.AuthenticationException;
import org.codehaus.plexus.redback.authentication.AuthenticationResult;
import org.codehaus.plexus.redback.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.redback.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.redback.keys.AuthenticationKey;
import org.codehaus.plexus.redback.keys.KeyManagerException;
import org.codehaus.plexus.redback.keys.KeyNotFoundException;
import org.codehaus.plexus.redback.policy.AccountLockedException;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.util.AutoLoginCookies;
import org.codehaus.plexus.util.StringUtils;

import com.opensymphony.webwork.ServletActionContext;

/**
 * LoginAction
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-login"
 * instantiation-strategy="per-lookup"
 */
public class LoginAction
    extends AbstractSecurityAction
    implements CancellableAction
{
    private static final String LOGIN_SUCCESS = "security-login-success";

    private static final String PASSWORD_CHANGE = "security-must-change-password";

    private static final String ACCOUNT_LOCKED = "security-login-locked";

    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    private String username;

    private String password;

    private String validateMe;

    private String resetPassword;

    private boolean rememberMe;

    /**
     * @plexus.requirement
     */
    private AutoLoginCookies autologinCookies;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {             
        return INPUT;
    }

    /**
     * 1) check if this is a validation authentication action
     * 2) check if this is a reset password authentication action
     * 3) sets up a password based authentication and passes on to webLogin()
     *
     * @return
     */
    public String login()
    {
        if ( StringUtils.isNotEmpty( validateMe ) )
        {
            // Process a login / validate request.
            return validated();
        }

        if ( StringUtils.isNotEmpty( resetPassword ) )
        {
            // Process a login / reset password request.
            return resetPassword();
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addFieldError( "username", getText( "username.required" ) );
            return ERROR;
        }

        PasswordBasedAuthenticationDataSource authdatasource = new PasswordBasedAuthenticationDataSource();
        authdatasource.setPrincipal( username );
        authdatasource.setPassword( password );

        return webLogin( authdatasource, rememberMe );
    }

    /**
     * 1) sets up a token based authentication
     * 2) forces a password change requirement to the user
     * 3) passes on to webLogin()
     *
     * @return
     */
    public String resetPassword()
    {
        if ( StringUtils.isEmpty( resetPassword ) )
        {
            addActionError( getText( "reset.password.missing" ) );
            return ERROR;
        }

        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( resetPassword );

            User user = securitySystem.getUserManager().findUser( authkey.getForPrincipal() );

            user.setPasswordChangeRequired( true );
            user.setEncodedPassword( "" );

            TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource();
            authsource.setPrincipal( user.getPrincipal().toString() );
            authsource.setToken( authkey.getKey() );

            securitySystem.getUserManager().updateUser( user );

            return webLogin( authsource, false );
        }
        catch ( KeyNotFoundException e )
        {
            getLogger().info( "Invalid key requested: " + resetPassword );
            addActionError( getText( "cannot.find.key" ) );
            return ERROR;
        }
        catch ( KeyManagerException e )
        {
            addActionError( getText( "cannot.find.key.at.the.moment" ) );
            getLogger().warn( "Key Manager error: ", e );
            return ERROR;
        }
        catch ( UserNotFoundException e )
        {
            addActionError( getText( "cannot.find.user" ) );
            return ERROR;
        }
    }

    /**
     * 1) sets up a token based authentication
     * 2) forces a password change requirement to the user
     * 3) passes on to webLogin()
     *
     * @return
     */
    public String validated()
    {
        if ( StringUtils.isEmpty( validateMe ) )
        {
            addActionError( getText( "validation.failure.key.missing" ) );
            return ERROR;
        }

        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( validateMe );

            User user = securitySystem.getUserManager().findUser( authkey.getForPrincipal() );

            user.setValidated( true );
            user.setLocked( false );
            user.setPasswordChangeRequired( true );
            user.setEncodedPassword( "" );

            TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource();
            authsource.setPrincipal( user.getPrincipal().toString() );
            authsource.setToken( authkey.getKey() );

            securitySystem.getUserManager().updateUser( user );

            return webLogin( authsource, false );
        }
        catch ( KeyNotFoundException e )
        {
            getLogger().info( "Invalid key requested: " + validateMe );
            addActionError( getText( "cannot.find.key" ) );
            return ERROR;
        }
        catch ( KeyManagerException e )
        {
            addActionError( getText( "cannot.find.key.at.the.momment" ) );
            return ERROR;
        }
        catch ( UserNotFoundException e )
        {
            addActionError( getText( "cannot.find.user" ) );
            return ERROR;
        }
    }

    public String cancel()
    {
        return CANCEL;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getValidateMe()
    {
        return validateMe;
    }

    public void setValidateMe( String validateMe )
    {
        this.validateMe = validateMe;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
    }

    public String getResetPassword()
    {
        return resetPassword;
    }

    public void setResetPassword( String resetPassword )
    {
        this.resetPassword = resetPassword;
    }

    public boolean isRememberMe()
    {
        return rememberMe;
    }

    public void setRememberMe( boolean rememberMe )
    {
        this.rememberMe = rememberMe;
    }


    /**
     * 1) attempts to authentication based on the passed in data source
     * 2) if successful sets cookies and returns LOGIN_SUCCESS
     * 3) if failure then check what kinda failure and return error
     *
     * @param authdatasource
     * @param rememberMe
     * @return
     */
    private String webLogin( AuthenticationDataSource authdatasource, boolean rememberMe )
    {
        // An attempt should log out your authentication tokens first!
        setAuthTokens( null );

        clearErrorsAndMessages();

        // TODO: share this section with AutoLoginInterceptor
        try
        {
            SecuritySession securitySession = securitySystem.authenticate( authdatasource );

            if ( securitySession.getAuthenticationResult().isAuthenticated() )
            {
                // TODO: this should not happen if there is a password change required - but the password change action needs to log the user in on success to swap them
                // Success!  Create tokens.
                setAuthTokens( securitySession );

                if ( rememberMe )
                {
                    autologinCookies.setRememberMeCookie( authdatasource.getPrincipal() );
                }
                autologinCookies.setSignonCookie( authdatasource.getPrincipal() );

                // check if user is forced to change their password (also see policy enforcement interceptor)
                if( securitySession.getUser().isPasswordChangeRequired() )
                {
                    return PASSWORD_CHANGE;
                }              

                return LOGIN_SUCCESS;
            }
            else
            {
                getLogger().debug( "Login Action failed against principal : " +
                    securitySession.getAuthenticationResult().getPrincipal(),
                                   securitySession.getAuthenticationResult().getException() );

                AuthenticationResult result = securitySession.getAuthenticationResult();
                if ( result.getExceptionsMap() != null && !result.getExceptionsMap().isEmpty() )
                {
                    if ( result.getExceptionsMap().get( AuthenticationConstants.AUTHN_NO_SUCH_USER ) != null )
                    {
                        addActionError( getText( "incorrect.username.password" ) );
                    }
                    else
                    {
                        addActionError( getText( "authentication.failed" ) );
                    }
                }
                else
                {
                    addActionError( getText( "authentication.failed" ) );
                }

                return ERROR;
            }
        }
        catch ( AuthenticationException ae )
        {
            List list = new ArrayList();
            list.add( ae.getMessage() );
            addActionError( getText( "authentication.exception", list ) );
            return ERROR;
        }
        catch ( UserNotFoundException ue )
        {
            List list = new ArrayList();
            list.add( username );
            list.add( ue.getMessage() );
            addActionError( getText( "user.not.found.exception", list ) );
            return ERROR;
        }
        catch ( AccountLockedException e )
        {
            addActionError( getText( "account.locked" ) );
            return ACCOUNT_LOCKED;
        }
    }
}
