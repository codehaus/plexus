package org.codehaus.plexus.servletcontainer.jetty;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.servletcontainer.ServletContainer;
import org.codehaus.plexus.servletcontainer.ServletContainerException;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.Log;
import org.mortbay.util.LogSink;
import org.mortbay.http.HttpListener;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JettyServletContainer
    extends AbstractLogEnabled
    implements ServletContainer, Startable
{
    private Server server;

    private LogSink jettyLogSink;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Initialize the Jetty logging system
        // ----------------------------------------------------------------------

        Log log = Log.instance();

//        log.disableLog();

        jettyLogSink = new PlexusJettyLogSink( getLogger() );

//        log.add( jettyLogSink );

        // ----------------------------------------------------------------------
        // Start the server
        // ----------------------------------------------------------------------

        server = new Server();

        server.start();
    }

    public void stop()
    {
        if ( server.isStarted() )
        {
            while( true )
            {
                try
                {
                    server.stop( true );

                    break;
                }
                catch ( InterruptedException e )
                {
                    continue;
                }
            }

            server.destroy();
        }
    }

    // ----------------------------------------------------------------------
    // ServletContainer Implementation
    // ----------------------------------------------------------------------

    public void addListener( String address, int port )
        throws ServletContainerException, UnknownHostException
    {
        // TODO: validate the address

        InetAddrPort addrPort = new InetAddrPort( address, port );

        HttpListener listener;

        try
        {
            listener = server.addListener( addrPort );
        }
        catch( IOException e )
        {
            throw new ServletContainerException( "Error while adding listener on address: '" + address + "', port: " + port + ".", e );
        }

        try
        {
            listener.start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException( "Error while starting listener on address: '" + address + "', port: " + port + ".", e );
        }
    }

    public void deployWAR( File war, String context, ClassLoader classLoader, String virtualHost )
        throws ServletContainerException
    {
        if ( war == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'war' cannot be null." );
        }

        if ( context == null )
        {
            throw new ServletContainerException( "Invalid parameter: 'context' cannot be null." );
        }

        // ----------------------------------------------------------------------
        // Create the web application
        // ----------------------------------------------------------------------

        WebApplicationContext applicationContext;

        try
        {
            if ( virtualHost != null )
            {
                applicationContext = server.addWebApplication( virtualHost, context, war.getAbsolutePath() );
            }
            else
            {
                applicationContext = server.addWebApplication( context, war.getAbsolutePath() );
            }
        }
        catch ( IOException e )
        {
            throw new ServletContainerException( "Error while deplying WAR.", e );
        }

        // ----------------------------------------------------------------------
        // Configure the web application
        // ----------------------------------------------------------------------

        if ( classLoader != null )
        {
            applicationContext.setClassLoader( classLoader );
        }

        // ----------------------------------------------------------------------
        // Start it!
        // ----------------------------------------------------------------------

        try
        {
            applicationContext.start();
        }
        catch ( Exception e )
        {
            throw new ServletContainerException( "Error while starting the web application.", e );
        }
    }
}
