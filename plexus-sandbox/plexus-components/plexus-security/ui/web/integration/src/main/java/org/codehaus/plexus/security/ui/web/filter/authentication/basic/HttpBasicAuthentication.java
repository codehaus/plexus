package org.codehaus.plexus.security.ui.web.filter.authentication.basic;

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
import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticator;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpBasicAuthentication 
 *
 * @plexus.component role="org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticator"
 *                   role-hint="basic"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class HttpBasicAuthentication
    extends HttpAuthenticator
{
    
    public String getId()
    {
        return HttpBasicAuthentication.class.getName();
    }

    public AuthenticationResult getAuthenticationResult( HttpServletRequest request, HttpServletResponse response,
                                                         String defaultPrincipal )
        throws AuthenticationException, AccountLockedException, MustChangePasswordException
    {
        if ( isAlreadyAuthenticated() )
        {
            return getSecuritySession().getAuthenticationResult();
        }

        AuthenticationDataSource authDataSource;
        String header = request.getHeader( "Authorization" );

        if ( ( header != null ) && header.startsWith( "Basic " ) )
        {
            String base64Token = header.substring( 6 );
            String token = new String( Base64.decodeBase64( base64Token.getBytes() ) );

            String username = "";
            String password = "";
            int delim = token.indexOf( ':' );

            if ( delim != ( -1 ) )
            {
                username = token.substring( 0, delim );
                password = token.substring( delim + 1 );
            }

            authDataSource = new AuthenticationDataSource( username, password );
        }
        else
        {
            // Default Empty Source.
            authDataSource = new AuthenticationDataSource( null, null );
        }

        authDataSource.setDefaultPrincipal( defaultPrincipal );
        return super.authenticate( authDataSource );
    }

    /**
     * Return a HTTP 403 - Access Denied response.
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
        response.addHeader( "WWW-Authenticate", "Basic realm=\"" + realmName + "\"" );
        String message = "You must provide a username and password to access this resource.";
        if ( ( exception != null ) && StringUtils.isNotEmpty( exception.getMessage() ) )
        {
            message = exception.getMessage();
        }
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, message );
    }
}
