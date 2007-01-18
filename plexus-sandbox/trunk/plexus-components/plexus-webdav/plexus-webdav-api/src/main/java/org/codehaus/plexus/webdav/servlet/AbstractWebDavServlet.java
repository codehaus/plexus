package org.codehaus.plexus.webdav.servlet;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.webdav.DavServerManager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AbstractWebDavServlet 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractWebDavServlet
    extends HttpServlet
{
    private boolean debug = true;

    protected DavServerManager davManager;

    public void destroy()
    {
        if ( davManager != null )
        {
            try
            {
                getPlexusContainer().release( davManager );
            }
            catch ( ServletException e )
            {
                log( "Unable to release DavServerManager.", e );
            }
            catch ( ComponentLifecycleException e )
            {
                log( "Unable to release DavServerManager.", e );
            }
        }
    }

    public DavServerManager getDavManager()
    {
        return davManager;
    }

    public String getServletInfo()
    {
        return "Plexus WebDAV Servlet";
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );

        ensureContainerSet( servletConfig );

        initComponents();
    }

    public void initComponents()
        throws ServletException
    {
        davManager = (DavServerManager) lookup( DavServerManager.ROLE );
    }

    /**
     * Perform any authentication steps here.
     * 
     * If authentication fails, it is the responsibility of the implementor to issue
     * the appropriate status codes and/or challenge back on the response object, then
     * return false on the overridden version of this method.
     * 
     * To effectively not have authentication, just implement this method and always
     * return true.
     * 
     * @param httpRequest the incoming http request.
     * @param httpResponse the outgoing http response.
     * @return true if user is authenticated, false if not.
     * @throws ServletException if there was a problem performing authencation.
     * @throws IOException if there was a problem obtaining credentials or issuing challenge.
     */
    public boolean isAuthenticated( DavServerRequest davRequest, HttpServletResponse httpResponse )
        throws ServletException, IOException
    {
        // Always return true. Effectively no Authentication done.
        return true;
    }

    /**
     * Perform any authorization steps here.
     * 
     * If authorization fails, it is the responsibility of the implementor to issue
     * the appropriate status codes and/or challenge back on the response object, then
     * return false on the overridden version of this method.
     * 
     * to effectively not have authorization, just implement this method and always
     * return true.
     * 
     * @param httpRequest
     * @param httpResponse
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public boolean isAuthorized( DavServerRequest davRequest, HttpServletResponse httpResponse )
        throws ServletException, IOException
    {
        // Always return true. Effectively no Authorization done.
        return true;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public Object lookup( String role )
        throws ServletException
    {
        try
        {
            return getPlexusContainer().lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup role [" + role + "]", e );
        }
    }

    public Object lookup( String role, String hint )
        throws ServletException
    {
        try
        {
            return getPlexusContainer().lookup( role, hint );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup role [" + role + "] hint [" + hint + "]", e );
        }
    }

    public void setDavManager( DavServerManager davManager )
    {
        this.davManager = davManager;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

    private void ensureContainerSet( ServletConfig sc )
        throws ServletException
    {
        ServletContext context = sc.getServletContext();

        if ( context.getAttribute( PlexusConstants.PLEXUS_KEY ) != null )
        {
            context.log( "Plexus container already in context." );

            return;
        }

        // Container not found.

        // Attempt to find it already loaded by xwork.
        PlexusContainer xworkContainer = (PlexusContainer) context.getAttribute( "webwork.plexus.container" );

        if ( xworkContainer != null )
        {
            context.setAttribute( PlexusConstants.PLEXUS_KEY, xworkContainer );

            return;
        }

        // Create container.

        Map keys = new HashMap();

        PlexusContainer pc;
        try
        {
            pc = new DefaultPlexusContainer( "default", keys, "META-INF/plexus/application.xml",
                                             new ClassWorld( "plexus.core", getClass().getClassLoader() ) );

            context.setAttribute( PlexusConstants.PLEXUS_KEY, pc );
        }
        catch ( PlexusContainerException e )
        {
            throw new ServletException( "Unable to initialize Plexus Container.", e );
        }
    }

    private PlexusContainer getPlexusContainer()
        throws ServletException
    {
        PlexusContainer container = (PlexusContainer) getServletContext().getAttribute( PlexusConstants.PLEXUS_KEY );
        if ( container == null )
        {
            throw new ServletException( "Unable to find plexus container." );
        }
        return container;
    }

    protected void requestDebug( HttpServletRequest request )
    {
        if ( debug )
        {
            System.out.println( "-->>> request ----------------------------------------------------------" );
            System.out.println( "--> " + request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + request.getServletPath() );
            System.out.println( request.getMethod() + " " + request.getRequestURI()
                + ( request.getQueryString() != null ? "?" + request.getQueryString() : "" ) + " " + "HTTP/1.1" );

            Enumeration enHeaders = request.getHeaderNames();
            while ( enHeaders.hasMoreElements() )
            {
                String headerName = (String) enHeaders.nextElement();
                String headerValue = request.getHeader( headerName );
                System.out.println( headerName + ": " + headerValue );
            }

            System.out.println();

            System.out.println( "------------------------------------------------------------------------" );
        }
    }
}
