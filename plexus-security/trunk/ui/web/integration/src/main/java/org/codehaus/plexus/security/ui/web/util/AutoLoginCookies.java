package org.codehaus.plexus.security.ui.web.util;

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
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.system.SecuritySystemConstants;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.Cookie;

/**
 * AutoLoginCookies
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.ui.web.util.AutoLoginCookies"
 */
public class AutoLoginCookies
    extends AbstractLogEnabled
    implements Initializable
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private boolean rememberMeEnabled;

    private boolean singleSignonEnabled;

    public AutoLoginCookies()
    {
    }

    public void initialize()
        throws InitializationException
    {
        rememberMeEnabled = securitySystem.getPolicy().getRememberMeSettings().isEnabled();
        singleSignonEnabled = securitySystem.getPolicy().getSingleSignOnSettings().isEnabled();
    }

    public AuthenticationKey getRememberMeKey()
    {
        if ( !rememberMeEnabled )
        {
            return null;
        }

        Cookie rememberMeCookie =
            CookieUtils.getCookie( ServletActionContext.getRequest(), SecuritySystemConstants.REMEMBER_ME_KEY );

        if ( rememberMeCookie == null )
        {
            getLogger().debug( "Remember Me Cookie Not Found: " + SecuritySystemConstants.REMEMBER_ME_KEY );
            return null;
        }

        // Found user with a remember me key.
        String providedKey = rememberMeCookie.getValue();

        getLogger().info( "Found remember me cookie : " + providedKey );

        return findAuthKey( SecuritySystemConstants.REMEMBER_ME_KEY, providedKey, getDomain(), getWebappContext() );
    }

    public void setRememberMe( String principal )
    {
        if ( !rememberMeEnabled )
        {
            return;
        }

        try
        {
            int timeout = securitySystem.getPolicy().getRememberMeSettings().getCookieTimeout();
            KeyManager keyManager = securitySystem.getKeyManager();
            AuthenticationKey authkey = keyManager.createKey( principal, "Remember Me Key", timeout );

            CookieUtils.setCookie( ServletActionContext.getResponse(), getDomain(),
                                   SecuritySystemConstants.REMEMBER_ME_KEY, authkey.getKey(), getWebappContext(),
                                   timeout );
        }
        catch ( KeyManagerException e )
        {
            getLogger().warn( "Unable to set remember me cookie." );
        }
    }

    public void removeRememberMe()
    {
        CookieUtils.setCookie( ServletActionContext.getResponse(), getDomain(), SecuritySystemConstants.REMEMBER_ME_KEY,
                               "-", getWebappContext(), 0 );
    }

    public AuthenticationKey getSingleSignonKey()
    {
        if ( !singleSignonEnabled )
        {
            return null;
        }

        Cookie ssoCookie =
            CookieUtils.getCookie( ServletActionContext.getRequest(), SecuritySystemConstants.SINGLE_SIGN_ON_KEY );

        if ( ssoCookie == null )
        {
            getLogger().debug( "Single Sign On Cookie Not Found: " + SecuritySystemConstants.SINGLE_SIGN_ON_KEY );
            return null;
        }

        // Found user with a single sign on key.

        String providedKey = ssoCookie.getValue();

        getLogger().info( "Found sso cookie : " + providedKey );

        return findAuthKey( SecuritySystemConstants.SINGLE_SIGN_ON_KEY, providedKey, getDomain(), "/" );
    }

    public void setSingleSignon( String principal )
    {
        if ( !singleSignonEnabled )
        {
            return;
        }

        try
        {
            int timeout = securitySystem.getPolicy().getSingleSignOnSettings().getCookieTimeout();
            KeyManager keyManager = securitySystem.getKeyManager();
            AuthenticationKey authkey = keyManager.createKey( principal, "Single Sign On Key", timeout );

            /* The path must remain as "/" in order for SSO to work on installations where the only
             * all of the servers are installed into the same web container but under different 
             * web contexts.
             */
            CookieUtils.setCookie( ServletActionContext.getResponse(), getDomain(),
                                   SecuritySystemConstants.SINGLE_SIGN_ON_KEY, authkey.getKey(), "/",
                                   CookieUtils.SESSION_COOKIE );
        }
        catch ( KeyManagerException e )
        {
            getLogger().warn( "Unable to set single sign on cookie." );

        }
    }

    public void removeSingleSignon()
    {
        CookieUtils.setCookie( ServletActionContext.getResponse(), getDomain(),
                               SecuritySystemConstants.SINGLE_SIGN_ON_KEY, "-", "/", 0 );
    }

    /**
     * <p/>
     * Initialize the Domain Parameter of all cookies.
     * </p>
     * <p/>
     * <p/>
     * The domain parameter is used to specify which hosts (single host, or multiple hosts) within a domain
     * can access the cookie.
     * </p>
     * <p/>
     * <p/>
     * If you have a web configuration with multiple servers within the same domain such like...
     * </p>
     * <p/>
     * <ul>
     * <li>plexus.codehaus.org</li>
     * <li>svn.codehaus.org</li>
     * <li>www.codehaus.org</li>
     * <li>fisheye.codehaus.org</li>
     * </ul>
     * <p/>
     * <p/>
     * ... and you want all of those hosts to be able to access the domain, then the following string <b>must</b>
     * be passed into the domain parameter.
     * </p>
     * <p/>
     * <ul>
     * <li>".codehaus.org"</li>
     * </ul>
     * <p/>
     * <p/>
     * Per RFC 2109, when using the domain parameter, the use of a "." (dot) character at the start of the
     * domain parameter indicates a desire to match multiple machines in a domain.  Implementations found in the
     * popular browsers, proxies, and caches, out an extra restriction on this value in that it must contain at least
     * 2 "." (dot) characters to be treated as a multi-machine domain parameter.
     * </p>
     * <p/>
     * <p/>
     * Examples:
     * </p>
     * <p/>
     * <pre>
     *    "plexus.codehaus.org"   - (single host) matches only on "plexus.codehaus.org"
     *    ".codehaus.org"         - (multi host) matches on "*.codehaus.org"
     *    ".svn.codehaus.org"     - (multi host) matches on "*.svn.codehaus.org"
     *    "localhost"             - (single host) matches only on "localhost"
     *    ".com"                  - (single host) matches only on "com"
     * </pre>
     * <p/>
     * <p/>
     * It is important to note that all browser based cookie implementations impose a security check to ensure that
     * only members of the domain that tries to set set the cookie can issue a cookie for that domain.
     * For example, a server called plexus.codehaus.org cannot set a cookie for the domain google.com.
     * </p>
     * <p/>
     * <p/>
     * This implementation utilizes the HttpServletRequest.getServerName() so that various web techniques such as
     * aliases, virtual hosts, clusters, load balancing, proxying, etc.. will work correctly.
     * </p>
     */
    public String getDomain()
    {
        // Calculate the domain.
        String domain = "." + ServletActionContext.getRequest().getServerName();

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

        return domain;
    }

    public String getWebappContext()
    {
        // Calculate the webapp context.
        String webappContext = ServletActionContext.getRequest().getContextPath();

        if ( StringUtils.isEmpty( webappContext ) )
        {
            // Still empty?  means you are a root context.
            webappContext = "/";
        }

        return webappContext;
    }

    public boolean isRememberMeEnabled()
    {
        return rememberMeEnabled;
    }

    public boolean isSingleSignonEnabled()
    {
        return singleSignonEnabled;
    }

    private AuthenticationKey findAuthKey( String cookieName, String providedKey, String domain, String webcontext )
    {
        try
        {
            AuthenticationKey authkey = securitySystem.getKeyManager().findKey( providedKey );

            getLogger().debug( "Found AuthKey: " + authkey );

            return authkey;
        }
        catch ( KeyNotFoundException e )
        {
            getLogger().info( "Invalid AuthenticationKey " + providedKey + " submitted. Invalidating cookie." );

            // Invalid Cookie.  Remove it.
            CookieUtils.setCookie( ServletActionContext.getResponse(), domain, cookieName, "-", webcontext, 0 );
        }
        catch ( KeyManagerException e )
        {
            getLogger().error( "KeyManagerException: " + e.getMessage(), e );
        }

        return null;
    }
}
