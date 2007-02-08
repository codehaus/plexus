/*
 *  Copyright (C) MX4J.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.server;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

/** @plexus.component */
public abstract class DefaultServer
    extends AbstractLogEnabled
    implements Server, Startable
{
    /** @plexus.requirement */
    private PlexusServerSocketFactory socketFactory;

    private int port;

    private String host;

    /** @plexus.configuration default-value="5" */
    private int backlog;

    private ServerSocket serverSocket;

    private boolean alive;

    public InetAddress getServerAddress()
    {
        return serverSocket.getInetAddress();
    }

    public void start()
        throws StartingException
    {
        try
        {
            getLogger().info( "Staring server [" + port + ":" + backlog + ":" + host + "]" );

            serverSocket = socketFactory.createServerSocket( port, backlog, host );
        }
        catch ( IOException e )
        {
            throw new StartingException( "Error starting server.", e );
        }

        if ( serverSocket == null )
        {
            getLogger().error( "DefaultServer socket is null" );

            return;
        }

        alive = true;

        Thread serverThread = new Thread( new Runnable()
        {
            public void run()
            {
                while ( alive )
                {
                    try
                    {
                        Socket client;

                        client = serverSocket.accept();

                        if ( !alive )
                        {
                            break;
                        }

                        new Client( client ).start();
                    }
                    catch ( InterruptedIOException e )
                    {
                    }
                    catch ( IOException e )
                    {
                    }
                    catch ( Exception e )
                    {
                        getLogger().warn( "Exception during request processing", e );
                    }
                    catch ( Error e )
                    {
                        getLogger().error( "Error during request processing", e );
                    }
                }
                try
                {
                    serverSocket.close();
                }
                catch ( IOException e )
                {
                    getLogger().warn( "Exception closing the server", e );
                }
                serverSocket = null;

                getLogger().info( "DefaultServer stopped" );
            }
        } );
        serverThread.start();
    }

    public void stop()
    {
        try
        {
            // force the close with a socket call
            //new Socket( host, port );

            if ( serverSocket != null )
            {
                serverSocket.close();
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    private class Client
        extends Thread
    {
        private Socket client;

        Client( Socket client )
        {
            this.client = client;
        }

        public void run()
        {
            try
            {
                handleConnection( client );
            }
            catch ( Exception e )
            {
                getLogger().error( "Error during http request ", e );
            }
            finally
            {
                try
                {
                    client.close();
                }

                catch ( IOException e )
                {
                    getLogger().warn( "Exception during request processing", e );
                }
            }
        }
    }
}


