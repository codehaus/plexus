/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.synapse.acceptor;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.synapse.handler.AbstractServiceHandler;
import org.codehaus.plexus.synapse.socket.ServerSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DefaultSocketAcceptor
    extends AbstractServiceHandler
    implements Serviceable, Configurable, Initializable, Disposable, SocketAcceptor
{
    private int port;

    private Thread serverThread;

    private boolean serverStarted;

    private ServerSocket serverSocket;

    private ServerSocketFactory serverSocketFactory;

    private InetAddress localAddress;

    private InetAddress bindAddress;

    // maximum connections

    // paranoid mode

    // list of valid address that can connect

    public boolean isServerStarted()
    {
        return serverStarted;
    }

    /**
     * to change the server's status
     */
    private void setServerStarted( boolean serverStarted )
    {
        this.serverStarted = serverStarted;
    }

    // ----------------------------------------------------------------------
    // Service Handler API
    // ----------------------------------------------------------------------

    public void handleEvents()
    {
        start();
    }

    public void handleEvent( Socket socket )
    {
    }

    public String getHandleKey()
    {
        return "ACCEPT";
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        serverSocketFactory = (ServerSocketFactory) serviceManager.lookup( ServerSocketFactory.class.getName() );
    }

    /** @see org.apache.avalon.framework.configuration.Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        port = configuration.getChild( "port" ).getValueAsInteger();
    }

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        InetAddress[] adds = InetAddress.getAllByName( "192.168.1.103" );

        bindAddress = adds[0];

        serverSocket = serverSocketFactory.createServerSocket( port, 50, bindAddress );

        localAddress = InetAddress.getLocalHost();
    }

    public void start()
    {
        if ( serverThread != null )
        {
            return;
        }

        setServerStarted( true );

        System.out.println( "Server is started" );

        serverThread = new Thread( new Runnable()
        {
            public void run()
            {
                while ( isServerStarted() )
                {
                    try
                    {
                        Socket socket = serverSocket.accept();

                        getReactor().handleEvent( socket );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Error processing request: ", e );
                    }
                }
            }
        } );

        serverThread.start();
    }

    public void stop()
    {
        setServerStarted( false );

        serverThread = null;

        try
        {
            serverSocket.close();
        }
        catch ( IOException e )
        {
            getLogger().error( "Error shutting down server." );
        }
    }

    /** @see org.apache.avalon.framework.activity.Disposable#dispose() */
    public void dispose()
    {
        stop();
    }
}


