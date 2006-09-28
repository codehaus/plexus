/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.ProtocolException;
import java.io.IOException;

import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;


/**
 * @avalon.component version="1.0" name="net.java.dev.openim.IMServer" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.IMServer"
 *
 * @version 1.0 
 * @author AlAg
 */
public class IMServerImpl extends AbstractLogEnabled 
implements  IMServer, Configurable, Initializable, ConnectionHandlerFactory, Serviceable,Disposable
{
    private ServerParameters    m_serverParameters;
    private ServiceManager      m_serviceManager;
    private SocketManager       m_socketManager;
    private ConnectionManager   m_connectionManager;

    
    private int                 m_listenBacklog;
    private InetAddress         m_bindTo;
    private boolean             m_disposed = false;
    
    //-------------------------------------------------------------------------
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        m_listenBacklog = configuration.getChild( "listen-backlog" ).getValueAsInteger();
        final String bindAddress = configuration.getChild( "bind" ).getValue( null );
        
        try{
            if( bindAddress != null ){
                m_bindTo = InetAddress.getByName( bindAddress );
            }
        } catch( java.net.UnknownHostException e ){
            throw new ConfigurationException( "Unable to bind " + bindAddress, e );
        }
    }
    
    //-------------------------------------------------------------------------
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0" key="ServerParameters"
     *
     * @avalon.dependency type="net.java.dev.openim.IMConnectionHandler:1.0" key="IMConnectionHandler"
     * @avalon.dependency type="org.apache.avalon.cornerstone.services.connection.ConnectionManager" key="ConnectionManager"
     * @avalon.dependency type="org.apache.avalon.cornerstone.services.sockets.SocketManager" key="SocketManager"
     */
    public void service(ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_socketManager = (SocketManager)serviceManager.lookup( "SocketManager" );
        m_connectionManager = (ConnectionManager)serviceManager.lookup( "ConnectionManager" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
    }    
    
    //-------------------------------------------------------------------------
    public void initialize() throws java.lang.Exception {
        m_disposed = false;
        final ServerSocketFactory factory = m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket clientServerSocket = factory.createServerSocket( m_serverParameters.getLocalClientPort(), m_listenBacklog, m_bindTo );
        final ServerSocket serverServerSocket = factory.createServerSocket( m_serverParameters.getLocalServerPort(), m_listenBacklog, m_bindTo );
                
        m_connectionManager.connect( "client-listener", clientServerSocket, this );
        m_connectionManager.connect( "server-listener", serverServerSocket, this );
        
        final ServerSocketFactory sslfactory = m_socketManager.getServerSocketFactory( "secure" );
        final ServerSocket sslClientServerSocket = sslfactory.createServerSocket( m_serverParameters.getLocalSSLClientPort(), m_listenBacklog, m_bindTo );
        final ServerSocket sslServerServerSocket = sslfactory.createServerSocket( m_serverParameters.getLocalSSLServerPort(), m_listenBacklog, m_bindTo );
        m_connectionManager.connect( "ssl-client-listener", sslClientServerSocket, this );
        m_connectionManager.connect( "ssl-server-listener", sslServerServerSocket, this );
        
        String s = "Server '"+ m_serverParameters.getHostName()+"' initialized on"
                + " server2server port " + m_serverParameters.getLocalServerPort()
                + " SSL-server2server port " + m_serverParameters.getLocalSSLServerPort()
                + " client2server port " + m_serverParameters.getLocalClientPort()
                + " SSL-client2server port " + m_serverParameters.getLocalSSLClientPort();
                
        getLogger().info( s );
        //System.out.println( s );
    }
    
    
    //-------------------------------------------------------------------------
    public ConnectionHandler createConnectionHandler() throws java.lang.Exception {
       if ( !m_disposed) {
          return (ConnectionHandler)m_serviceManager.lookup( "IMConnectionHandler" );
       } else {
          return new DisposedConnectionHandler();
          
       } // end of else
       
    }    
    
    //-------------------------------------------------------------------------
    public void releaseConnectionHandler(ConnectionHandler connectionHandler) {
        //m_serviceManager.release( connectionHandler );        
    }
    //-------------------------------------------------------------------------
   public void dispose() {
      getLogger().debug( "Disposing Server" );
      // We must really stop all incomming traffic!
      m_disposed = true;

      
   }

   class DisposedConnectionHandler implements ConnectionHandler {
      public void handleConnection(Socket connection)
         throws IOException,
                ProtocolException {
         getLogger().info("Server is closing down: does not accept new connections");
         connection.close();
      }
   }
   
}


