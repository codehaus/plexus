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




import java.util.List;



import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Initializable;


import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.IMPresenceHolder;
import net.java.dev.openim.data.UsersManager;
import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.data.jabber.IMPresenceImpl;
import net.java.dev.openim.data.jabber.IMRosterItem;


//import net.java.dev.openim.tools.InputStreamDebugger;

/**
 *
 * @avalon.component version="1.0" name="IMClientSession" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.session.IMClientSession"
 *
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public class IMClientSessionImpl extends AbstractIMSession 
 implements IMClientSession, Initializable, Serviceable, Configurable
{
    
    private ServerParameters m_serverParameters;
    private ServiceManager  m_serviceManager;
    private Configuration   m_configuration;
    private UsersManager  m_usersManager;
    
    private User            	m_user;
    private IMPresence      m_presence;
    private IMPresenceHolder  m_presenceHolder;
    
    
    //-------------------------------------------------------------------------
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        m_configuration = configuration;
        m_defaultEncoding = configuration.getChild( "default-encoding" ).getValue( "UTF-8" );
    }

    //-------------------------------------------------------------------------

    
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters"  key="ServerParameters"
     * @avalon.dependency type="net.java.dev.openim.data.UsersManager"  key="UsersManager"
     * @avalon.dependency type="net.java.dev.openim.IMPresenceHolder"  key="IMPresenceHolder"
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_usersManager = (UsersManager)m_serviceManager.lookup( "UsersManager" );
        m_presenceHolder = (IMPresenceHolder)serviceManager.lookup( "IMPresenceHolder" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
    }
    

    public void initialize() throws java.lang.Exception {
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
	 
			try{
			    // set disconnected to all roster friend
			    if( m_user != null && getConnectionType() == IMSession.C2S_CONNECTION ){
			        IMPresence presence = m_presenceHolder.removePresence( m_user.getJIDAndRessource() );
			        getLogger().debug( "Remove presence jid " + m_user.getJIDAndRessource() );
			
			        // emit unavailaible to all user
			        presence = new IMPresenceImpl();
			        presence.setFrom( m_user.getJIDAndRessource() );
			        presence.setType( IMPresence.TYPE_UNAVAILABLE );
			        presence.setStatus( "Disconnected" );
			        List rosterList = m_user.getRosterItemList();
			        if( rosterList != null ){
			            for( int i=0, l=rosterList.size(); i<l; i++ ){
			                IMRosterItem item = (IMRosterItem)rosterList.get( i );
			                getLogger().debug( "Item " +  item );
			                IMPresence localPresence = (IMPresence)presence.clone();
			                localPresence.setTo( item.getJID() );
			                if( m_router != null ){
			                    m_router.route( this, localPresence );
			                }
			            }
			        }
			
			    }
		
		
			    if( m_router != null ){
			        m_router.unregisterSession( this );
			    }
			
			} catch( Exception e ){
			    getLogger().warn( "Session dispose failed (stage1): " + e.getMessage(), e );
			}
			
			
			try{
                           if ( m_streams instanceof net.java.dev.openim.jabber.FlashStreams) {
                              // I think....
			    writeOutputStream( "<flash:stream/>" );
                           } else {
                                                          
			    writeOutputStream( "</stream:stream>" );
                           } // end of else

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
		}
		
		m_disposed = new Boolean( true );

    }    
    
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public final void setUser(final User user) {
        m_user = user;
    }
    
    //-------------------------------------------------------------------------
    public final User getUser() {
        return m_user;
    }
    //-------------------------------------------------------------------------
    public IMPresence getPresence() {
        return m_presence;
    }

    public int getPriority() {
        int priorityNumber = 0;
        try {
            priorityNumber = Integer.parseInt(getPresence().getPriority());
        } catch (Exception e) {};
        return priorityNumber;
    }
    
    //-------------------------------------------------------------------------
    public void setPresence(IMPresence presence) {
        m_presence = presence;
    }
    
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    public int getConnectionType() {
        return C2S_CONNECTION;
    }
    
}

