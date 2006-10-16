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



import java.net.Socket;
import java.net.InetSocketAddress;



import java.io.IOException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceManager;



import org.xmlpull.v1.XmlPullParser;


import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.session.IMServerSession;



/**
 *
 * @avalon.component version="1.0" name="S2SConnector" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.S2SConnector"
 *
 * @version 1.0
 * @author AlAg
 * @author PV
 */

public class S2SConnectorImpl extends AbstractLogEnabled 
implements S2SConnector, Runnable, Configurable, Serviceable {

    private ServerParameters    m_serverParameters;
    private SessionsManager     m_sessionsManager;
    private IMConnectionHandler m_connectionHandler;

    
    private int     m_deliveryConnectionTimout;
    
    
    
    private String m_toHostName;
    private IMServerSession m_session;
    private IMRouter m_router; 
    
    private volatile boolean m_isAlive = false;
    
    private volatile boolean m_ready = false;
    private volatile boolean m_sendResult = false;
    private volatile boolean m_sendVerify = false;
    
    private volatile String m_verifyDialbackValue;
    private volatile String m_verifyId;

    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
     */
    public void service( ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
    }
    
    //-------------------------------------------------------------------------
    public void configure(Configuration configuration) throws ConfigurationException {
        m_deliveryConnectionTimout = configuration.getChild( "delivery-connection-timeout" ).getValueAsInteger( 1000*60 );
    }
    //-------------------------------------------------------------------------    
    public void setToHostname( String toHostname ){
        m_toHostName = toHostname;
    }
    //-------------------------------------------------------------------------    
    public void setRouter( IMRouter router ){
        m_router = router;
    }
    //-------------------------------------------------------------------------    
    public void setIMConnectionHandler( IMConnectionHandler connectionHandler ){
        m_connectionHandler = connectionHandler;
    }
    //-------------------------------------------------------------------------
    public void setSessionsManager( SessionsManager sessionManager ){
        m_sessionsManager = sessionManager;
    }
    
    //----------------------------------------------------------------------
    public IMServerSession getSession() throws Exception {
        if( m_session == null ){
            m_session = m_sessionsManager.getNewServerSession();
            m_session.setRouter( m_router );
            m_session.setRemoteHostname( m_toHostName );
        }
        return m_session;
    }
    
    //----------------------------------------------------------------------
    public boolean isAlive() {
        return m_isAlive;
    }
    //----------------------------------------------------------------------
    public void run() {
        m_isAlive = true;
        try{

            //Socket socket = new Socket( toHostname, m_serverParameters.getRemoteServerPort() );
            Socket socket = new Socket();
            InetSocketAddress insa = new InetSocketAddress( m_toHostName, m_serverParameters.getRemoteServerPort() );
            getLogger().debug( "Trying to connect (timeout "+m_deliveryConnectionTimout+" ms) to " + m_toHostName +":"+m_serverParameters.getRemoteServerPort() );
            socket.connect( insa, m_deliveryConnectionTimout );
            getLogger().info( "Connection to "+ m_toHostName +":"+m_serverParameters.getRemoteServerPort() + " successfull" );
            //socket.setKeepAlive( true );
            
            IMServerSession session = getSession();
            session.setup( socket );
            
            
            final XmlPullParser xpp = session.getXmlPullParser();

            int eventType = xpp.getEventType(); 
            while( eventType != XmlPullParser.START_DOCUMENT ){
                eventType = xpp.getEventType();
            }

            // initial connection string
            String s = "<?xml version='1.0' encoding='"+ session.getEncoding() +"' ?>";
            s += "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' "
                       +"xmlns='jabber:server' "
                       +"to='"+m_toHostName+"' "
                       +"from='"+m_serverParameters.getHostName()+"' "
                       +"id='"+session.getId()+"' "
                       +"xmlns:db='jabber:server:dialback'>";
        
            session.writeOutputStream( s );
            
            m_ready = true;
            if( m_sendVerify ){
                sendVerify( m_verifyDialbackValue, m_verifyId );
            }
            if( m_sendResult ){
                sendResult();
            }
            m_connectionHandler.process( session );
        } catch( Exception e ){
            getLogger().error( "L2R "+ m_toHostName +" session exception: "+ e.getMessage(), e );
        }
        finally{
            m_isAlive = false;
            if (m_session != null) {
                if( !m_session.isClosed() ){
                    getLogger().info( "Release session " + m_session.getId() );
                    m_sessionsManager.release( m_session );
                }
                // unlock all thread
                synchronized( m_session ){
                    m_session.notifyAll();
                }
            }
        }
    }
 
        //----------------------------------------------------------------------
    public void sendResult() throws IOException {

        if( !m_ready ){
            m_sendResult = true;   
        }
        else{

                if( m_session.getDialbackValue() == null ){
                    String dialbackValue = Long.toString( m_session.getId() );
                    m_session.setDialbackValue( dialbackValue );
                    
                    String s = "<db:result from='"
                        + m_serverParameters.getHostName()
                        + "' to='"+m_toHostName+"'>";
                    s += dialbackValue;
                    s += "</db:result>";
                    getLogger().info( "Started dialback validation for host " + m_toHostName + " id " + m_session.getId() );
                    m_session.writeOutputStream( s );
                }
            }


    }
    
    //----------------------------------------------------------------------
    public void sendVerify( String dialbackValue, String id ) throws IOException {
        if( !m_ready ){
            m_sendVerify = true;   
            m_verifyDialbackValue = dialbackValue;
            m_verifyId = id;
        }
        else{
            String s = "<db:verify from='"
                + m_serverParameters.getHostName()
                + "' to='"+m_toHostName
                + "' id='"+id
                +"'>";
            s += dialbackValue;
            s += "</db:verify>";
            m_session.writeOutputStream( s );
        }
    }
    
    
    
} // class
