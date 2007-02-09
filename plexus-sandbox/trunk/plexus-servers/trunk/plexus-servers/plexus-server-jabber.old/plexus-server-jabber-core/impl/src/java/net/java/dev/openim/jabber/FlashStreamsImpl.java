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
package net.java.dev.openim.jabber;


import org.apache.avalon.framework.service.ServiceManager;

import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.session.IMSession;

/**
 * @avalon.component version="1.0" name="FlashStreams" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.FlashStreams"
 *
 * @version 1.0
 * @author Pra
 */
 

public class FlashStreamsImpl extends StreamsImpl implements FlashStreams{
   
	//boolean m_inTag = false;
    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.Error:1.0" key="Error"
     *
     * @avalon.dependency type="net.java.dev.openim.jabber.client.Iq:1.0" key="client.Iq"
     * @avalon.dependency type="net.java.dev.openim.jabber.client.Presence:1.0" key="client.Presence"
     * @avalon.dependency type="net.java.dev.openim.jabber.client.Message:1.0" key="client.Message"
     *
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        super.service( serviceManager );
    }
   public void process( final IMSession session, final Object context ) throws Exception{
      // First stream
      session.setStreams(this);
      //processAttribute( session, context );
      super.process(session,context);

      // Then we start our own loop, continue until a new flash:stream is
      // detected
      final XmlPullParser xpp = session.getXmlPullParser();
      //final String currentEventName = xpp.getNamespace()+':'+xpp.getName();
      final String currentEventName = getEventName(session,xpp.getNamespace(),xpp.getName());
      
      for( int eventType = xpp.next() ; eventType != XmlPullParser.END_DOCUMENT; eventType = xpp.next() ){
         
         
         if( eventType == XmlPullParser.START_TAG ){
            processStartTag( session, context );
         }
         else if( eventType == XmlPullParser.TEXT ){
            processText( session, context );
         }
         else if( eventType == XmlPullParser.END_TAG ){
            
            if( currentEventName.equals( getEventName(session,xpp.getNamespace(),xpp.getName() )) ) {
               processEndTag( session, context );
               break;
            }
         }    
      } // for
      getLogger().debug( "END_DOCUMENT" );
      
      
      // We should not leave it if we was not
   }

   public void processStartTag( final IMSession session, final Object context  ) throws Exception {
      final XmlPullParser xpp = session.getXmlPullParser();
      //final String eventName = xpp.getNamespace()+':'+xpp.getName();
      final String eventName = getEventName(session,xpp.getNamespace(),xpp.getName()); 
      getLogger().debug( "FlashStream processing tag " + eventName);
      if ( "flash:streams".equals(eventName)) {
         getLogger().debug( "End of stream");
         return;
      } else {
         super.processStartTag(session,context);
      } // end of else
           
      
   }


   public void processAttribute( final IMSession session, final Object context ) throws Exception {
      final XmlPullParser xpp = session.getXmlPullParser();
      String from = xpp.getAttributeValue( "", "from" );
      if( from == null || from.length() == 0 ){
         getLogger().debug( "from attribut not specified in stream declaration" );
      }
      
      StringBuffer s = new StringBuffer("<flash:stream xmlns:flash='http://www.jabber.com/streams/flash' xmlns:stream='http://etherx.jabber.org/streams' ");
      s.append("id='").append(session.getId()).append("' ");
      s.append("xmlns='jabber:client' ");
      s.append("from='").append(m_serverParameters.getHostName()).append("'/>");
      session.writeOutputStream( s.toString() );
   }
   
   
}// FlashStreams
