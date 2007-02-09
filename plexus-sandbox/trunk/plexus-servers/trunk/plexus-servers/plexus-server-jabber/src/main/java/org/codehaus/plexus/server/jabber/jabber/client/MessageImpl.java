// very simple delivery tree added (it looks for destination hostname and decides who will process msg)
// server now routes not processed elements
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
package org.codehaus.plexus.server.jabber.jabber.client;


import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.*;
import org.codehaus.plexus.server.jabber.tools.JIDParser;
import org.codehaus.plexus.server.jabber.module.ServerModule;
import org.codehaus.plexus.server.jabber.data.jabber.MessagePacket;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="client.Message" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.client.Message"
 */
public class MessageImpl
    extends DefaultSessionProcessor
    implements Message
{

    private ServerParameters m_serverParameters;
    private ModuleManager m_moduleManager;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Subject:1.0" key="client.Subject"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Body:1.0" key="client.Body"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.client.Thread:1.0" key="client.Thread"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.ModuleManager:1.0" key="ModuleManager"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.ServerParameters:1.0"  key="ServerParameters"
     */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        super.service( serviceManager );

        m_serverParameters = (ServerParameters) serviceManager.lookup( "ServerParameters" );
        m_moduleManager = (ModuleManager) serviceManager.lookup( "ModuleManager" );
    }

    //-------------------------------------------------------------------------

    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        ServerModule module = null;

        XmlPullParser xpp = session.getXmlPullParser();

        MessagePacket message = new MessagePacket( xpp );

        if ( session instanceof IMClientSession )
        {
            if ( message.getFrom().length() == 0 )
            {
                String from = ( (IMClientSession) session ).getUser().getJIDAndRessource();
                message.setFrom( from );
            }
            // Handle ping your self
            if ( message.getTo().length() == 0 )
            {
                String to = ( (IMClientSession) session ).getUser().getJIDAndRessource();
                message.setTo( to );
            }
        }

        if ( ( module = m_moduleManager.getModuleByHostname( JIDParser.getHostname( message.getTo() ) ) ) != null )
        {
            // the received packet is for some server module
            SessionProcessor processor =
                module.getProcessor( getEventName( session, xpp.getNamespace(), xpp.getName() ) );
            if ( processor != null )
            {
                processor.process( session, message );
            }

        }
        else if ( !m_serverParameters.getHostNameList().contains( message.getTo() ) )
        {
            // the received packet is not for the server or its modules
            super.process( session, message );
            if ( this.m_notProcessedData != null )
            {
                message.addSerializedChild( this.m_notProcessedData.getBuffer().toString() );
            }
            IMRouter router = session.getRouter();
            router.route( session, message );
        }

/*
        String iqMsg = session.getMessageData().getId();
        
        String s = "<iq type='"+IqData.TYPE_RESULT+"' id='"+iqId+"'>"
                +"<query xmlns='jabber:iq:roster'>"
                +"<item jid='romeo@montague.org' name='Romeo' subscription='both'/>"
                +"</query></iq>";

                
        session.writeOutputStream( s );            
*/
    }

}


