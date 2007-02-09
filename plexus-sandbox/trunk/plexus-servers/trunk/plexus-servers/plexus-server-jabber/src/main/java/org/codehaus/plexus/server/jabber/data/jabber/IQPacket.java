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
package org.codehaus.plexus.server.jabber.data.jabber;

import org.codehaus.plexus.server.jabber.data.Transitable;
import org.codehaus.plexus.server.jabber.data.Deferrable;
import ctu.jabber.data.Packet;
import org.xmlpull.v1.XmlPullParser;

/** @author PV */
public class IQPacket
    extends Packet
    implements Transitable, Deferrable
{
    public static final String TYPE_GET = "get";
    public static final String TYPE_SET = "set";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_ERROR = "error";

    // Constructors
    public IQPacket()
    {
        super( "iq" );
    }

    public IQPacket( XmlPullParser xpp )
    {
        super( xpp );
    }

}

