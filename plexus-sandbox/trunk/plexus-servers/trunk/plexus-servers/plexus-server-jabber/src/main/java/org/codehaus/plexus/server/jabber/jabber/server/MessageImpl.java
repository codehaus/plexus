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
package org.codehaus.plexus.server.jabber.jabber.server;


import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;

import org.codehaus.plexus.server.jabber.IMRouter;
import org.codehaus.plexus.server.jabber.data.jabber.MessagePacket;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="server.Message" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.server.Message"
 */
public class MessageImpl
    extends DefaultSessionProcessor
    implements Message
{

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Subject:1.0" key="server.Subject"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Body:1.0" key="server.Body"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Thread:1.0" key="server.Thread"
     */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        super.service( serviceManager );
    }

    //-------------------------------------------------------------------------

    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        XmlPullParser xpp = session.getXmlPullParser();

        for ( int i = 0, l = xpp.getAttributeCount(); i < l; i++ )
        {
            getLogger().debug( "Attribut ns: " + xpp.getAttributeNamespace( i ) + " name: " +
                xpp.getAttributeName( i ) + " value: " + xpp.getAttributeValue( i ) );
        }

        MessagePacket message = new MessagePacket();
        message.setTo( xpp.getAttributeValue( "", "to" ) );
        message.setType( xpp.getAttributeValue( "", "type" ) );

        if ( session.getConnectionType() == IMSession.C2S_CONNECTION )
        {
            message.setFrom( ( (IMClientSession) session ).getUser().getJIDAndRessource() );
        }
        else
        {
            message.setFrom( xpp.getAttributeValue( "", "from" ) );
        }


        super.process( session, message );


        IMRouter router = session.getRouter();
        router.route( session, message );

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


