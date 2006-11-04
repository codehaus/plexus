/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.server;

import org.codehaus.plexus.ircd.properties.Config;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.user.User;
import org.codehaus.plexus.ircd.utils.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class Server
{

    private static int port;
    private static int defaultPort;
    private static Thread serverThread;
    private static boolean serverStarted;
    private static InetAddress localAddress;
    private static ServerSocket serverSocket;

    static
    {
        defaultPort = Integer.parseInt( Config.getValue( "server.default.port", "6667" ) );
    }

    /**
     * to create a server on the default port
     */
    public Server() throws UnknownHostException, IOException
    {
        this.port = defaultPort;
        serverSocket = new ServerSocket( port );
        localAddress = InetAddress.getLocalHost();
    }

    /**
     * to create a server on the given port
     */
    public Server( int port ) throws UnknownHostException, IOException
    {
        this.port = port;
        serverSocket = new ServerSocket( port );
        localAddress = InetAddress.getLocalHost();
    }

    /**
     * to get the host name of the server
     */
    public static String getHost()
    {

        //!!!!!!!
        return "localhost";
        //return localAddress.getHostName();
    }

    /**
     * to get the host address of the server
     */
    public static String getHostAddress()
    {
        return "127.0.0.1";
        //return localAddress.getHostAddress();
    }

    /**
     * to get the port of the server
     */
    public static int getPort()
    {
        return port;
    }

    /**
     * to know if the server is started
     */
    public static boolean isServerStarted()
    {
        return serverStarted;
    }

    /**
     * to start the server
     */
    public static void main( String[] args ) throws Exception
    {
        Server server;
        try
        {
            port = Integer.parseInt( args[0] );
            server = new Server( port );
        }
        catch ( NumberFormatException e )
        {
            server = new Server();
        }
        catch ( ArrayIndexOutOfBoundsException e )
        {
            server = new Server();
        }
        server.start();
    }

    /**
     * to accept a new connection and to create a new user
     */
    public synchronized void processRequest() throws IOException
    {
        Socket socket = serverSocket.accept();
        User user = new User( socket );
        user.startProcess();
    }

    /**
     * to set the server port
     */
    public static void setPort( int port )
    {
        Server.port = port;
    }

    /**
     * to change the server's status
     */
    private static void setServerStarted( boolean serverStarted )
    {
        Server.serverStarted = serverStarted;
    }

    /**
     * to start the server
     */
    public void start()
    {
        if ( serverThread != null )
        {
            return;
        }
        setServerStarted( true );
        serverThread = new Thread( new Runnable()
        {
            public void run()
            {
                while ( isServerStarted() )
                {
                    try
                    {
                        processRequest();
                    }
                    catch ( Exception e )
                    {
                        Log.log( Level.WARNING, "Server", "start", "", e );
                    }
                }
            }
        } );
        serverThread.start();
        Log.log( Level.CONFIG, "Serveur", "start", "Java IRC Server Port = " + port + "." );
    }

    /**
     * to stop the server
     */
    public void stop()
    {
        setServerStarted( false );
        HandleUser.disconnectAll();
        serverThread = null;
        try
        {
            serverSocket.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        Log.log( Level.CONFIG, "Serveur", "stop", "Java IRC Server stopped." );
    }
}
