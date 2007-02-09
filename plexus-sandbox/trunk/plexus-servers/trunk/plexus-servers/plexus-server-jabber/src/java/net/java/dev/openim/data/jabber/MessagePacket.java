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
package net.java.dev.openim.data.jabber;

import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.Deferrable;
import ctu.jabber.data.Packet;
import org.xmlpull.v1.XmlPullParser;


/**
 * @author PV
 */
public class MessagePacket extends Packet implements Transitable, Deferrable {
    public static final String TYPE_CHAT = "chat";

    // Constructors
    public MessagePacket() { super("message"); }
    public MessagePacket(XmlPullParser xpp) { super(xpp); }

    public final void setSubject(String subject) {
        this.setChildText("subject", subject);
    }
    public final String getSubject() {
        return this.getChildText("subject");
    }

    public final void setBody(String body) {
        this.setChildText("body", body);
    }
    public final String getBody() {
        return this.getChildText("body");
    }

    public final void setThread(String thread) {
        this.setChildText("thread", thread);
    }
    public final String getThread() {
        return this.getChildText("thread");
    }

}

