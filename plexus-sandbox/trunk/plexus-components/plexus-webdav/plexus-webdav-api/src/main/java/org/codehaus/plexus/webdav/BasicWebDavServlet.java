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
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BasicWebDavServlet - Basic implementation of a single WebDAV server as servlet. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class BasicWebDavServlet
    implements Servlet
{
    public static final String INIT_ROOT_DIRECTORY = "dav.root";

    private ServletConfig config;

    private DavServerManager davManager;

    private DavServerComponent davServer;

    private boolean debug = true;

    // -----------------------------------------------------------------------
    // Servlet Implementation
    // -----------------------------------------------------------------------

    public void init( ServletConfig config )
        throws ServletException
    {
        this.config = config;

        davManager = (DavServerManager) PlexusServletUtils.lookup( config.getServletContext(), DavServerManager.ROLE );

        String prefix = config.getServletContext().getServletContextName();

        String rootDirName = config.getInitParameter( INIT_ROOT_DIRECTORY );

        if ( StringUtils.isEmpty( rootDirName ) )
        {
            throw new ServletException( "Init Parameter '" + INIT_ROOT_DIRECTORY + "' is empty." );
        }

        File rootDir = new File( rootDirName );

        if ( !rootDir.isDirectory() )
        {
            throw new ServletException( "Invalid configuration, the specified " + INIT_ROOT_DIRECTORY
                + " is not a directory: [" + rootDir.getAbsolutePath() + "]" );
        }

        try
        {
            davServer = davManager.createServer( prefix, new File( rootDirName ) );
            davServer.init( config );
        }
        catch ( DavServerException e )
        {
            throw new ServletException( "Unable to create DAV Server component for prefix [" + prefix
                + "] mapped to root directory [" + rootDirName + "]", e );
        }
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

        if ( davServer == null )
        {
            throw new ServletException( "Unable to service request due to unconfigured DavServerComponent." );
        }

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        DavServerRequest davRequest = new BasicDavServerRequest( httpRequest );

        if ( debug )
        {
            System.out.println( "-->>> request ----------------------------------------------------------" );
            System.out
                .println( httpRequest.getMethod() + " " + httpRequest.getRequestURI()
                    + ( httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "" ) + " "
                    + "HTTP/1.1" );

            Enumeration enHeaders = httpRequest.getHeaderNames();
            while ( enHeaders.hasMoreElements() )
            {
                String headerName = (String) enHeaders.nextElement();
                String headerValue = httpRequest.getHeader( headerName );
                System.out.println( headerName + ": " + headerValue );
            }

            System.out.println();

            System.out.println( "------------------------------------------------------------------------" );
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

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }

}
