/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Incubator", "FtpServer", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * $Id$
 */
package org.apache.ftpserver;

import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.cornerstone.blocks.connection.DefaultConnectionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.ftpserver.interfaces.FtpServerInterface;

/**
 * Ftp server starting point. Avalon framework will load this
 * from the jar file. This is also the starting point of remote
 * admin.
 *
 * @author Rana Bhattacharyya <rana_b@yahoo.com>
 * @author Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 * @phoenix:block
 * @phoenix:service name="org.apache.ftpserver.interfaces.FtpServerInterface"
 */
public class FtpServerImpl
    extends AbstractLogEnabled
    implements FtpServerInterface, Contextualizable, Serviceable, Configurable, Disposable, ConnectionHandlerFactory
{
    private ServerSocket serverSocket = null;
    private SocketManager socketManager = null;
    private ConnectionManager connectionManager = null;
    private Context context = null;
    private FtpConfig configuration = null;

    /**
     * Default constructor - does nothing.
     */
    public FtpServerImpl()
    {
    }

    /**
     * Set application context - first spep.
     */
    public void contextualize( Context context ) throws ContextException
    {
        try
        {
            configuration = new FtpConfig();
            configuration.setLogger( getLogger() );
            this.context = context;
            configuration.setContext( this.context );
        }
        catch ( Exception ex )
        {
            getLogger().error( "FtpServerImpl.contextualize()", ex );
            throw new ContextException( "FtpServerImpl.contextualize()", ex );
        }
    }


    /**
     * Get all managers - second step.
     *
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.sockets.SocketManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
     * @phoenix:dependency name="org.apache.ftpserver.usermanager.UserManagerInterface"
     * @phoenix:dependency name="org.apache.ftpserver.ip.IpRestrictorInterface"
     */
    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        configuration.setServiceManager( serviceManager );

        socketManager = (SocketManager) serviceManager.lookup( SocketManager.ROLE );

        connectionManager = (ConnectionManager) serviceManager.lookup( ConnectionManager.ROLE );
    }

    /**
     * Configure the server - third step.
     *
     * @param conf the XML configuration block
     */
    public void configure( Configuration conf ) throws ConfigurationException
    {
        try
        {
            configuration.setConfiguration( conf );

            // open server socket
            ServerSocketFactory factory = socketManager.getServerSocketFactory( "plain" );

            InetAddress serverAddress = configuration.getSelfAddress();

            if ( serverAddress == null )
            {
                serverSocket = factory.createServerSocket( configuration.getServerPort(), 5 );
            }
            else
            {
                serverSocket = factory.createServerSocket( configuration.getServerPort(), 5, serverAddress );
            }

            connectionManager.connect( DISPLAY_NAME, serverSocket, this );

            System.out.println( "FTP server ready!" );

            if ( configuration.isRemoteAdminAllowed() )
            {
                System.out.println( "You can start the remote admin by executing \"java -jar ftp-admin.jar\"." );
            }
        }
        catch ( Exception ex )
        {
            getLogger().error( "FtpServerImpl.configure()", ex );

            throw new ConfigurationException( ex.getMessage(), ex );
        }
    }

    /**
     * Release all resources.
     */
    public void dispose()
    {
        getLogger().info( "Closing Ftp server..." );

        if ( configuration != null )
        {
            try
            {
                configuration.dispose();

                ((DefaultConnectionManager)connectionManager).dispose(); 

                serverSocket.close();
            }
            catch ( Exception ex )
            {
                getLogger().warn( "FtpServerImpl.dispose()", ex );
            }
        }
    }

    /**
     * Construct an appropriate <code>ConnectionHandler</code>
     * to handle a new connection.
     *
     * @return the new ConnectionHandler
     * @throws Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler() throws Exception
    {
        BaseFtpConnection conHandle = new FtpConnection( configuration );
        return conHandle;
    }

    /**
     * Release a previously created ConnectionHandler.
     * e.g. for spooling.
     */
    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
    }
}

