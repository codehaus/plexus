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
package org.codehaus.plexus.server.jabber.jabber.server;


import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.data.jabber.IQPacket;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="server.Iq" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.server.Iq"
 */
public class IqImpl
    extends DefaultSessionProcessor
    implements Iq
{

    /** @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.iq.vcardtemp.VCard:1.0" key="iq.vcardtemp.VCard" */
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

        for ( int i = 0, l = xpp.getAttributeCount(); i < l; i++ )
        {
            getLogger().debug( "Attribut ns: " + xpp.getAttributeNamespace( i ) + " name: " +
                xpp.getAttributeName( i ) + " value: " + xpp.getAttributeValue( i ) );
        }

        IQPacket iq = new IQPacket();
        iq.setId( xpp.getAttributeValue( "", "id" ) );
        iq.setType( xpp.getAttributeValue( "", "type" ) );
        iq.setTo( xpp.getAttributeValue( "", "to" ) );
        iq.setFrom( xpp.getAttributeValue( "", "from" ) );
        getLogger().debug( "Got IQ " + iq );
        super.process( session, iq );


    }

}


