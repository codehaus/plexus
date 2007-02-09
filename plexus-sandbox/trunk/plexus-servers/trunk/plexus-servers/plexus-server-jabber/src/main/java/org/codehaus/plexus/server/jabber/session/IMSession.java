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
package org.codehaus.plexus.server.jabber.session;

import java.net.Socket;


import java.io.IOException;
import java.io.Writer;


import org.codehaus.plexus.server.jabber.IMRouter;
import org.codehaus.plexus.server.jabber.data.Transitable;
import org.codehaus.plexus.server.jabber.jabber.Streams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;


/**
 * @author AlAg
 * @author PV
 * @version 1.0
 */
public interface IMSession
{
    public static final int UNKNOWN_CONNECTION = 0;
    public static final int C2S_CONNECTION = 1;
    public static final int S2S_L2R_CONNECTION = 2;
    public static final int S2S_R2L_CONNECTION = 3;

    public void setup( Socket socket )
        throws Exception;

    public boolean isClosed();

    public void close();


    public long getId();

    public XmlPullParser getXmlPullParser();

    public XmlPullParserFactory getXMLParserFactory();

    public int getConnectionType();

    public void writeOutputStream( String s )
        throws IOException;

    public void writeOutputStream( Transitable s )
        throws IOException;

    public String getEncoding();

    public void setRouter( IMRouter router );

    public IMRouter getRouter();

    public void setStreams( Streams streams );

    public Streams getStreams();

    public void roundTripNode( Writer out )
        throws XmlPullParserException, java.io.IOException;
}



