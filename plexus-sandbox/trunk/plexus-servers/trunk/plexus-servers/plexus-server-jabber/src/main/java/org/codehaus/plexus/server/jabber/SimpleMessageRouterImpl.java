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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.server.jabber.data.jabber.MessagePacket;
import org.codehaus.plexus.server.jabber.session.IMSession;
import org.codehaus.plexus.server.jabber.session.SessionsManager;

import java.io.IOException;

/**
 * @author AlAg
 * @version 1.0
 * @plexus.component
 */
public class SimpleMessageRouterImpl
    extends AbstractLogEnabled
    implements SimpleMessageRouter
{
    /** @plexus.requirement */
    private SessionsManager m_sessionsManager;

    /** @plexus.requirement */
    private IMRouter m_router;

    public void route( final String from,
                       final String to,
                       final String type,
                       final String subject,
                       final String body,
                       final String threadId )
        throws IOException
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


