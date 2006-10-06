package org.codehaus.plexus.security.ui.web.action;

/*
 * Copyright 2001-2006 The Codehaus.
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

import com.opensymphony.webwork.ServletActionContext;

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.util.CookieUtils;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * AbstractAuthenticationAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractAuthenticationAction
    extends AbstractSecurityAction
{
    static final String LOGIN_SUCCESS = "security-login-success";

    static final String LOGIN_CANCEL = "security-login-cancel";

    static final String PASSWORD_CHANGE = "must-change-password";

    static final String ACCOUNT_LOCKED = "security-login-locked";

    private String domain;

    private String webappContext;

    protected String webLogin( SecuritySystem securitySystem, AuthenticationDataSource authdatasource,
                               boolean rememberMe )
    {
        // An attempt should log out your authentication tokens first!
        setAuthTokens( null );

        clearErrorsAndMessages();

        if ( StringUtils.isEmpty( domain ) )
        {
            domain = "." + ServletActionContext.getRequest().getServerName();

            int idx = domain.lastIndexOf( '.' );
            if ( idx > 0 )
            {
                // domain has a dot.
                idx = domain.lastIndexOf( '.', idx - 1 );
                if ( idx > 0 )
                {
                    // domain has second dot. trim it.
                    domain = domain.substring( idx );
                }
            }
        }

        if ( StringUtils.isEmpty( webappContext ) )
        {
            webappContext = ServletActionContext.getRequest().getContextPath();

            if ( StringUtils.isEmpty( webappContext ) )
            {
                // Still empty?  means you are a root context.
                webappContext = "/";
            }
        }

        try
        {
            SecuritySession securitySession = securitySystem.authenticate( authdatasource );

            if ( securitySession.getAuthenticationResult().isAuthenticated() )
            {
                // Success!  Create tokens.
                setAuthTokens( securitySession );

                if ( rememberMe )
                {
                    try
                    {
                        int timeout = securitySystem.getPolicy().getRememberMeSettings().getCookieTimeout();
                        KeyManager keyManager = securitySystem.getKeyManager();
                        AuthenticationKey authkey = keyManager.createKey( authdatasource.getPrincipal(),
                                                                          "Remember Me Key", timeout );

                        CookieUtils.setCookie( ServletActionContext.getResponse(), domain,
                                               SecuritySystemConstants.REMEMBER_ME_KEY, authkey.getKey(),
                                               webappContext, timeout );
                    }
                    catch ( KeyManagerException e )
                    {
                        getLogger().warn( "Unable to set remember me cookie." );

                    }
                }

                if ( securitySystem.getPolicy().getSingleSignOnSettings().isEnabled() )
                {
                    try
                    {
                        int timeout = securitySystem.getPolicy().getSingleSignOnSettings().getCookieTimeout();
                        // String ssoDomain = securitySystem.getPolicy().getSingleSignOnSettings().getCookieDomain();
                        KeyManager keyManager = securitySystem.getKeyManager();
                        AuthenticationKey authkey = keyManager.createKey( authdatasource.getPrincipal(),
                                                                          "Single Sign On Key", timeout );

                        CookieUtils.setCookie( ServletActionContext.getResponse(), domain,
                                               SecuritySystemConstants.SINGLE_SIGN_ON_KEY, authkey.getKey(), "/",
                                               CookieUtils.SESSION_COOKIE );
                    }
                    catch ( KeyManagerException e )
                    {
                        getLogger().warn( "Unable to set single sign on cookie." );

                    }

                }

                if ( securitySession.getUser().isPasswordChangeRequired() )
                {
                    return PASSWORD_CHANGE;
                }

                return LOGIN_SUCCESS;
            }
            else
            {
                getLogger().debug(
                                   "Login Action failed against principal : "
                                       + securitySession.getAuthenticationResult().getPrincipal(),
                                   securitySession.getAuthenticationResult().getException() );
                addActionError( "Authentication failed" );
                return ERROR;
            }
        }
        catch ( AuthenticationException ae )
        {
            addActionError( ae.getMessage() );
            return ERROR;
        }
        catch ( UserNotFoundException ue )
        {
            addActionError( ue.getMessage() );
            return ERROR;
        }
        catch ( AccountLockedException e )
        {
            addActionError( "Your Account is Locked." );
            return ACCOUNT_LOCKED;
        }
    }
}
