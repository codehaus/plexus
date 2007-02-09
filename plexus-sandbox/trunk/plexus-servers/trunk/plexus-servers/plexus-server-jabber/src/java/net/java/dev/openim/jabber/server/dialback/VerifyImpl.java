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
package net.java.dev.openim.jabber.server.dialback;

import org.apache.avalon.framework.service.ServiceManager;

import org.xmlpull.v1.XmlPullParser;


import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.session.IMServerSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.session.SessionsManager;





/**
 * @avalon.component version="1.0" name="server.dialback.Verify" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.jabber.server.dialback.Verify"
 *
 * @version 1.0
 * @author AlAg
 */
public class VerifyImpl extends DefaultSessionProcessor implements Verify {
    private SessionsManager m_sessionsManager;
    private String m_dialbackValue;
    /**
     * @avalon.dependency type="net.java.dev.openim.session.SessionsManager:1.0"  key="SessionsManager"
     */
    public void service( ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_sessionsManager = (SessionsManager)serviceManager.lookup( "SessionsManager" );
        super.service( serviceManager );
    }

    //-------------------------------------------------------------------------
    public void process( final IMSession session, final Object context ) throws Exception {

        IMServerSession serverSession = (IMServerSession)session;
        
        XmlPullParser xpp = session.getXmlPullParser();
        String type = xpp.getAttributeValue( "", "type" );
        String from = xpp.getAttributeValue( "", "from" );
        String to = xpp.getAttributeValue( "", "to" );
        String id = xpp.getAttributeValue( "", "id" );
        
        super.process( session, context );
        
        
        getLogger().debug( "Got m_dialbackValue " + m_dialbackValue  );
        
        if( "valid".equals( type ) ){
            String s = "<db:result to='"+from+"' from='"+to+"' type='valid' id='"+id+"'/>";    
            getLogger().debug( "Verfication valid " + s );
            serverSession.getTwinSession().writeOutputStream( s );
            //session.getRouter().validateRemoteHost( session, from, m_dialbackValue );
        }
        else if( m_dialbackValue != null ){
            IMServerSession local2remoteSession = session.getRouter().getS2SConnectorManager().getCurrentRemoteSession( from );
            if( local2remoteSession != null && m_dialbackValue.equals( local2remoteSession.getDialbackValue() ) ){
                getLogger().debug( "Verification valid from " + from + " to " + to 
                        +" id " + id
                        +" dialbackId " + m_dialbackValue );
                //session.getRouter().validateRemoteHost( session, from, m_dialbackValue );
                String s = "<db:verify from='"+to+"' "
                    +"to='"+from+"' "
                    +"id='"+id+"' "
                    +"type='valid'/>";
                session.writeOutputStream( s );
            }
            else{
                // should send unvalid?
                if( local2remoteSession == null ){
                    getLogger().warn( "Abnormal: local2remoteSession null" );
                }
                else{
                    getLogger().warn( "Unvalid Dialback " + m_dialbackValue + " expected " + local2remoteSession.getDialbackValue() );
                }
                
            }
        }

        
       
    }
    
   //-------------------------------------------------------------------------
    public void processText( final IMSession session, final Object context ) throws Exception {
        m_dialbackValue = session.getXmlPullParser().getText().trim();
    }

}

