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
package net.java.dev.openim.jabber.client;


import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.data.jabber.MessagePacket;
import net.java.dev.openim.session.IMSession;

/**
 * @avalon.component version="1.0" name="client.Thread" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.client.Thread"
 *
 * @version 1.0
 * @author AlAg
 */
public class ThreadImpl extends DefaultSessionProcessor implements Thread {
    
    
    public void processText( final IMSession session, final Object context ) throws Exception {
        ((MessagePacket)context).setThread( session.getXmlPullParser().getText().trim() );
    }

}


