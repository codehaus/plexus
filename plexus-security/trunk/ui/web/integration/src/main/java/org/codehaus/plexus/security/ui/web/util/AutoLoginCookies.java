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
import org.codehaus.plexus.security.policy.CookieSettings;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * Cookie key for the Remember Me functionality.
     */
    private static final String REMEMBER_ME_KEY = "rbkRememberMe";

    /**
     * Cookie key for the signon cookie.
     */
    private static final String SIGNON_KEY = "rbkSignon";

    public void initialize()
        throws InitializationException
    {
        rememberMeEnabled = securitySystem.getPolicy().getRememberMeCookieSettings().isEnabled();
    }

    public AuthenticationKey getRememberMeKey()
    {
        if ( !rememberMeEnabled )
        {
            return null;
        }

        Cookie rememberMeCookie =
            getCookie( ServletActionContext.getRequest(), REMEMBER_ME_KEY );

        if ( rememberMeCookie == null )
        {
            getLogger().debug( "Remember Me Cookie Not Found: " + REMEMBER_ME_KEY );
            return null;
        }

        // Found user with a remember me key.
        String providedKey = rememberMeCookie.getValue();

        getLogger().info( "Found remember me cookie : " + providedKey );

        return findAuthKey( REMEMBER_ME_KEY, providedKey, getDomain(), getWebappContext() );
    }

    public void setRememberMe( String principal )
    {
        if ( !rememberMeEnabled )
        {
            return;
        }

        try
        {
            int timeout = securitySystem.getPolicy().getRememberMeCookieSettings().getCookieTimeout();
            KeyManager keyManager = securitySystem.getKeyManager();
            AuthenticationKey authkey = keyManager.createKey( principal, "Remember Me Key", timeout );

            Cookie cookie = new Cookie( REMEMBER_ME_KEY, authkey.getKey() );
            if ( timeout > 0 )
            {
                cookie.setMaxAge( timeout );
            }
            cookie.setDomain( getDomain() );
            cookie.setPath( getWebappContext() );
            ServletActionContext.getResponse().addCookie( cookie );

        }
        catch ( KeyManagerException e )
        {
            getLogger().warn( "Unable to set remember me cookie." );
        }
    }

    public void removeRememberMe()
    {
        removeCookie( ServletActionContext.getResponse(), REMEMBER_ME_KEY, getDomain(),
                      getWebappContext() );
    }

    public AuthenticationKey getSignonKey()
    {
        Cookie ssoCookie = getCookie( ServletActionContext.getRequest(), SIGNON_KEY );

        if ( ssoCookie == null )
        {
            getLogger().debug( "Single Sign On Cookie Not Found: " + SIGNON_KEY );
            return null;
        }

        // Found user with a single sign on key.

        String providedKey = ssoCookie.getValue();

        getLogger().info( "Found sso cookie : " + providedKey );

        CookieSettings settings = securitySystem.getPolicy().getSignonCookieSettings();
        return findAuthKey( SIGNON_KEY, providedKey, settings.getDomain(),
                            settings.getPath() );
    }

    public void setSingleSignon( String principal )
    {
        try
        {
            int timeout = securitySystem.getPolicy().getSignonCookieSettings().getCookieTimeout();
            KeyManager keyManager = securitySystem.getKeyManager();
            AuthenticationKey authkey = keyManager.createKey( principal, "Signon Session Key", timeout );

            /* The path must remain as "/" in order for SSO to work on installations where the only
             * all of the servers are installed into the same web container but under different 
             * web contexts.
             */
            Cookie cookie = new Cookie( SIGNON_KEY, authkey.getKey() );
            cookie.setDomain( getDomain() );
            cookie.setPath( "/" );
            ServletActionContext.getResponse().addCookie( cookie );

        }
        catch ( KeyManagerException e )
        {
            getLogger().warn( "Unable to set single sign on cookie." );

        }
    }

    public void removeSingleSignon()
    {
        removeCookie( ServletActionContext.getResponse(), SIGNON_KEY, getDomain(),
                      "/" );
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

    private AuthenticationKey findAuthKey( String cookieName, String providedKey, String domain, String path )
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
            removeCookie( ServletActionContext.getResponse(), cookieName, domain, path );
        }
        catch ( KeyManagerException e )
        {
            getLogger().error( "KeyManagerException: " + e.getMessage(), e );
        }

        return null;
    }

    private static Cookie getCookie( HttpServletRequest request, String name )
    {
        Cookie[] cookies = request.getCookies();

        Cookie cookie = null;
        if ( cookies != null && !StringUtils.isEmpty( name ) )
        {
            for ( int i = 0; i < cookies.length && cookie == null; i++ )
            {
                if ( StringUtils.equals( name, cookies[i].getName() ) )
                {
                    cookie = cookies[i];
                }
            }
        }

        return cookie;
    }

    private static void removeCookie( HttpServletResponse response, String cookieName, String domain, String path )
    {
        Cookie cookie = new Cookie( cookieName, "" );
        cookie.setMaxAge( 0 );
        cookie.setDomain( domain );
        cookie.setPath( path );
        response.addCookie( cookie );
    }
}
