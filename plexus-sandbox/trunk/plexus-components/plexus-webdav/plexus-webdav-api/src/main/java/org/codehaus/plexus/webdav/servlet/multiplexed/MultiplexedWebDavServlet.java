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

import org.codehaus.plexus.webdav.DavServerComponent;
import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.DavServerManager;
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
    extends AbstractWebDavServlet
{
    public void init( ServletConfig config )
        throws ServletException
    {
        super.init( config );

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

    public DavServerComponent createServer( String prefix, File rootDirectory, ServletConfig config )
        throws DavServerException
    {
        DavServerComponent serverComponent = getDavManager().createServer( prefix, rootDirectory );
        serverComponent.init( config );
        return serverComponent;
    }

    protected void service( HttpServletRequest httpRequest, HttpServletResponse httpResponse )
        throws ServletException, IOException
    {
        DavServerRequest davRequest = new MultiplexedDavServerRequest( new WrappedRepositoryRequest( httpRequest ) );

        DavServerComponent davServer = davManager.getServer( davRequest.getPrefix() );

        if ( davServer == null )
        {
            throw new ServletException( "Unable to service DAV request due to unconfigured DavServerComponent "
                + "for prefix [" + davRequest.getPrefix() + "]." );
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
}
