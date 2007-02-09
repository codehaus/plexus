// serialization is replaced with session roundtriping
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
package net.java.dev.openim.jabber.iq.vcardtemp;




import net.java.dev.openim.DefaultSessionProcessor;


import net.java.dev.openim.data.jabber.IQPacket;
import net.java.dev.openim.data.storage.PrivateRepositoryHolder;

import org.xmlpull.v1.XmlPullParser;

import net.java.dev.openim.ServerParameters;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.tools.JIDParser;

import java.io.StringWriter;

/**
 * @avalon.component version="1.0" name="iq.vcardtemp.VCard" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.vcardtemp.VCard"
 *
 * @version 1.0
 * @author AlAg
 */
public class VCardImpl extends DefaultSessionProcessor implements VCard {
    
    private ServerParameters m_serverParameters;
    private PrivateRepositoryHolder m_privateRepository;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0" key="ServerParameters"
     * @avalon.dependency type="net.java.dev.openim.data.storage.PrivateRepositoryHolder:1.0" key="PrivateRepositoryHolder"
     */    
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_privateRepository = (PrivateRepositoryHolder)serviceManager.lookup( "PrivateRepositoryHolder" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
    }


    //-------------------------------------------------------------------------
    
    public void process( final IMSession session, final Object context ) throws Exception{
        
        String type = ((IQPacket)context).getType();
        
        // GET
        if( IQPacket.TYPE_GET.equals( type ) ){
            get( session, context );
        }
        else if( IQPacket.TYPE_SET.equals( type ) ){
            set( (IMClientSession)session, context );
        }        
        else if( IQPacket.TYPE_RESULT.equals( type ) ){
            result( session, context );
        }        
    }
    
    
    //-------------------------------------------------------------------------
    private void get( final IMSession session, Object context ) throws Exception {
        
        
        final XmlPullParser xpp = session.getXmlPullParser();
        final String vcardname = xpp.getNamespace()+':'+xpp.getName();
        
        String iqId = ((IQPacket)context).getId();
        String to = ((IQPacket)context).getTo();
        String from = ((IQPacket)context).getFrom();
        
        if( to == null || to.length() == 0 ){
            to = ((IMClientSession)session).getUser().getJID();
        }
        if( from == null || from.length() == 0 ){
            from = ((IMClientSession)session).getUser().getJID();
        }
        
        
        
        IQPacket iq = null;
        
        if( m_serverParameters.getHostNameList().contains( JIDParser.getHostname( to ) ) ){
            String data = m_privateRepository.getData( to, vcardname.toLowerCase() );
            if( data == null ){
                data = "<vCard xmlns='vcard-temp'/>";
            }

            getLogger().debug( "Get "+to+"/"+vcardname+" vcard: " + data );

            // local request
            iq = new IQPacket();
            iq.setFrom( to );
            iq.setTo( from );
            iq.setId( iqId );
            iq.setType( IQPacket.TYPE_RESULT );
            iq.addSerializedChild(data);
        }
        else{
            iq = new IQPacket();
            iq.setFrom( from );
            iq.setTo( to );
            iq.setId( iqId );
            iq.setType( IQPacket.TYPE_GET );
            iq.addSerializedChild("<vCard xmlns='vcard-temp'/>");
        }
        
        session.getRouter().route( session, iq );

        skip( xpp );
    }
    
    //-------------------------------------------------------------------------
    private void set( final IMClientSession session, final Object context ) throws Exception {

        final XmlPullParser xpp = session.getXmlPullParser();
        
        String vcardname = xpp.getNamespace()+':'+xpp.getName();

        StringWriter sw = new StringWriter();
        session.roundTripNode(sw);
        String data = sw.toString();
        
        getLogger().debug( "Set "+session.getUser().getJID()+"/"+vcardname+" vcard: " + data );
        if( data != null ){
            m_privateRepository.setData( session.getUser().getJID(), vcardname.toLowerCase(), data );
        }
        
        
        String iqId = ((IQPacket)context).getId();
        String to = ((IQPacket)context).getTo();
        String from = ((IQPacket)context).getFrom();
        
        String s = "<iq type='result'";
        s += " from='"+to+"'";
        s += " to='"+from+"'";
        s += " id='"+iqId+"'/>";
        
        session.writeOutputStream( s );
        
        
    }
    //-------------------------------------------------------------------------
    private void result( final IMSession session, final Object context ) throws Exception {

        
        final XmlPullParser xpp = session.getXmlPullParser();
        String to = ((IQPacket)context).getTo();

        if( m_serverParameters.getHostNameList().contains( JIDParser.getHostname( to ) ) ){
            // local request
            String iqId = ((IQPacket)context).getId();
            String from = ((IQPacket)context).getFrom();
            StringWriter sw = new StringWriter();
            session.roundTripNode(sw);
            String data = sw.toString();

            IQPacket iq = new IQPacket();
            iq.setFrom( from );
            iq.setTo( to );
            iq.setId( iqId );
            iq.setType(IQPacket.TYPE_RESULT);
            iq.addSerializedChild(data);
            session.getRouter().route(session, iq);
        }
        
        else{
            getLogger().warn( "Abnormal result for remote delivery?" );
            skip( xpp );
        }
    }
}

