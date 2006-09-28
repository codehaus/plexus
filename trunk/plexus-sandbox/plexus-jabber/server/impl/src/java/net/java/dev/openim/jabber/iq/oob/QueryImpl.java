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
package net.java.dev.openim.jabber.iq.oob;





import net.java.dev.openim.DefaultSessionProcessor;

import net.java.dev.openim.data.jabber.IQPacket;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

import java.io.StringWriter;





/**
 * @avalon.component version="1.0" name="iq.oob.Query" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.oob.Query"
 *
 * @version 1.0
 * @author AlAg
 */
public class QueryImpl extends DefaultSessionProcessor implements Query {

    
    //-------------------------------------------------------------------------
    public void process( final IMSession session, final Object context ) throws Exception{

        StringWriter sw = new StringWriter();
        session.roundTripNode(sw);
        String data = sw.toString();

        IQPacket iq = ((IQPacket)context);
        iq.setFrom( ((IMClientSession)session).getUser().getJIDAndRessource() );
        iq.addSerializedChild(data);
        session.getRouter().route( session, iq );
    }
    
    
}

