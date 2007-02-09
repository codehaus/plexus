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




import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

import net.java.dev.openim.data.jabber.MessagePacket;

import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.session.SessionsManager;



/**
 *
 * @avalon.component version="1.0" name="SimpleMessageRouter" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.SimpleMessageRouter"
 * @avalon.dependency type="net.java.dev.openim.IMRouter:1.0" key="IMRouter"
 *
 * @version 1.0
 * @author AlAg
 */
public class SimpleMessageRouterImpl extends AbstractLogEnabled 
implements SimpleMessageRouter, Serviceable {
    

    private ServiceManager m_serviceManager;
    
    private SessionsManager m_sessionsManager;
    private IMRouter m_router;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.session.SessionsManager:1.0" key="SessionsManager"
     * @avalon.dependency type="net.java.dev.openim.IMRouter:1.0" key="IMRouter"
     * @avalon.dependency type="net.java.dev.openim.IMConnectionHandler:1.0" key="IMConnectionHandler"
     */
    public void service( ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
        m_sessionsManager = (SessionsManager)serviceManager.lookup( "SessionsManager" );
        m_router = (IMRouter)serviceManager.lookup( "IMRouter" );
        IMConnectionHandler connectionHandler = (IMConnectionHandler)serviceManager.lookup( "IMConnectionHandler" );
    }
    
    public void route( final String from, final String to, final String type, 
            final String subject, final String body, final String threadId ) 
            throws java.io.IOException {
                
        MessagePacket message = new MessagePacket();
        message.setFrom( from );
        message.setTo( to );
        message.setSubject( subject );
        message.setBody( body );
        message.setThread( threadId );
        message.setType( type );
        
        IMSession session = null;
        try{
            session = m_sessionsManager.getNewClientSession();
            session.setRouter( m_router );
            m_router.route( session, message );
        } catch( Exception e ){
            getLogger().warn( e.getMessage(), e );
        }
        
    }    

    
 
}


