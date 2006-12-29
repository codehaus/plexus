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
import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.security.ui.web.util.AutoLoginCookies;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

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
            return invocation.invoke();
        }

        if ( autologinCookies.isRememberMeEnabled() )
        {
            AuthenticationKey authkey = autologinCookies.getRememberMeKey();

            if ( authkey != null )
            {
                String result = performLogin( authkey );

                if ( StringUtils.isNotEmpty( result ) )
                {
                    return result;
                }
            }
        }

        if ( autologinCookies.isSingleSignonEnabled() )
        {
            AuthenticationKey authkey = autologinCookies.getSingleSignonKey();

            if ( authkey != null )
            {
                String result = performLogin( authkey );

                if ( StringUtils.isNotEmpty( result ) )
                {
                    return result;
                }
            }
        }

        return invocation.invoke();
    }

    private String performLogin( AuthenticationKey authkey )
    {
        getHttpSession().removeAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY );
        getLogger().debug( "Removing session:" + SecuritySystemConstants.SECURITY_SESSION_KEY );

        try
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
                getLogger().info( "Login Action failed against principal : " +
                    securitySession.getAuthenticationResult().getPrincipal(),
                                  securitySession.getAuthenticationResult().getException() );
            }
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
        if ( securitySession == null )
        {
            throw new NullPointerException( "securitySession must not be null" );
        }

        HttpSession session = getHttpSession();
        session.setAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession );
        getLogger().debug(
            "Setting session:" + SecuritySystemConstants.SECURITY_SESSION_KEY + " to " + securitySession );

        if ( securitySession.getUser() != null )
        {
            Object principal = securitySession.getUser().getPrincipal();
            if ( principal != null )
            {
                autologinCookies.setSingleSignon( principal.toString() );
            }
        }
    }

    private HttpSession getHttpSession()
    {
        return ServletActionContext.getRequest().getSession( true );
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
