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
package org.codehaus.plexus.server.jabber.jabber.iq.jprivate;


import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;

import org.codehaus.plexus.server.jabber.data.jabber.IQPacket;
import org.codehaus.plexus.server.jabber.data.storage.PrivateRepositoryHolder;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringWriter;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.auth.Query" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.jprivate.Query"
 */
public class QueryImpl
    extends DefaultSessionProcessor
    implements Query
{

    private PrivateRepositoryHolder m_privateRepository;

    /** @avalon.dependency type="org.codehaus.plexus.server.jabber.data.storage.PrivateRepositoryHolder:1.0" key="PrivateRepositoryHolder" */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        m_privateRepository = (PrivateRepositoryHolder) serviceManager.lookup( "PrivateRepositoryHolder" );
    }

    //-------------------------------------------------------------------------

    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        IMClientSession clientSession = (IMClientSession) session;
        String type = ( (IQPacket) context ).getType();

        // GET
        if ( IQPacket.TYPE_GET.equals( type ) )
        {
            get( clientSession, context );
        }
        else if ( IQPacket.TYPE_SET.equals( type ) )
        {
            set( clientSession, context );
        }
    }


    //-------------------------------------------------------------------------
    private void get( final IMClientSession session,
                      Object context )
        throws Exception
    {


        final XmlPullParser xpp = session.getXmlPullParser();
        //final String privateName = xpp.getNamespace()+':'+xpp.getName();


        String iqId = ( (IQPacket) context ).getId();

        int eventType = xpp.next();
        String privateKey = xpp.getNamespace() + ':' + xpp.getName();
        String data = m_privateRepository.getData( session.getUser().getName(), privateKey );
        if ( data == null )
        {
            data = "<" + xpp.getName() + " xmlns='" + xpp.getNamespace() + "'/>";
        }

        getLogger().debug( "Got data (" + privateKey + "): " + data );
        String s = "<iq type='result'";
        s += " from='" + session.getUser().getJIDAndRessource() + "'";
        s += " to='" + session.getUser().getJIDAndRessource() + "'";
        s += " id='" + iqId + "'>";
        s += "<query xmlns='jabber:iq:private'>";
        s += data;
        s += "</query>";
        s += "</iq>";

        session.writeOutputStream( s );

        skip( xpp );
        /*
        while( !( eventType == XmlPullParser.END_TAG 
            && privateName.equals( xpp.getNamespace()+':'+xpp.getName() ) ) ){
            eventType = xpp.next();
        }
         */
    }

    //-------------------------------------------------------------------------
    private void set( final IMClientSession session,
                      final Object context )
        throws Exception
    {

        final XmlPullParser xpp = session.getXmlPullParser();

        //int eventType = xpp.next();
        String privateKey = xpp.getNamespace() + ':' + xpp.getName();

        StringWriter sw = new StringWriter();
        session.roundTripNode( sw );
        String data = sw.toString();

        getLogger().debug( "Got private key " + privateKey + " => data: " + data );
        if ( data != null && data.length() > 0 )
        {
            m_privateRepository.setData( session.getUser().getName(), privateKey, data );
        }


        String iqId = ( (IQPacket) context ).getId();

        String s = "<iq type='result'";
        s += " from='" + session.getUser().getJIDAndRessource() + "'";
        s += " to='" + session.getUser().getJIDAndRessource() + "'";
        s += " id='" + iqId + "'/>";


    }

}

