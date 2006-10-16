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
package net.java.dev.openim.jabber.server;



import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.DefaultSessionProcessor;

import net.java.dev.openim.IMRouter;
import net.java.dev.openim.data.jabber.MessagePacket;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;


/**
 * @avalon.component version="1.0" name="server.Message" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.server.Message"
 *
 * @version 1.0
 * @author AlAg
 */
public class MessageImpl extends DefaultSessionProcessor implements Message {
    
    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.server.Subject:1.0" key="server.Subject"
     * @avalon.dependency type="net.java.dev.openim.jabber.server.Body:1.0" key="server.Body"
     * @avalon.dependency type="net.java.dev.openim.jabber.server.Thread:1.0" key="server.Thread"
      */
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        super.service( serviceManager );
    }

    //-------------------------------------------------------------------------
   
    public void process( final IMSession session, final Object context ) throws Exception{
        
        XmlPullParser xpp = session.getXmlPullParser();

        for( int i=0, l=xpp.getAttributeCount(); i<l; i++ ){
            getLogger().debug( "Attribut ns: "+ xpp.getAttributeNamespace( i ) + " name: " + xpp.getAttributeName( i ) + " value: " + xpp.getAttributeValue( i ) ); 
        }
        
        MessagePacket message = new MessagePacket();
        message.setTo( xpp.getAttributeValue( "", "to" ) );
        message.setType( xpp.getAttributeValue( "", "type" ) );
        
        if( session.getConnectionType() == IMSession.C2S_CONNECTION ){
            message.setFrom( ((IMClientSession)session).getUser().getJIDAndRessource() );
        }
        else{
            message.setFrom( xpp.getAttributeValue( "", "from" ) );
        }
        

        super.process( session, message );

        
        IMRouter router = session.getRouter();
        router.route( session, message );
        
/*        
        String iqMsg = session.getMessageData().getId();
        
        String s = "<iq type='"+IqData.TYPE_RESULT+"' id='"+iqId+"'>"
                +"<query xmlns='jabber:iq:roster'>"
                +"<item jid='romeo@montague.net' name='Romeo' subscription='both'/>"
                +"</query></iq>";

                
        session.writeOutputStream( s );            
*/
    }

}


