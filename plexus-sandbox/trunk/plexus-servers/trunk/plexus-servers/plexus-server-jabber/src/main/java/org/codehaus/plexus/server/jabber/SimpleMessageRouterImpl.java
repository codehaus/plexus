/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber;


import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

import org.codehaus.plexus.server.jabber.data.jabber.MessagePacket;

import org.codehaus.plexus.server.jabber.session.IMSession;
import org.codehaus.plexus.server.jabber.session.SessionsManager;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="SimpleMessageRouter" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.SimpleMessageRouter"
 * @avalon.dependency type="org.codehaus.plexus.server.jabber.IMRouter:1.0" key="IMRouter"
 */
public class SimpleMessageRouterImpl
    extends AbstractLogEnabled
    implements SimpleMessageRouter, Serviceable
{


    private ServiceManager m_serviceManager;

    private SessionsManager m_sessionsManager;
    private IMRouter m_router;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.session.SessionsManager:1.0" key="SessionsManager"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.IMRouter:1.0" key="IMRouter"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.IMConnectionHandler:1.0" key="IMConnectionHandler"
     */
    public void service( ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        m_serviceManager = serviceManager;
        m_sessionsManager = (SessionsManager) serviceManager.lookup( "SessionsManager" );
        m_router = (IMRouter) serviceManager.lookup( "IMRouter" );
        IMConnectionHandler connectionHandler = (IMConnectionHandler) serviceManager.lookup( "IMConnectionHandler" );
    }

    public void route( final String from,
                       final String to,
                       final String type,
                       final String subject,
                       final String body,
                       final String threadId )
        throws java.io.IOException
    {

        MessagePacket message = new MessagePacket();
        message.setFrom( from );
        message.setTo( to );
        message.setSubject( subject );
        message.setBody( body );
        message.setThread( threadId );
        message.setType( type );

        IMSession session = null;
        try
        {
            session = m_sessionsManager.getNewClientSession();
            session.setRouter( m_router );
            m_router.route( session, message );
        }
        catch ( Exception e )
        {
            getLogger().warn( e.getMessage(), e );
        }

    }


}


