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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.Authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Detects if there is no <code>Authentication</code> object in the
 * <code>Session</code>, and populates it with a remember-me
 * authentication token if a {@link
 * org.codehaus.plexus.rememberme.RememberMeServices} implementation so
 * requests.
 * 
 * <p>
 * Concrete <code>RememberMeServices</code> implementations will have their
 * {@link
 * org.codehaus.plexus.rememberme.RememberMeServices#autoLogin(HttpServletRequest,
 * HttpServletResponse)} method called by this filter. The
 * <code>Authentication</code> or <code>null</code> returned by that method
 * will be placed into the <code>Session</code>.
 * </p>
 * 
 * <p>
 * <b>Do not use this class directly.</b> Instead configure
 * <code>web.xml</code>.
 * </p>
 *
 * @author Ben Alex
 * @version $Id$
 */
public class RememberMeProcessingFilter
    extends AbstractLogEnabled
    implements Filter
{
    private RememberMeServices rememberMeServices = new NullRememberMeServices();

    public void setRememberMeServices( RememberMeServices rememberMeServices )
    {
        this.rememberMeServices = rememberMeServices;
    }

    public RememberMeServices getRememberMeServices()
    {
        return rememberMeServices;
    }

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        if ( !( request instanceof HttpServletRequest ) )
        {
            throw new ServletException( "Can only process HttpServletRequest" );
        }

        if ( !( response instanceof HttpServletResponse ) )
        {
            throw new ServletException( "Can only process HttpServletResponse" );
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession( true );
        Authentication authentication = (Authentication) session.getAttribute( "authentication" );

        if ( authentication == null )
        {
            Authentication rememberMeAuth = rememberMeServices.autoLogin( httpRequest, httpResponse );

            if( rememberMeAuth != null )
            {
                session.setAttribute( "authentication", rememberMeAuth );

                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "SecurityContextHolder populated with remember-me token: '"
                        + rememberMeAuth + "'");
                }
            }
        }
        else
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug(
                    "Session not populated with remember-me token, as it already contained: '"
                    + authentication + "'");
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Does nothing - we rely on IoC lifecycle services instead.
     */
    public void destroy() {}

    /**
     * Does nothing - we rely on IoC lifecycle services instead.
     *
     * @param ignored not used
     *
     */
    public void init(FilterConfig ignored) throws ServletException {}
}