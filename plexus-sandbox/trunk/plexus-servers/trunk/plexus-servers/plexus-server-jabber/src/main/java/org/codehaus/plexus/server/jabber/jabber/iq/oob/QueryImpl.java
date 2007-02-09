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
package org.codehaus.plexus.server.jabber.jabber.iq.oob;


import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;

import org.codehaus.plexus.server.jabber.data.jabber.IQPacket;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;

import java.io.StringWriter;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.oob.Query" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.oob.Query"
 */
public class QueryImpl
    extends DefaultSessionProcessor
    implements Query
{


    //-------------------------------------------------------------------------
    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        StringWriter sw = new StringWriter();
        session.roundTripNode( sw );
        String data = sw.toString();

        IQPacket iq = ( (IQPacket) context );
        iq.setFrom( ( (IMClientSession) session ).getUser().getJIDAndRessource() );
        iq.addSerializedChild( data );
        session.getRouter().route( session, iq );
    }


}

