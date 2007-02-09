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


import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.data.jabber.MessagePacket;
import net.java.dev.openim.session.IMSession;

/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="server.Body" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.server.Body"
 */
public class BodyImpl
    extends DefaultSessionProcessor
    implements Body
{

    public void processText( final IMSession session,
                             final Object context )
        throws Exception
    {
        ( (MessagePacket) context ).setBody( session.getXmlPullParser().getText().trim() );
    }

}


