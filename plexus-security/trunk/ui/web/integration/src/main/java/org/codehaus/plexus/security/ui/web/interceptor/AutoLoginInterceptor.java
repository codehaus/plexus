package org.codehaus.plexus.security.ui.web.interceptor;

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

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.util.AutoLoginCookies;
import org.codehaus.plexus.security.user.UserNotFoundException;

import javax.servlet.http.HttpSession;

/**
 * AutoLoginInterceptor
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 * role-hint="pssAutoLoginInterceptor"
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

    /**
     * @plexus.requirement
     */
    private AutoLoginCookies autologinCookies;

    public void destroy()
    {
        // Ignore
    }

    public void init()
    {
        // Ignore
    }

    /**
     * @noinspection ProhibitedExceptionDeclared
     */
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        SecuritySession securitySession = getSecuritySession();

        if ( securitySession != null && securitySession.isAuthenticated() )
        {
            // User already authenticated.
            getLogger().debug( "User already authenticated." );

            checkCookieConsistency( securitySession );
        }
        else
        {
            AuthenticationKey authkey = autologinCookies.getSignonKey();

            if ( authkey != null )
            {
                try
                {
                    securitySession = checkAuthentication( authkey );

                    if ( securitySession != null && securitySession.isAuthenticated() )
                    {
                        checkCookieConsistency( securitySession );

                        if ( securitySession.getUser().isPasswordChangeRequired() )
                        {
                            return PASSWORD_CHANGE;
                        }
                    }
                    else
                    {
                        autologinCookies.removeSignonCookie();
                        autologinCookies.removeRememberMeCookie();
                    }
                }
                catch ( AccountLockedException e )
                {
                    getLogger().info( "Account Locked : Username [" + e.getUser().getUsername() + "]", e );
                    autologinCookies.removeSignonCookie();
                    autologinCookies.removeRememberMeCookie();
                    return ACCOUNT_LOCKED;
                }
            }
            else if ( autologinCookies.isRememberMeEnabled() )
            {
                authkey = autologinCookies.getRememberMeKey();

                if ( authkey != null )
                {
                    try
                    {
                        securitySession = checkAuthentication( authkey );

                        if ( securitySession != null && securitySession.isAuthenticated() )
                        {
                            if ( securitySession.getUser().isPasswordChangeRequired() )
                            {
                                return PASSWORD_CHANGE;
                            }
                        }
                        else
                        {
                            autologinCookies.removeRememberMeCookie();
                        }
                    }
                    catch ( AccountLockedException e )
                    {
                        getLogger().info( "Account Locked : Username [" + e.getUser().getUsername() + "]", e );
                        autologinCookies.removeRememberMeCookie();
                        return ACCOUNT_LOCKED;
                    }
                }
            }
        }

        return invocation.invoke();
    }

    private void checkCookieConsistency( SecuritySession securitySession )
    {
        String username = securitySession.getUser().getUsername();

        boolean failed = false;

        AuthenticationKey key = autologinCookies.getRememberMeKey();
        if ( key != null )
        {
            if ( !key.getForPrincipal().equals( username ) )
            {
                getLogger().debug( "Login invalidated: remember me cookie was for " + key.getForPrincipal() +
                    "; but session was for " + username );
                failed = true;
            }
        }

        if ( !failed )
        {
            key = autologinCookies.getSignonKey();
            if ( key != null )
            {
                if ( !key.getForPrincipal().equals( username ) )
                {
                    getLogger().debug( "Login invalidated: signon cookie was for " + key.getForPrincipal() +
                        "; but session was for " + username );
                    failed = true;
                }
            }
            else
            {
                getLogger().debug( "Login invalidated: signon cookie was removed" );
                failed = true;
            }
        }

        if ( failed )
        {
            removeCookiesAndSession();
        }
    }

    private SecuritySession checkAuthentication( AuthenticationKey authkey )
        throws AccountLockedException
    {
        SecuritySession securitySession = null;
        getLogger().debug( "Logging in with an authentication key: " + authkey.getForPrincipal() );
        TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource();
        authsource.setPrincipal( authkey.getForPrincipal() );
        authsource.setToken( authkey.getKey() );

        try
        {
            securitySession = securitySystem.authenticate( authsource );

            if ( securitySession.isAuthenticated() )
            {
                // TODO: this should not happen if there is a password change required,
                //  ... but that requires that we somehow secure the password change action
                // (by entering the old password - check if this already happens?)
                getLogger().debug( "Login success." );

                HttpSession session = ServletActionContext.getRequest().getSession( true );
                session.setAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );
                getLogger().debug(
                    "Setting session:" + SecuritySystemConstants.SECURITY_SESSION_KEY + " to " + securitySession );

                autologinCookies.setSignonCookie( authkey.getForPrincipal() );
            }
            else
            {
                AuthenticationResult result = securitySession.getAuthenticationResult();
                getLogger().info( "Login interceptor failed against principal : " + result.getPrincipal(),
                                  result.getException() );
            }

        }
        catch ( AuthenticationException e )
        {
            getLogger().info( "Authentication Exception.", e );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "User Not Found: " + authkey.getForPrincipal(), e );
        }
        return securitySession;
    }

    private void removeCookiesAndSession()
    {
        autologinCookies.removeRememberMeCookie();
        autologinCookies.removeSignonCookie();

        HttpSession session = ServletActionContext.getRequest().getSession();
        if ( session != null )
        {
            session.removeAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY );
        }
    }

    private SecuritySession getSecuritySession()
    {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if ( session == null )
        {
            getLogger().debug( "No HTTP Session exists." );
            return null;
        }

        SecuritySession secSession =
            (SecuritySession) session.getAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY );
        getLogger().debug( "Returning Security Session: " + secSession );
        return secSession;
    }
}
