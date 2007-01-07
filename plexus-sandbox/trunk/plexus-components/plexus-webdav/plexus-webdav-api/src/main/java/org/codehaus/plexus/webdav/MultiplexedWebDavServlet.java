package org.codehaus.plexus.webdav;

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
    }

    public abstract void initServers();

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
        return "Plexus Simple WebDAV Servlet";
    }

    public void service( ServletRequest req, ServletResponse res )
        throws ServletException, IOException
    {
        if ( !( req instanceof HttpServletRequest ) )
        {
            throw new ServletException( "BasicWebDavServlet can only handle HttpServletRequests." );
        }

        if ( !( res instanceof HttpServletResponse ) )
        {
            throw new ServletException( "BasicWebDavServlet can only handle HttpServletResponse." );
        }

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        DavServerRequest davRequest = new MultiplexedDavServerRequest( httpRequest );

        DavServerComponent davServer = davManager.getServer( davRequest.getPrefix() );

        if ( davServer == null )
        {
            throw new ServletException( "Unable to service DAV request due to unconfigured DavServerComponent "
                + "for prefix [" + davRequest.getPrefix() + "]." );
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
}
