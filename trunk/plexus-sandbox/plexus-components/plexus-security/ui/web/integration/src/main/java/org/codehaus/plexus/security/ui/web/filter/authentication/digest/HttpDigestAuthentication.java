package org.codehaus.plexus.security.ui.web.filter.authentication.digest;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.authentication.AuthenticationDataSource;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticator;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticationException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpDigestAuthentication methods for working with <a href="http://www.faqs.org/rfcs/rfc2617.html">RFC 2617 HTTP Authentication</a>.
 * 
 * @plexus.component role="org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticator"
 *                   role-hint="basic"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class HttpDigestAuthentication
    extends HttpAuthenticator
{
    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    /**
     * @plexus.configuration default-value="300"
     */
    private int nonceLifetimeSeconds = 300;

    /**
     * NOTE: Must be alphanumeric.
     * 
     * @plexus.configuration default-value="OrycteropusAfer"
     */
    private String digestKey;

    private String realm;
    
    public String getId()
    {
        return HttpDigestAuthentication.class.getName();
    }

    public AuthenticationResult getAuthenticationResult( HttpServletRequest request, HttpServletResponse response,
                                             String defaultPrincipal )
        throws AuthenticationException
    {
        if ( isAlreadyAuthenticated() )
        {
            return getSecuritySession().getAuthenticationResult();
        }

        AuthenticationDataSource authDataSource = new AuthenticationDataSource( null, null );
        String authHeader = request.getHeader( "Authorization" );

        if ( ( authHeader != null ) && authHeader.startsWith( "Digest " ) )
        {
            String rawDigestHeader = authHeader.substring( 7 );

            HttpDigestHeader digestHeader = new HttpDigestHeader();
            digestHeader.parseClientHeader( rawDigestHeader, getRealm(), digestKey );

            // Lookup password for presented username
            User user = findUser( digestHeader.username, defaultPrincipal );

            String serverSideHash = generateDigestHash( digestHeader, user.getPassword(), request.getMethod() );

            if ( !StringUtils.equals( serverSideHash, digestHeader.response ) )
            {
                throw new HttpAuthenticationException( "Digest response was invalid." );
            }
            
            authDataSource.setDefaultPrincipal( user.getUsername() );
        }
        else
        {
            authDataSource.setDefaultPrincipal( defaultPrincipal );
        }

        return super.authenticate( authDataSource );
    }

    public User findUser( String username, String defaultPrincipal ) throws HttpAuthenticationException
    {
        UserNotFoundException primaryException = null;
        
        try
        {
            return userManager.findUser( username );
        }
        catch ( UserNotFoundException e )
        {
            primaryException = e;
            // Fall Thru
        }
        
        if ( StringUtils.isNotEmpty( defaultPrincipal ) )
        {
            try
            {
                return userManager.findUser( defaultPrincipal );
            }
            catch ( UserNotFoundException e )
            {
                String msg = "Unable to find primary user '" + username + 
                             "' and default principal '" + defaultPrincipal + "'.";
                getLogger().error( msg, e );
                throw new HttpAuthenticationException( msg, e );
            }
        }
        else
        {
            String msg = "Unable to find primary user '" + username + 
                         "' (no default principal backup)";
            getLogger().error( msg, primaryException );
            throw new HttpAuthenticationException(msg, primaryException);
        }
    }

    /**
     * Issue HTTP Digest Authentication Challenge  
     *
     * @param request the request to use.
     * @param response the response to use.
     * @param realmName the realm name to state.
     * @param exception the exception to base the message off of.
     * @throws IOException if there was a problem with the {@link HttpServletResponse#sendError(int, String)} call.
     */
    public void challenge( HttpServletRequest request, HttpServletResponse response, String realmName,
                           AuthenticationException exception )
        throws IOException
    {
        // The Challenge Header
        StringBuffer authHeader = new StringBuffer();
        authHeader.append( "Digest " );
        // [REQUIRED] The name to appear in the dialog box to the user.
        authHeader.append( "realm=\"" ).append( realmName ).append( "\"" );
        // [OPTIONAL] We do not use the optional 'domain' header.
        // authHeader.append( "domain=\"" ).append( domain ).append( "\"" );
        // [REQUIRED] Nonce specification.
        authHeader.append( ", nonce=\"" );
        long timestamp = System.currentTimeMillis() + ( nonceLifetimeSeconds * 1000 );
        // Not using ETag from RFC 2617 intentionally.
        String hraw = String.valueOf( timestamp ) + ":" + digestKey;
        String rawnonce = String.valueOf( timestamp ) + ":" + Digest.md5Hex( hraw );
        authHeader.append( Base64.encodeBase64( rawnonce.getBytes() ) );
        authHeader.append( "\"" );
        // [REQUIRED] The RFC 2617 Quality of Protection.
        // MSIE Appears to only support 'auth'
        // Do not use 'opaque' here. (Your MSIE users will have issues)
        authHeader.append( ", qop=\"auth\"" );
        // [BROKEN] since we force the 'auth' qop we cannot use the opaque option.
        // authHeader.append( ", opaque=\"").append(opaqueString).append("\"");

        // [OPTIONAL] Use of the stale option is reserved for expired nonce strings.
        if ( exception instanceof NonceExpirationException )
        {
            authHeader.append( ", stale=\"true\"" );
        }

        // [OPTIONAL] We do not use the optional Algorithm header.
        // authHeader.append( ", algorithm=\"MD5\"");

        response.addHeader( "WWW-Authenticate", authHeader.toString() );
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage() );
    }

    private String generateDigestHash( HttpDigestHeader digestHeader, String password, String httpMethod )
    {
        String a1 = Digest.md5Hex( digestHeader.username + ":" + realm + ":" + password );
        String a2 = Digest.md5Hex( httpMethod + ":" + digestHeader.uri );

        String digest;

        if ( StringUtils.isEmpty( digestHeader.qop ) )
        {
            digest = a1 + ":" + digestHeader.nonce + ":" + a2;
        }
        else if ( StringUtils.equals( "auth", digestHeader.qop ) )
        {
            digest = a1 + ":" + digestHeader.nonce + ":" + digestHeader.nc + ":" + digestHeader.cnonce + ":"
                + digestHeader.qop + ":" + a2;
        }
        else
        {
            throw new IllegalStateException( "Http Digest Parameter [qop] with value of [" + digestHeader.qop
                + "] is unsupported." );
        }

        return Digest.md5Hex( digest );
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm( String realm )
    {
        this.realm = realm;
    }

}
