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
package org.codehaus.plexus.server.jabber.jabber.iq.roster;


import org.xmlpull.v1.XmlPullParser;


import org.codehaus.plexus.server.jabber.data.jabber.IMRosterItem;
import org.codehaus.plexus.server.jabber.session.IMSession;
import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.roster.Item" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.roster.Item"
 */
public class ItemImpl
    extends DefaultSessionProcessor
    implements Item
{

    /** @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.iq.roster.Group:1.0" key="iq.roster.Group" */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        super.service( serviceManager );
    }


    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {
        XmlPullParser xpp = session.getXmlPullParser();
        IMRosterItem rosterItem = (IMRosterItem) context;
        rosterItem.setName( xpp.getAttributeValue( "", "name" ) );
        rosterItem.setJID( xpp.getAttributeValue( "", "jid" ) );
        rosterItem.setSubscription( xpp.getAttributeValue( "", "subscription" ) );
        rosterItem.setAsk( xpp.getAttributeValue( "", "ask" ) );
        super.process( session, context );
    }


}


