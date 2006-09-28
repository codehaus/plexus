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


import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.activity.Disposable;

import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.session.IMSession;

/**
 * @avalon.component version="1.0" name="IMConnectionHandler" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.IMConnectionHandler"
 *
 * @version 1.0
 * @author AlAg
 */
public class IMConnectionHandlerImpl extends DefaultSessionProcessor 
implements IMConnectionHandler, ConnectionHandler, Configurable,Disposable {

    private ServerParameters    m_serverParameters;
    private SessionsManager     m_sessionsManager;
    private IMRouter            m_router;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.Streams:1.0" key="Streams"
     * @avalon.dependency type="net.java.dev.openim.jabber.FlashStreams:1.0" key="FlashStreams"
     * @avalon.dependency type="net.java.dev.openim.session.SessionsManager:1.0" key="SessionsManager"
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0" key="ServerParameters"
     * @avalon.dependency type="net.java.dev.openim.IMRouter:1.0" key="IMRouter"
     * @avalon.dependency type="net.java.dev.openim.S2SConnectorManager:1.0" key="S2SConnectorManager"
     */
    public void service(ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
        m_sessionsManager = (SessionsManager)serviceManager.lookup( "SessionsManager" );
        m_router = (IMRouter)serviceManager.lookup( "IMRouter" );
        
        S2SConnectorManager s2sConnectorManager = (S2SConnectorManager)serviceManager.lookup( "S2SConnectorManager" );
        s2sConnectorManager.setConnectionHandler( this );

        m_router.setS2SConnectorManager(  s2sConnectorManager );
        
        super.service( serviceManager );
    }
    //-------------------------------------------------------------------------
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        super.configure( configuration );
    }

    //-------------------------------------------------------------------------
    public void handleConnection(java.net.Socket socket) throws java.io.IOException, java.net.ProtocolException {
        getLogger().info( "Connection from " + socket.getRemoteSocketAddress() );
        
        IMSession session = null;
        try {
			
			if( socket.getLocalPort() == m_serverParameters.getLocalClientPort() 
				|| socket.getLocalPort() == m_serverParameters.getLocalSSLClientPort() ){
                session = m_sessionsManager.getNewClientSession();
			}
			else{
                session = m_sessionsManager.getNewServerSession();
			}
            	
            session.setRouter( m_router );
            
            getLogger().debug( "######## ["+m_serverParameters.getHostName()+"] New session instance: " + session.getId() );
            session.setup( socket );
            //session.setHostname( m_serverParameters.getHostName() );
            
            //socket.setKeepAlive( true );

            final XmlPullParser xpp = session.getXmlPullParser();
            
            int eventType = xpp.getEventType(); 
            while( eventType != XmlPullParser.START_DOCUMENT ){
                eventType = xpp.getEventType();
            }

            
            String s = "<?xml version='1.0' encoding='"+ session.getEncoding() +"' ?>";
            session.writeOutputStream( s );

            
            
            process( session );
        }
        
        catch( java.net.SocketException e ){
            String s = e.getMessage();
            getLogger().info( s );
        }
        
        catch( java.io.EOFException e ){
            getLogger().info( e.getMessage() );
        }
        
        
        catch( Exception e ){
            getLogger().error( e.getMessage(), e );
            throw new java.io.IOException( e.getMessage() );
        }
        
        finally{
            try{
                if( session != null ){
                    if( m_sessionsManager != null ){
                        getLogger().info( "Release session " + session.getId() );
                        m_sessionsManager.release( session );
                    }
                }
                if( ! socket.isClosed() ){
                    socket.close();
                }
            } catch( Exception e ){
                getLogger().error( e.getMessage(), e );
                throw new java.io.IOException( e.getMessage() );
            }
        }
        getLogger().info( "Disconnected session " + session.getId() );
    }

   
   public void dispose() {
      getLogger().debug( "Disposing Router" );
      // We must stop all sessions!
      // Hope the pull parser stops gracefully!
      m_router.releaseSessions();

      // Unfortunately we may also have sessions that was never authenticated
      // and therefore is not yet part of the router sessions
      m_sessionsManager.releaseSessions();
      
   }
    
    
    
    
}


