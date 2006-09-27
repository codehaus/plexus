package org.codehaus.plexus.security.ui.web.interceptor;

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
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.util.CookieUtils;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * AutoLoginInterceptor 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 *                   role-hint="pssAutoLoginInterceptor"
 */
public class AutoLoginInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{
    static final String PASSWORD_CHANGE = "must-change-password";

    static final String ACCOUNT_LOCKED = "security-login-locked";

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private boolean isRememberMeEnabled = false;

    private boolean isSingleSignOnEnabled = false;

    public void destroy()
    {
        // Ignore
    }

    public void init()
    {
        isRememberMeEnabled = securitySystem.getPolicy().getRememberMeSettings().isEnabled();
        isSingleSignOnEnabled = securitySystem.getPolicy().getSingleSignOnSettings().isEnabled();

        getLogger().info( "Remember Me (enabled) : " + isRememberMeEnabled );
        getLogger().info( "Single Sign On (enabled) : " + isSingleSignOnEnabled );
    }

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        SecuritySession securitySession = getSecuritySession();

        if ( ( securitySession != null ) && securitySession.isAuthenticated() )
        {
            // User already authenticated.
            getLogger().info( "User already authenticated." );
            return invocation.invoke();
        }

        if ( isRememberMeEnabled )
        {
            Cookie rememberMeCookie = CookieUtils.getCookie( ServletActionContext.getRequest(),
                                                             SecuritySystemConstants.REMEMBER_ME_KEY );

            if ( rememberMeCookie != null )
            {
                // Found user with a remember me key.
                String providedKey = rememberMeCookie.getValue();

                getLogger().info( "Found remember me cookie : " + providedKey );

                String result = populateAuthTokens( SecuritySystemConstants.REMEMBER_ME_KEY, providedKey );

                if ( StringUtils.isNotEmpty( result ) )
                {
                    return result;
                }
            }
            else
            {
                getLogger().info( "Cookie Not Found: Remember Me Cookie: " + SecuritySystemConstants.REMEMBER_ME_KEY );
            }
        }

        if ( isSingleSignOnEnabled )
        {
            Cookie ssoCookie = CookieUtils.getCookie( ServletActionContext.getRequest(),
                                                      SecuritySystemConstants.SINGLE_SIGN_ON_KEY );

            if ( ssoCookie != null )
            {
                // Found user with a single sign on key.

                String providedKey = ssoCookie.getValue();

                getLogger().info( "Found sso cookie : " + providedKey );

                String result = populateAuthTokens( SecuritySystemConstants.SINGLE_SIGN_ON_KEY, providedKey );

                if ( StringUtils.isNotEmpty( result ) )
                {
                    return result;
                }
            }
            else
            {
                getLogger().info(
                                  "Cookie Not Found: Single Sign On Cookie: "
                                      + SecuritySystemConstants.SINGLE_SIGN_ON_KEY );
            }
        }

        return invocation.invoke();
    }

    private String populateAuthTokens( String cookieName, String providedKey )
    {
        setAuthTokens( null );

        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( providedKey );
            if ( authkey == null )
            {
                getLogger().info( "Authkey not found - " + providedKey );

                // Invalid Cookie.  Remove it.
                CookieUtils.invalidateCookie( ServletActionContext.getRequest(), ServletActionContext.getResponse(),
                                              cookieName );
            }
            else
            {
                getLogger().info( "Performing Login." );
                TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource();
                authsource.setPrincipal( authkey.getForPrincipal() );
                authsource.setToken( authkey.getKey() );

                SecuritySession securitySession = securitySystem.authenticate( authsource );

                if ( securitySession.getAuthenticationResult().isAuthenticated() )
                {
                    getLogger().info( "Login success." );
                    // Success!  Create tokens.
                    setAuthTokens( securitySession );

                    if ( securitySession.getUser().isPasswordChangeRequired() )
                    {
                        return PASSWORD_CHANGE;
                    }
                }
                else
                {
                    getLogger().info(
                                      "Login Action failed against principal : "
                                          + securitySession.getAuthenticationResult().getPrincipal(),
                                      securitySession.getAuthenticationResult().getException() );
                }
            }

        }
        catch ( KeyNotFoundException e )
        {
            getLogger().info( "Key " + providedKey + " not found." );
        }
        catch ( KeyManagerException e )
        {
            getLogger().warn( "KeyManager error on " + providedKey + ".", e );
        }
        catch ( AccountLockedException e )
        {
            getLogger().info( "Account Locked : Username [" + e.getUser().getUsername() + "]", e );
            return ACCOUNT_LOCKED;
        }
        catch ( AuthenticationException e )
        {
            getLogger().info( "Authentication Exception.", e );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "User Not Found.", e );
        }

        return null;
    }

    private void setAuthTokens( SecuritySession securitySession )
    {
        HttpSession session = ServletActionContext.getRequest().getSession( true );
        session.setAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );
        getLogger().debug( "Setting session:" + SecuritySystemConstants.SECURITY_SESSION_KEY + " to " + securitySession );
    }

    private SecuritySession getSecuritySession()
    {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if ( session == null )
        {
            getLogger().debug( "No Security Session exists." );
            return null;
        }

        SecuritySession secSession = (SecuritySession) session
            .getAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY );
        getLogger().debug( "Returning Security Session: " + secSession );
        return secSession;
    }
}
