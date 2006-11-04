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
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;


import java.io.IOException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceManager;

import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.DeferrableListRepositoryHolder;
import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.Deferrable;
import net.java.dev.openim.data.jabber.DeferrableTransitable;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.tools.JIDParser;

import net.java.dev.openim.log.MessageLogger;
import net.java.dev.openim.log.MessageRecorder;

import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;
import ctu.jabber.data.Packet;

/**
 *
 * @avalon.component version="1.0" name="IMRouter" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.IMRouter"
 *
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public class IMRouterImpl extends AbstractLogEnabled 
implements IMRouter, Initializable, Configurable, Serviceable {
    
    
    private Map m_sessionMap;
    private ServerParameters    m_serverParameters;
    private SessionsManager     m_sessionsManager;
    private S2SConnectorManager m_s2sConnectorManager;


    
    private DeferrableListRepositoryHolder  m_deferrableListHolder;
    private AccountRepositoryHolder         m_accountHolder;

    private int     m_deliveryRetryDelay;
    private int     m_deliveryMaxRetry;
    private long    m_deliveryMessageQueueTimeout;
    private Map     m_remoteDeliveryThreadMap;

    private ServiceManager  m_serviceManager;

    private MessageLogger   m_messageLogger;
    private MessageRecorder m_messageRecorder;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
     * @avalon.dependency type="net.java.dev.openim.session.SessionsManager:1.0" key="SessionsManager"
     * 
     * @avalon.dependency type="net.java.dev.openim.data.storage.DeferrableListRepositoryHolder:1.0"  key="DeferrableListRepositoryHolder"
     * @avalon.dependency type="net.java.dev.openim.data.storage.AccountRepositoryHolder:1.0"  key="AccountRepositoryHolder"
     *
     * @avalon.dependency type="net.java.dev.openim.log.MessageLogger:1.0"  key="MessageLogger"
     * @avalon.dependency type="net.java.dev.openim.log.MessageRecorder:1.0"  key="MessageRecorder"
     */
    public void service( ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_deferrableListHolder = (DeferrableListRepositoryHolder)serviceManager.lookup( "DeferrableListRepositoryHolder" );
        m_accountHolder = (AccountRepositoryHolder)serviceManager.lookup( "AccountRepositoryHolder" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
        m_sessionsManager = (SessionsManager)serviceManager.lookup( "SessionsManager" );
        
        m_messageLogger = (MessageLogger)serviceManager.lookup( "MessageLogger" );
        m_messageRecorder = (MessageRecorder)serviceManager.lookup( "MessageRecorder" );
    }
    
    //-------------------------------------------------------------------------
    public void configure(Configuration configuration) throws ConfigurationException {
        m_deliveryMessageQueueTimeout = configuration.getChild( "delivery-message-queue-timeout" ).getValueAsLong( 1000*60*60 );
        m_deliveryMaxRetry = configuration.getChild( "delivery-max-retry" ).getValueAsInteger( 3 );
        m_deliveryRetryDelay = configuration.getChild( "delivery-retry-delay" ).getValueAsInteger( 500 );
        getLogger().info( "Router having delivery max retry: "+m_deliveryMaxRetry+" and delay " + m_deliveryRetryDelay );
    }
    
    //-------------------------------------------------------------------------
    public void initialize() throws java.lang.Exception {
        //m_validHost = new HashSet();
        m_sessionMap = new HashMap();   
        m_remoteDeliveryThreadMap = new HashMap();
    }
    //-------------------------------------------------------------------------
    public S2SConnectorManager getS2SConnectorManager(){
            return m_s2sConnectorManager;
    }
    //-------------------------------------------------------------------------
    public void setS2SConnectorManager( S2SConnectorManager s2sConnectorManager ){
            m_s2sConnectorManager = s2sConnectorManager;
    }

    //-------------------------------------------------------------------------
    public void registerSession( final IMClientSession session ) {
        
        final User user = session.getUser();
            
        if( session.getConnectionType() == IMSession.C2S_CONNECTION && user != null ){
            getLogger().debug( "Session map before register : " + m_sessionMap );
            getLogger().debug( "Register session user: " + user.getNameAndRessource() + " session id " + session.getId() );
            try{ 
                IMSession prevSession = (IMSession)m_sessionMap.get( user.getNameAndRessource() );
                if( prevSession != null ){
                    getLogger().debug( "Allready register session: " + prevSession.getId() );
                    m_sessionsManager.release( prevSession );
                }
            } catch( Exception e ){
                getLogger().error( e.getMessage(), e );
            }
            m_sessionMap.put( user.getNameAndRessource(), session );

            try {
                deliverQueueMessage( session, user.getName() );
            } catch( Exception e ){
                getLogger().warn( "Failed to deliver queue message " + e.getMessage(), e );
            }
            

        } // if
        
    }
    
    
    //-------------------------------------------------------------------------
    public void unregisterSession( final IMClientSession session ) {
        if( session instanceof IMClientSession ){
            User user = ((IMClientSession)session).getUser();
            if( user != null ){
                getLogger().debug( "Unregister register session user: " + user.getJIDAndRessource() + " session id " + session.getId() );
                m_sessionMap.remove( user.getNameAndRessource() );
                //m_sessionMap.remove( user.getName() );
            }
        }
    }
    
    //-------------------------------------------------------------------------    
    public List getAllRegisteredSession( final String name ){

        List list = new ArrayList( 1 );
        final String[] nameArray = (String[])m_sessionMap.keySet().toArray( new String[0] );
        for( int i=0, l=nameArray.length; i<l; i++ ){
            getLogger().debug( "Check if " + name + " could match " + nameArray[i] );
            if( nameArray[i].startsWith( name ) ){
                list.add( m_sessionMap.get( nameArray[i] ) );
            }
        } // for
        return list;
    }
    //-------------------------------------------------------------------------
    private IMClientSession getRegisteredSession( final String name ){
        IMClientSession session = (IMClientSession)m_sessionMap.get( name );
        getLogger().debug( ">>> getting session for " + name + " having map key " + m_sessionMap.keySet() );
        if( session == null ){
            String username = name;
            if( name.indexOf( '/' ) > 0 ){
                // we have a ressource => get the login
                username = JIDParser.getName( name );
            }

            List list = getAllRegisteredSession( name );
            for( int i=0, l=list.size(); i<l; i++ ){
                IMClientSession s = (IMClientSession)list.get( i );
                if( session == null || 
                    ( getPriorityNumber( s ) > getPriorityNumber( session ) ) ){
                    session = s;
                    getLogger().debug( "Select session " + s );
                }
            } // for
        } // if
        
        return session;
    }
    //-------------------------------------------------------------------------
    private final int getPriorityNumber( IMClientSession session ){
        int priorityNumber = 0;
        if( session.getPresence() != null ){
            String priorityStr = session.getPresence().getPriority();

            if( priorityStr != null ){
                try{
                    priorityNumber = Integer.parseInt( priorityStr );
                }
                catch( Exception e ){
                    getLogger().error( e.getMessage(), e );
                }
            }
        }
        return priorityNumber;
    }
    //-------------------------------------------------------------------------
    public void route( final IMSession currentSession, final Transitable transit )
    throws java.io.IOException {
        final String to  = transit.getTo();        
        //final String from = transit.getFrom();
        final String toHostname = JIDParser.getHostname( to );
        
        if( m_serverParameters.getHostNameList().contains( toHostname ) ){ // local delivery
    
            final IMClientSession session = getRegisteredSession( JIDParser.getNameAndRessource( to ) );
            
            if( session == null ){
                if( transit instanceof Deferrable ){
                    
                    final String username = JIDParser.getName( to );
                    Account account = m_accountHolder.getAccount( username );
                    if( account == null ){
                        getLogger().debug( to + " unknown user. Transit value was: " + transit );
                        String from = transit.getFrom();
                        transit.setError( "Not Found" );
                        transit.setErrorCode( 404 );
                        transit.setFrom( to );
                        transit.setTo( from );
                        transit.setType( Transitable.TYPE_ERROR );
                
                        m_messageLogger.log( transit );
                        if (currentSession != null) {
                            currentSession.writeOutputStream(transit);
                        }
                        m_messageLogger.log( transit );
                        m_messageRecorder.record( transit );
                        
                    }
                    else{
                        getLogger().debug( to + " is not connected for getting message, should store for offline dispatch. Transit value was: " + transit );

                        List list = m_deferrableListHolder.getDeferrableList( username );
                        if( list == null ){
                            list = new ArrayList();
                        }
                        Transitable transitToStore = transit;
                        if (transit instanceof Packet) {
                            transitToStore = new DeferrableTransitable((Packet)transit);
                        }
                        list.add( transitToStore );
                        m_deferrableListHolder.setDeferrableList( username, list );
                    } // if else
                } // if
            } // if
            else {
                transit.setTo( session.getUser().getJIDAndRessource() );
                session.writeOutputStream( transit.toString() );
                m_messageLogger.log( transit );
                m_messageRecorder.record( transit );                
            } // else
        } // if
        
        else { // remote delivery
            getLogger().debug( "Remote delivery to " + transit.getTo() );
            enqueueRemoteDelivery( transit, currentSession );
            getLogger().debug( "Enqueued to " + transit.getTo() );
            //new Thread( new AsyncDeliverer( transit, toHostname, currentSession ) ).start();
        }
        
    }
   
    //-------------------------------------------------------------------------
    public void deliverQueueMessage( IMSession currentSession, String username ) 
    throws java.io.IOException {
        final List list = m_deferrableListHolder.getDeferrableList( username );
        if( list != null ){
            for( int i=0, l=list.size(); i<l; i++ ){
                route( currentSession, (Transitable)list.get( i ) );
            }
        }
        // empty list
        m_deferrableListHolder.setDeferrableList( username, new ArrayList() );
        
    }
    
    
    //-------------------------------------------------------------------------
    private void enqueueRemoteDelivery( Transitable transitable, IMSession session ){
        TransitableAndSession tas = new TransitableAndSession( transitable, session );

        final String hostname = tas.getHostname();
        synchronized( m_remoteDeliveryThreadMap ){
            RemoteDeliveryThreadPerHost remoteDeliveryThread = (RemoteDeliveryThreadPerHost)m_remoteDeliveryThreadMap.get( hostname );
            if( remoteDeliveryThread == null ){
                // should get from a pool (to implem later)
                if( hostname == null ){
                    getLogger().warn( "Absurd hostname for Transitable " + transitable );
                }

                remoteDeliveryThread = new RemoteDeliveryThreadPerHost( hostname );
                remoteDeliveryThread.enqueue( tas );

                remoteDeliveryThread.start();
                m_remoteDeliveryThreadMap.put( hostname, remoteDeliveryThread );
            }

            else{
                remoteDeliveryThread.enqueue( tas );
            }
        } // sync
    }
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public class TransitableAndSession {
        private Transitable m_transitable;
        private IMSession m_session;
        public TransitableAndSession( Transitable transitable, IMSession session ){
            m_transitable = transitable;
            m_session = session;
        }
        public Transitable getTransitable(){
            return m_transitable;
        }
        public IMSession getSession(){
            return m_session;
        }
        public String getHostname(){
            return JIDParser.getHostname( m_transitable.getTo() );
        }
    }
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public class RemoteDeliveryThreadPerHost extends Thread {
        private LinkedList m_perHostRemoteDeliveryQueue;
        private IMSession m_remoteSession = null;
        private String m_hostname;
        private String m_currentStatus;

        
        //----------------------------------------------------------------------
        public RemoteDeliveryThreadPerHost( String hostname ){
            m_hostname = hostname;
            m_perHostRemoteDeliveryQueue = new LinkedList();
            m_currentStatus = "";
        }
        
        //----------------------------------------------------------------------
        public void enqueue( TransitableAndSession tas ){
            synchronized( m_perHostRemoteDeliveryQueue ){
                getLogger().debug( "Adding tas for " + m_hostname + " this thread ("+this+") isAlive: " + isAlive() + " current status: " + m_currentStatus);
                m_perHostRemoteDeliveryQueue.add( tas );
            }
            synchronized( this ){
                notify();
            }
            
        }
        
        //----------------------------------------------------------------------
        public void run(){
            m_currentStatus = "Started";
            getLogger().debug( "Starting thread " + this );
            while( true ){
                TransitableAndSession tas = null;
                synchronized( m_perHostRemoteDeliveryQueue ){
                    tas = (TransitableAndSession)m_perHostRemoteDeliveryQueue.removeFirst();
                    getLogger().debug( "Remove tas for " + m_hostname );
                }

                if( tas != null ){
                    deliver( tas );
                    getLogger().debug( "Delivered tas for " + m_hostname );
                }
                
                
                synchronized( this ){
                    if( m_perHostRemoteDeliveryQueue.isEmpty() ){
                        try{
                            getLogger().debug( "Thread ("+this+"/"+m_hostname+") wait "+ m_deliveryMessageQueueTimeout);
                            wait( m_deliveryMessageQueueTimeout );
                            getLogger().debug( "Thread ("+this+"/"+m_hostname+") awake" );
                        } catch( InterruptedException e ){
                            getLogger().warn( e.getMessage(), e );
                        }
                    }
                }
                
                synchronized( m_remoteDeliveryThreadMap ){
                    if( m_perHostRemoteDeliveryQueue.isEmpty() ){
                        getLogger().debug( "Removing thread ("+this+"/"+m_hostname+") from list");

                        RemoteDeliveryThreadPerHost remoteDeliveryThread = (RemoteDeliveryThreadPerHost)m_remoteDeliveryThreadMap.remove( m_hostname );
                        // should get back to pool (to implem later)
                        break;
                    } // if
                }//sync

            } // while true
            
            // cleanup
            //m_validHost.remove( m_hostname );
            m_sessionsManager.release( m_remoteSession );
            m_remoteSession = null;
            
            
            m_currentStatus = "Ended";
            getLogger().debug( "Ending thread " + this );
        } // run
        
        //----------------------------------------------------------------------
        private void deliver( TransitableAndSession tas ) {
            Transitable transitable = tas.getTransitable();
            try{
                boolean failedToDeliver = true;
                for( int retry=0; retry<m_deliveryMaxRetry; retry++ ){
                    try{
                        getLogger().debug( "Trying to send ("+transitable+") to hostname "+m_hostname+" step " + retry );
                        if( m_remoteSession == null || m_remoteSession.isClosed() ){
                            m_remoteSession = m_s2sConnectorManager.getRemoteSessionWaitForValidation( m_hostname, m_deliveryMessageQueueTimeout );
                        }
                        
                        m_remoteSession.writeOutputStream( transitable.toString() );
                        m_messageLogger.log( transitable );
                        m_messageRecorder.record( transitable );
                        getLogger().debug( "Sent ("+transitable+") to hostname "+m_hostname+" step " + retry );                        
                        failedToDeliver = false;
                        break;
                    } catch( java.net.SocketException e ){
                        m_sessionsManager.release( m_remoteSession );
                        m_remoteSession = null;
                        temporise( e );
                    } catch( java.io.IOException e ){
                        m_sessionsManager.release( m_remoteSession );
                        m_remoteSession = null;
                        temporise( e );
                    } catch( Exception e ){
                        m_sessionsManager.release( m_remoteSession );
                        m_remoteSession = null;
                        //m_validHost.remove( m_hostname );
                        getLogger().warn( "Remote send failed " + e.getMessage(), e );
                        break;
                    }

                } // for

                
                if( failedToDeliver ){
                    String to = transitable.getTo(); 
                    getLogger().info( "Failed to sent (from "+transitable.getFrom()+") to hostname "+m_hostname);
                    String from = transitable.getFrom();
                    transitable.setError( "Delivery failed" );
                    transitable.setErrorCode( 500 );
                    transitable.setFrom( to );
                    transitable.setTo( from );
                    transitable.setType( Transitable.TYPE_ERROR );

                    try{
                        if (tas.getSession() != null) {
                            tas.getSession().writeOutputStream( transitable.toString() );
                            m_messageLogger.log( transitable );
                            m_messageRecorder.record( transitable );
                        }
                    } catch( IOException e ){
                        getLogger().warn( "Error delivery failed " + e.getMessage(), e );
                    }

                } // if
            
            } catch( Exception e ){
               getLogger().warn( e.getMessage(), e );
            }            
        } // deliver
        
        //----------------------------------------------------------------------
        private final void temporise( Exception e ){
            getLogger().warn( "Remote send failed (retying in "+m_deliveryRetryDelay+ "ms) " + e.getMessage() );                        
            m_sessionsManager.release( m_remoteSession );
            m_remoteSession = null;

            try{
                sleep( m_deliveryRetryDelay );
            }catch( InterruptedException ie ){ getLogger().debug( ie.getMessage(), ie ); }
            // we retry
        }
        //----------------------------------------------------------------------
    
        
    }

   public void releaseSessions() {
      getLogger().debug( "Releasing sessions " );
      Iterator it = m_sessionMap.values().iterator();
      while ( it.hasNext()) {
         IMSession sess = (IMSession)it.next();
         m_sessionsManager.release( sess );
      } // end of while ()
   }
}


