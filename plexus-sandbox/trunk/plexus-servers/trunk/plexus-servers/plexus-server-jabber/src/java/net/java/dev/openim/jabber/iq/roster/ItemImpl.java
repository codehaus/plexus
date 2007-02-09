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
package net.java.dev.openim.jabber.iq.roster;


import org.xmlpull.v1.XmlPullParser;


import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.DefaultSessionProcessor;


/**
 * @avalon.component version="1.0" name="iq.roster.Item" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.roster.Item"
 *
 * @version 1.0
 * @author AlAg
 */
public class ItemImpl extends DefaultSessionProcessor implements Item {
    
    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.roster.Group:1.0" key="iq.roster.Group"
     */
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        super.service( serviceManager );
    }

    
    public void process( final IMSession session, final Object context ) throws Exception{
        XmlPullParser xpp = session.getXmlPullParser();
        IMRosterItem rosterItem = (IMRosterItem)context;
        rosterItem.setName( xpp.getAttributeValue( "", "name" ) );
        rosterItem.setJID( xpp.getAttributeValue( "", "jid" ) );
        rosterItem.setSubscription( xpp.getAttributeValue( "", "subscription" ) );
        rosterItem.setAsk( xpp.getAttributeValue( "", "ask" ) );
        super.process( session, context );
    }
    
    
    
}


