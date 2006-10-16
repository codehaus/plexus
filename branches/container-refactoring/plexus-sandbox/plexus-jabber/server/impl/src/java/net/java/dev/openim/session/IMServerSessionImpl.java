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
package net.java.dev.openim.session;


import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Initializable;


import net.java.dev.openim.ServerParameters;


/**
 *
 * @avalon.component version="1.0" name="IMServerSession" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.session.IMServerSession"
 *
 * @version 1.0
 * @author AlAg
 */
public class IMServerSessionImpl extends AbstractIMSession
implements IMServerSession, Initializable, Serviceable, Configurable
{
    
    private ServerParameters m_serverParameters;
    private ServiceManager  m_serviceManager;
    private Configuration   m_configuration;
    
    private String          m_remoteHostname;
    
    
    private volatile boolean    m_dialbackValid;
    private volatile String         m_dialbackValue;
    private IMServerSession       m_twinSession;
    
    
    //-------------------------------------------------------------------------
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        m_configuration = configuration;
        m_defaultEncoding = configuration.getChild( "default-encoding" ).getValue( "UTF-8" );
    }

    //-------------------------------------------------------------------------

    
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters"  key="ServerParameters"
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
    }
    

    public void initialize() throws java.lang.Exception {
        m_dialbackValid = false;
        m_disposed = new Boolean( false );
        synchronized( m_lastSessionId ){
            m_sessionId = m_lastSessionId.longValue();
            m_lastSessionId = new Long( m_sessionId + 1 );
        }
    }
    
    //-------------------------------------------------------------------------
    public void close() {
        getLogger().debug( "Closing session id " + getId() );

        
        synchronized( m_disposed ){
            m_dialbackValid = false;
            m_dialbackValue = null;
            try{
                if( m_twinSession != null ){
                    m_twinSession.setTwinSession( null );
                }

            } catch( Exception e ){
                getLogger().warn( "Session dispose failed (stage1): " + e.getMessage(), e );
            }


            try{
                writeOutputStream( "</stream:stream>" );
            } catch( Exception e ){
                getLogger().warn( "Session dispose failed (stage2): " + e.getMessage() );
            }


            try{
                getLogger().debug( "Session " + m_sessionId + " closed" );
                
                if( m_socket != null && !m_socket.isClosed() ){
                    m_socket.close();
                    m_outputStreamWriter.close();
                }
            } catch( Exception e ){
                getLogger().warn( "Session dispose failed (stage3): " + e.getMessage(), e );
            }
            getLogger().debug( "Session " + m_sessionId + " disposed " );
        } // synchro
        m_disposed = new Boolean( true );
    }    
    
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public boolean getDialbackValid(){
        return m_dialbackValid;
    }
    //-------------------------------------------------------------------------
    public void setDialbackValid( boolean value ){
        int ctype = getConnectionType();
        if( ctype == S2S_R2L_CONNECTION || ctype == S2S_L2R_CONNECTION ){
            m_dialbackValid = value;
        }
    }
    //-------------------------------------------------------------------------
    public String getDialbackValue(){
        return m_dialbackValue;
    }
    
    //-------------------------------------------------------------------------
    public void setDialbackValue( String dialback ){
        m_dialbackValue = dialback;
    }
    //-------------------------------------------------------------------------
    public IMServerSession getTwinSession(){
        return m_twinSession;
    }
    //-------------------------------------------------------------------------
    public void setTwinSession( IMServerSession session ){
        m_twinSession = session;
    }
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public final String getRemoteHostname() {
        return m_remoteHostname;
    }    
    //-------------------------------------------------------------------------
    public final void setRemoteHostname(final String remoteHostname) {
        m_remoteHostname = remoteHostname;
    }
    
    
    //-------------------------------------------------------------------------
    public int getConnectionType() {
        int type = UNKNOWN_CONNECTION;
        
        if( m_socket != null ){
             if( m_socket.getLocalPort() == m_serverParameters.getLocalServerPort() 
                || m_socket.getLocalPort() == m_serverParameters.getLocalSSLServerPort() ){
                type = S2S_R2L_CONNECTION;
            }
            else if( m_socket.getPort() == m_serverParameters.getRemoteServerPort() ){
                type = S2S_L2R_CONNECTION;
            }
        }
        
        return type;
    }
    
    
}

