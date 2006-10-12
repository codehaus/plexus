package org.codehaus.plexus.security.ui.web.filter.authorization;

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

import org.codehaus.plexus.security.authorization.AuthorizationException;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.filter.PlexusServletFilter;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * SimpleAuthorizationFilter 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class SimpleAuthorizationFilter
    extends PlexusServletFilter
{
    private String permission;

    private String resource;

    private String accessDeniedLocation;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        super.init( filterConfig );

        permission = filterConfig.getInitParameter( "permission" );
        resource = filterConfig.getInitParameter( "resource" );
        accessDeniedLocation = filterConfig.getInitParameter( "accessDeniedLocation" );

        if ( StringUtils.isEmpty( accessDeniedLocation ) )
        {
            throw new ServletException( "Missing parameter 'accessDeniedLocation' from "
                + SimpleAuthorizationFilter.class.getName() + " configuration." );
        }
    }

    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        SecuritySession securitySession = (SecuritySession) lookup( SecuritySession.ROLE );

        if ( securitySession == null )
        {
            getLogger().warn( "Security Session is null." );
            return;
        }

        SecuritySystem securitySystem = (SecuritySystem) lookup( SecuritySystem.ROLE );

        boolean isAuthorized = false;

        try
        {
            if ( StringUtils.isEmpty( resource ) )
            {
                isAuthorized = securitySystem.isAuthorized( securitySession, permission );
            }
            else
            {
                isAuthorized = securitySystem.isAuthorized( securitySession, permission, resource );
            }
            if ( isAuthorized )
            {
                chain.doFilter( request, response );
            }
            else
            {
                accessDenied( response );
            }
        }
        catch ( AuthorizationException e )
        {
            accessDenied( response );
        }
    }

    protected void accessDenied( ServletResponse response )
        throws IOException
    {
        String newlocation = accessDeniedLocation;

        if ( newlocation.indexOf( "?" ) == ( -1 ) )
        {
            newlocation += "?";
        }
        else
        {
            newlocation += "&";
        }
        newlocation += "resource=" + resource;

        ( (HttpServletResponse) response ).sendRedirect( newlocation );
    }

}
