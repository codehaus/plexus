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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.security.ui.web.filter.authentication.AbstractHttpAuthenticationFilter;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthentication;
import org.codehaus.plexus.security.ui.web.filter.authentication.HttpAuthenticationException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpBasicAuthenticationFilter 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class HttpBasicAuthenticationFilter
    extends AbstractHttpAuthenticationFilter
{
    private HttpBasicAuthentication httpAuthentication;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        super.init( filterConfig );

        try
        {
            httpAuthentication = (HttpBasicAuthentication) getContainer().lookup( HttpAuthentication.ROLE, "basic" );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup the HttpBasicAuthentication component.", e );
        }
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

        try
        {
            httpAuthentication.authenticate( httpRequest, httpResponse );
        }
        catch ( HttpAuthenticationException e )
        {
            HttpBasicAuthentication httpauthn = new HttpBasicAuthentication();
            httpauthn.challenge( httpRequest, httpResponse, getRealmName(), e );
            return;
        }

        chain.doFilter( request, response );
    }
}
