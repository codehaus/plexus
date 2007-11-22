package org.codehaus.plexus.webdav.servlet.basic;

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

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.webdav.DavServerComponent;
import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.servlet.AbstractWebDavServlet;
import org.codehaus.plexus.webdav.servlet.DavServerRequest;
import org.codehaus.plexus.webdav.util.WrappedRepositoryRequest;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * BasicWebDavServlet - Basic implementation of a single WebDAV server as servlet. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class BasicWebDavServlet
    extends AbstractWebDavServlet
{
    public static final String INIT_ROOT_DIRECTORY = "dav.root";

    private DavServerComponent davServer;

    // -----------------------------------------------------------------------
    // Servlet Implementation
    // -----------------------------------------------------------------------
    
    public void init( ServletConfig config )
        throws ServletException
    {
        super.init( config );

        String prefix = config.getServletName();

        boolean useIndexHtml = getUseIndexHtml( config );
        File rootDir = getRootDirectory( config );
        
        if ( rootDir != null &&  !rootDir.isDirectory() )
        {
            log( "Invalid configuration, the dav root " + rootDir.getPath()
                + " is not a directory: [" + rootDir.getAbsolutePath() + "]" );
        }

        try
        {
            davServer = davManager.createServer( prefix, rootDir );
            davServer.setUseIndexHtml( useIndexHtml );
            davServer.init( config );
        }
        catch ( DavServerException e )
        {
            throw new ServletException( "Unable to create DAV Server component for prefix [" + prefix
                + "] mapped to root directory [" + rootDir.getPath() + "]", e );
        }
    }

    public File getRootDirectory( ServletConfig config )
        throws ServletException
    {
        String rootDirName = config.getInitParameter( INIT_ROOT_DIRECTORY );

        if ( StringUtils.isEmpty( rootDirName ) )
        {
            log( "Init Parameter '" + INIT_ROOT_DIRECTORY + "' is empty." );
            return null;
        }

        return new File( rootDirName );
    }

    protected void service( HttpServletRequest httpRequest, HttpServletResponse httpResponse )
        throws ServletException, IOException
    {
        DavServerRequest davRequest = new BasicDavServerRequest( new WrappedRepositoryRequest( httpRequest ) );

        if ( davServer == null )
        {
            throw new ServletException( "Unable to service DAV request due to unconfigured DavServerComponent." );
        }

        requestDebug( httpRequest );

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

    public void setUseIndexHtml( boolean useIndexHtml )
    {
        davServer.setUseIndexHtml( useIndexHtml );
    }

    public DavServerComponent getDavServer()
    {
        return davServer;
    }

    public void setDavServer( DavServerComponent davServer )
    {
        this.davServer = davServer;
    }
}
