package org.codehaus.plexus.rememberme;

/* Copyright 2004, 2005 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.codec.digest.DigestUtils;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.Authentication;
import org.codehaus.plexus.security.DefaultAuthentication;
import org.codehaus.plexus.security.User;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.StringUtils;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Identifies previously remembered users by a Base-64 encoded cookie.
 * 
 * <p>
 * This implementation does not rely on an external database, so is attractive
 * for simple applications. The cookie will be valid for a specific period
 * from the date of the last {@link #loginSuccess(HttpServletRequest,
 * HttpServletResponse, Authentication)}. As per the interface contract, this
 * method will only be called when the principal completes a successful
 * interactive authentication. As such the time period commences from the last
 * authentication attempt where they furnished credentials - not the time
 * period they last logged in via remember-me. The implementation will only
 * send a remember-me token if the parameter defined by {@link
 * #setParameter(String)} is present.
 * </p>
 * 
 * <p>
 * The cookie encoded by this implementation adopts the following form:
 * </p>
 * 
 * <p>
 * <code> username + ":" + expiryTime + ":" + Md5Hex(username + ":" +
 * expiryTime + ":" + password + ":" + key) </code>.
 * </p>
 * 
 * <p>
 * As such, if the user changes their password any remember-me token will be
 * invalidated. Equally, the system administrator may invalidate every
 * remember-me token on issue by changing the key. This provides some
 * reasonable approaches to recovering from a remember-me token being left on
 * a public machine (eg kiosk system, Internet cafe etc). Most importantly, at
 * no time is the user's password ever sent to the user agent, providing an
 * important security safeguard. Unfortunately the username is necessary in
 * this implementation (as we do not want to rely on a database for
 * remember-me services) and as such high security applications should be
 * aware of this occasionally undesired disclosure of a valid username.
 * </p>
 * 
 * <p>
 * This is a basic remember-me implementation which is suitable for many
 * applications. However, we recommend a database-based implementation if you
 * require a more secure remember-me approach.
 * </p>
 * 
 * <p>
 * By default the tokens will be valid for 14 days from the last successful
 * authentication attempt. This can be changed using {@link
 * #setTokenValiditySeconds(int)}.
 * </p>
 *
 * @author Ben Alex
 * @version $Id$
 *
 * From acegisecurity with modifications
 */
public abstract class AbstractRememberMeServices
    extends AbstractLogEnabled
    implements RememberMeServices
{
    public static final String REMEMBER_ME_COOKIE_KEY = "PLEXUS_REMEMBER_ME_COOKIE";

    private String key;

    private long tokenValiditySeconds = 2592000; // 30 days

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public void setTokenValiditySeconds( long tokenValiditySeconds )
    {
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    public long getTokenValiditySeconds()
    {
        return tokenValiditySeconds;
    }

    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();

        if ( cookies == null || cookies.length == 0 )
        {
            return null;
        }

        for ( int i = 0; i < cookies.length; i++ )
        {
            if ( REMEMBER_ME_COOKIE_KEY.equals( cookies[i].getName() ) )
            {
                String cookieValue = cookies[i].getValue();

                if ( Base64.isArrayByteBase64( cookieValue.getBytes() ) )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "Remember-me cookie detected" );
                    }

                    // Decode token from Base64
                    // format of token is:  
                    //     username + ":" + expiryTime + ":" + Md5Hex(username + ":" + expiryTime + ":" + password + ":" + key)
                    String cookieAsPlainText = new String( Base64.decodeBase64( cookieValue.getBytes() ) );
                    String[] cookieTokens = StringUtils.split( cookieAsPlainText, ":" );

                    if ( cookieTokens.length == 3 )
                    {
                        long tokenExpiryTime;

                        try
                        {
                            tokenExpiryTime = new Long( cookieTokens[1] ).longValue();
                        }
                        catch ( NumberFormatException nfe )
                        {
                            cancelCookie( request, response,
                                "Cookie token[1] did not contain a valid number (contained '"
                                + cookieTokens[1] + "')" );

                            return null;
                        }

                        // Check it has not expired
                        if ( tokenExpiryTime < System.currentTimeMillis() )
                        {
                            cancelCookie( request, response,
                                "Cookie token[1] has expired (expired on '"
                                + new Date( tokenExpiryTime )
                                + "'; current time is '" + new Date() + "')" );

                            return null;
                        }

                        // Check the user exists
                        // Defer lookup until after expiry time checked, to possibly avoid expensive lookup
                        User user;

                        try
                        {
                            user = getUserByUserName( cookieTokens[0] );
                        }
                        catch ( UsernameNotFoundException notFound )
                        {
                            cancelCookie( request, response,
                                "Cookie token[0] contained username '"
                                + cookieTokens[0] + "' but was not found" );

                            return null;
                        }

                        // Immediately reject if the user is not allowed to login
                        if ( !user.isAccountNonExpired()
                            || !user.isPasswordNonExpired()
                            || !user.isEnabled() )
                        {
                            cancelCookie( request, response,
                                "Cookie token[0] contained username '"
                                + cookieTokens[0]
                                + "' but account has expired, credentials have expired, or user is disabled" );

                            return null;
                        }

                        // Check signature of token matches remaining details
                        // Must do this after user lookup, as we need the DAO-derived password
                        // If efficiency was a major issue, just add in a UserCache implementation,
                        // but recall this method is usually only called one per HttpSession
                        // (as if the token is valid, it will cause SecurityContextHolder population, whilst
                        // if invalid, will cause the cookie to be cancelled)
                        String expectedTokenSignature = DigestUtils.md5Hex( user
                                .getUsername() + ":" + tokenExpiryTime + ":"
                                + user.getPassword() + ":" + this.key );

                        if ( !expectedTokenSignature.equals( cookieTokens[2] ) )
                        {
                            cancelCookie( request, response,
                                "Cookie token[2] contained signature '"
                                + cookieTokens[2] + "' but expected '"
                                + expectedTokenSignature + "'" );

                            return null;
                        }

                        // By this stage we have a valid token
                        if ( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "Remember-me cookie accepted" );
                        }

                        Authentication auth = new DefaultAuthentication();
                        auth.setAuthenticated( true );
                        auth.setUser( user );
                        return auth;
                    }
                    else
                    {
                        cancelCookie( request, response,
                            "Cookie token did not contain 3 tokens; decoded value was '"
                            + cookieAsPlainText + "'" );

                        return null;
                    }
                }
                else
                {
                    cancelCookie( request, response,
                        "Cookie token was not Base64 encoded; value was '"
                        + cookieValue + "'" );

                    return null;
                }
            }
        }

        return null;
    }

    public void loginFail( HttpServletRequest request, HttpServletResponse response )
    {
        cancelCookie( request, response, "Interactive authentication attempt was unsuccessful" );
    }

    public void loginSuccess( HttpServletRequest request, HttpServletResponse response,
        Authentication successfulAuthentication )
    {
        // Determine username and password, ensuring empty strings
        if ( successfulAuthentication.getUser() == null )
        {
            throw new IllegalArgumentException( "Principal can't be null." );
        }

        String username;
        String password;

        if ( successfulAuthentication.getUser() instanceof User )
        {
            username = successfulAuthentication.getUser().getUsername();
            password = successfulAuthentication.getUser().getPassword();
        }
        else
        {
            throw new IllegalArgumentException( "Principal isn't a User object." );
        }

        if ( StringUtils.isEmpty( username ) )
        {
            throw new IllegalArgumentException( "username can't be null or empty." );
        }
        if ( StringUtils.isEmpty( password ) )
        {
            password = "unknown";
        }

        long expiryTime = System.currentTimeMillis() + ( tokenValiditySeconds * 1000 );

        // construct token to put in cookie; format is:
        //     username + ":" + expiryTime + ":" + Md5Hex(username + ":" + expiryTime + ":" + password + ":" + key)
        String signatureValue = new String( DigestUtils.md5Hex( username + ":"
            + expiryTime + ":" + password + ":" + key ) );
        String tokenValue = username + ":" + expiryTime + ":" + signatureValue;
        String tokenValueBase64 = new String( Base64.encodeBase64( tokenValue.getBytes() ) );
        response.addCookie( makeValidCookie( expiryTime, tokenValueBase64 ) );

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Added remember-me cookie for user '" + username
                + "', expiry: '" + new Date( expiryTime ) + "'" );
        }
    }

    protected Cookie makeCancelCookie()
    {
        Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_KEY, null);

        cookie.setMaxAge( 0 );

        return cookie;
    }

    protected Cookie makeValidCookie( long expiryTime, String tokenValueBase64 )
    {
        Cookie cookie = new Cookie( REMEMBER_ME_COOKIE_KEY, tokenValueBase64 );

        cookie.setMaxAge( 60 * 60 * 24 * 365 * 5 ); // 5 years

        return cookie;
    }

    private void cancelCookie( HttpServletRequest request, HttpServletResponse response, String reasonForLog )
    {
        if ( ( reasonForLog != null ) && getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Cancelling cookie for reason: " + reasonForLog );
        }

        response.addCookie( makeCancelCookie() );
    }

    protected abstract User getUserByUserName( String username )
        throws UsernameNotFoundException;
}
