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


import java.util.Map;
import java.util.HashMap;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;



import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.session.IMServerSession;

import org.apache.avalon.framework.activity.Initializable;



/**
 *
 * @avalon.component version="1.0" name="S2SConnectorManager" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.S2SConnectorManager"
 *
 * @version 1.0
 * @author AlAg
 */

public class S2SConnectorManagerImpl extends AbstractLogEnabled 
implements S2SConnectorManager, Initializable, Serviceable {
    
    private Map                 m_hostnameAndS2SMap;
    
    private ServiceManager  m_serviceManager;
    
    private IMConnectionHandler m_connectionHandler;
    private IMRouter                    m_router;
    private SessionsManager         m_sessionsManager;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.S2SConnector:1.0"  key="S2SConnector"
     * @avalon.dependency type="net.java.dev.openim.session.SessionsManager:1.0" key="SessionsManager"
     * @avalon.dependency type="net.java.dev.openim.IMRouter:1.0" key="IMRouter"
     */
    public void service( ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_sessionsManager = (SessionsManager)serviceManager.lookup( "SessionsManager" );
        m_router = (IMRouter)serviceManager.lookup( "IMRouter" );        
    }
    
    
    //-------------------------------------------------------------------------
    public void initialize() throws java.lang.Exception {
        m_hostnameAndS2SMap = new HashMap();
    }


    
    
    //-------------------------------------------------------------------------    
    public void setConnectionHandler( IMConnectionHandler connectionHandler ){
        m_connectionHandler = connectionHandler;
    }


    //-------------------------------------------------------------------------
    public IMServerSession getCurrentRemoteSession( String hostname ) throws Exception {
        IMServerSession session = null;
        synchronized( m_hostnameAndS2SMap ){
            S2SConnector s2s = (S2SConnector)m_hostnameAndS2SMap.get( hostname );
            if( s2s != null && !s2s.getSession().isClosed() ){
                session = s2s.getSession();
            }
        }
        return session;
    }
    //----------------------------------------------------------------------
    public IMServerSession getRemoteSessionWaitForValidation( String hostname, long timeout )  throws Exception {
        
        IMServerSession session = null;
        S2SConnector s2s = null;
        synchronized( m_hostnameAndS2SMap ){
            s2s = (S2SConnector)m_hostnameAndS2SMap.get( hostname );
            if( s2s != null && !s2s.getSession().isClosed() ){
                session = s2s.getSession();
            }
            else{
                s2s = getS2SConnector( hostname );
                session = s2s.getSession();

            }
        }
            
        synchronized( session ){
            // wait for validation
            if(  !session.getDialbackValid()  ){
                s2s.sendResult();
                getLogger().info( "Wait validation for " + hostname + " for session " + session );
                session.wait( timeout );
            }
        }
         if( !session.getDialbackValid() ){
            throw new Exception( "Unable to get dialback validation for " + hostname +" after timeout " + timeout + " ms" );
        } 
        getLogger().info( "Validation granted from " + hostname + " for session " + session );

        return session;
        
    } // getremote session
    

    //-------------------------------------------------------------------------
    public void verifyRemoteHost( String hostname, String dialbackValue, String id, IMServerSession session ) throws Exception{
        
        S2SConnector s2s = getS2SConnector( hostname );
        
        
        s2s.sendVerify( dialbackValue, id );
        if( !s2s.getSession().getDialbackValid() ){
            s2s.sendResult();
        }
        
        session.setTwinSession( s2s.getSession() );
        s2s.getSession().setTwinSession( session );
        
    }


    //-------------------------------------------------------------------------
    private S2SConnector getS2SConnector( String hostname ) throws Exception {
        S2SConnector s2s = null;
        synchronized( m_hostnameAndS2SMap ){
            s2s = (S2SConnector)m_hostnameAndS2SMap.get( hostname );
            
            if( s2s != null && !s2s.isAlive() ){
                getLogger().info( "Removing s2s for hostname (thread not alive) " + hostname );
                m_hostnameAndS2SMap.remove( hostname );
                s2s = null;
            }
            
            if( s2s == null || s2s.getSession().isClosed() ){
                s2s = (S2SConnector)m_serviceManager.lookup( "S2SConnector" );
                s2s.setIMConnectionHandler( m_connectionHandler );
                s2s.setRouter( m_router );
                s2s.setSessionsManager( m_sessionsManager );
                s2s.setToHostname( hostname );
                new Thread( s2s ).start();
                m_hostnameAndS2SMap.put( hostname, s2s );
            }
        }
        return s2s;        
    }
    


} // class
