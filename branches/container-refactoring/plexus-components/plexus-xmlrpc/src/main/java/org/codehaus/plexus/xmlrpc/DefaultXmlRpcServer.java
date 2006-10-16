package org.codehaus.plexus.xmlrpc;

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

import java.net.InetAddress;
import java.net.Socket;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcException;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultXmlRpcServer
    extends AbstractLogEnabled
    implements XmlRpcServer, Startable
{
    private int port;

    private String host;

    private WebServer webServer;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
    }

    public void stop()
    {
        if ( webServer != null )
        {
            webServer.shutdown();
        }

        try
        {
            Socket interrupt = new Socket( InetAddress.getLocalHost(), port );

            interrupt.close();
        }
        catch ( Exception ex )
        {
            // ignore
        }
    }

    // ----------------------------------------------------------------------
    // XmlRpcServer Implementation
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Listener Management
    // ----------------------------------------------------------------------

    public void addListener( String host, int port, boolean secure )
        throws XmlRpcException
    {
        if ( webServer != null )
        {
            throw new RuntimeException( "This implementation only support a single web server." );
        }

        webServer = new WebServer( port );

        this.port = port;

        this.host = host;
    }

    public void removeListener( String host, int port )
        throws XmlRpcException
    {
        WebServer webServer = getWebServer( host, port );

        webServer.shutdown();
    }

    public void startListener( String host, int port )
        throws XmlRpcException
    {
        WebServer webServer = getWebServer( host, port );

        if ( host == null )
        {
            getLogger().info( "Starting XML-RPC listener on port '" + port + "'." );
        }
        else
        {
            getLogger().info( "Starting XML-RPC listener on host '" + host + "', port '" + port + "'." );
        }

        webServer.start();
    }

    // ----------------------------------------------------------------------
    // Handler Management
    // ----------------------------------------------------------------------

    public void addHandler( String name, Object handler )
        throws XmlRpcException
    {
        webServer.addHandler( name, handler );
    }

    public void addHandler( String host, String name, int port, Object handler )
        throws XmlRpcException
    {
        WebServer webServer = getWebServer( host, port );

        webServer.addHandler( name, handler );
    }

    public void removeHandler( String name )
        throws XmlRpcException
    {
        webServer.removeHandler( name );
    }

    public void removeHandler( String host, int port, String name )
        throws XmlRpcException
    {
        WebServer webServer = getWebServer( host, port );

        webServer.removeHandler( name );
    }

    // ----------------------------------------------------------------------
    // Authorization
    // ----------------------------------------------------------------------

    public void acceptClient( String clientHost )
    {
        webServer.setParanoid( true );

        webServer.acceptClient( clientHost );
    }

    public void acceptClient( String host, int port, String clientHost )
    {
        WebServer webServer = getWebServer( host, port );

        webServer.setParanoid( true );

        webServer.acceptClient( clientHost );
    }

    public void denyClient( String clientHost )
    {
        webServer.setParanoid( true );

        webServer.denyClient( clientHost );
    }

    public void denyClient( String host, int port, String clientHost )
    {
        WebServer webServer = getWebServer( host, port );

        webServer.setParanoid( true );

        webServer.denyClient( clientHost );
    }

    public void setParanoid( String host, int port, boolean state )
    {
        WebServer webServer = getWebServer( host, port );

        webServer.setParanoid( state );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private WebServer getWebServer( String host, int port )
    {
        if ( port != this.port )
        {
            throw new RuntimeException( "There isn't a listener on port " + port + "." );
        }

        if ( !( host == null && this.host == null ) &&  !host.equals( this.host ) )
        {
            throw new RuntimeException( "There isn't a listener on host '" + host + "'." );
        }

        return webServer;
    }
}
