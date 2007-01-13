package org.codehaus.plexus.webdav.servlet.multiplexed;

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

import org.codehaus.plexus.servlet.PlexusServletUtils;
import org.codehaus.plexus.webdav.DavServerComponent;
import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.DavServerManager;
import org.codehaus.plexus.webdav.servlet.DavServerRequest;
import org.codehaus.plexus.webdav.util.WrappedRepositoryRequest;

import java.io.File;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * MultiplexedWebDavServlet - and abstracted multiplexed webdav servlet.
 * </p>
 * 
 * <p>
 * Implementations of this servlet should override the {@link #initServers()} method and create all of the
 * appropriate DavServerComponents needed using the {@link DavServerManager} obtained via the {@link #getDavManager()}
 * method.
 * </p>
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class MultiplexedWebDavServlet
    implements Servlet
{
    private ServletConfig config;

    private DavServerManager davManager;

    // -----------------------------------------------------------------------
    // Servlet Implementation
    // -----------------------------------------------------------------------

    public void init( ServletConfig config )
        throws ServletException
    {
        this.config = config;

        davManager = (DavServerManager) PlexusServletUtils.lookup( config.getServletContext(), DavServerManager.ROLE );

        try
        {
            initServers( config );
        }
        catch ( DavServerException e )
        {
            throw new ServletException( "Unable to init nested servers." + e );
        }
    }

    /**
     * Create any DavServerComponents here.
     * Use the {@link #createServer(String, File, ServletConfig)} method to create your servers.
     * 
     * @param config the config to use.
     * @throws DavServerException if there was a problem initializing the server components.
     */
    public abstract void initServers( ServletConfig config )
        throws DavServerException;

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

    public DavServerComponent createServer( String prefix, File rootDirectory, ServletConfig config )
        throws DavServerException
    {
        DavServerComponent serverComponent = getDavManager().createServer( prefix, rootDirectory );
        serverComponent.init( config );
        return serverComponent;
    }

    public void destroy()
    {
        if ( davManager != null )
        {
            try
            {
                PlexusServletUtils.release( config.getServletContext(), davManager );
            }
            catch ( ServletException e )
            {
                config.getServletContext().log( "Unable to release DavServletManager.", e );
            }
        }
    }

    public ServletConfig getServletConfig()
    {
        return config;
    }

    public String getServletInfo()
    {
        return "Plexus WebDAV Servlet";
    }

    public void service( ServletRequest req, ServletResponse res )
        throws ServletException, IOException
    {
        if ( !( req instanceof HttpServletRequest ) )
        {
            throw new ServletException( "MultiplexedWebDavServlet can only handle HttpServletRequests." );
        }

        if ( !( res instanceof HttpServletResponse ) )
        {
            throw new ServletException( "MultiplexedWebDavServlet can only handle HttpServletResponse." );
        }

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        DavServerRequest davRequest = new MultiplexedDavServerRequest( new WrappedRepositoryRequest( httpRequest ) );

        DavServerComponent davServer = davManager.getServer( davRequest.getPrefix() );

        if ( davServer == null )
        {
            throw new ServletException( "Unable to service DAV request due to unconfigured DavServerComponent "
                + "for prefix [" + davRequest.getPrefix() + "]." );
        }

        if ( !isAuthenticated( davRequest, httpResponse ) )
        {
            return;
        }

        if ( !isAuthorized( davRequest, httpResponse ) )
        {
            return;
        }

        try
        {
            davServer.process( davRequest, httpResponse );
        }
        catch ( DavServerException e )
        {
            throw new ServletException( "Unable to process request.", e );
        }
    }

    public DavServerManager getDavManager()
    {
        return davManager;
    }

    public void setDavManager( DavServerManager davManager )
    {
        this.davManager = davManager;
    }

    public void log( String msg )
    {
        getServletConfig().getServletContext().log( msg );
    }

    public void log( String msg, Throwable t )
    {
        getServletConfig().getServletContext().log( msg, t );
    }
}
