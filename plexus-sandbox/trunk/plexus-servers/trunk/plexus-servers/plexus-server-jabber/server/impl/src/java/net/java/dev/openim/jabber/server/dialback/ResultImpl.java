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
 * @avalon.component version="1.0" name="server.dialback.Result" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.jabber.server.dialback.Result"
 *
 * @version 1.0
 * @author AlAg
 */
public class ResultImpl extends DefaultSessionProcessor implements Result {
    
    private String m_dialbackValue;
    private SessionsManager m_sessionsManager;
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
        //String to = xpp.getAttributeValue( "", "to" );
        String from = xpp.getAttributeValue( "", "from" );
        String type = xpp.getAttributeValue( "", "type" );
        
        if( from != null && from.length() > 0 ){
            serverSession.setRemoteHostname( from );
        }
        
        super.process( session, context );
        
        //String id = xpp.getAttributeValue( "", "id" );

        if( "valid".equals( type ) ){
            getLogger().debug( "Result valid from " + from  );
            serverSession.setDialbackValid( true );
            synchronized( session ){
                session.notifyAll();
            }
        }
        else if( m_dialbackValue != null ){
            getLogger().debug( "Verify " + from + " dialback " +  m_dialbackValue );
            if( serverSession.getTwinSession() == null ){
                session.getRouter().getS2SConnectorManager().verifyRemoteHost( from, m_dialbackValue, Long.toString( session.getId() ), serverSession );
            }
        }
        
    }
    
    
    //-------------------------------------------------------------------------
    public void processText( final IMSession session, final Object context ) throws Exception {
        m_dialbackValue = session.getXmlPullParser().getText().trim();
    }

}

