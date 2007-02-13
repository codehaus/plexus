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
package org.codehaus.plexus.server.jabber.jabber;

import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.ServerParameters;
import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.session.IMServerSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @plexus.component
 */
public class StreamsImpl
    extends DefaultSessionProcessor
    implements Streams
{
    /** @plexus.requirement */
    protected ServerParameters m_serverParameters;

    protected String m_namespace;

    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {
        final XmlPullParser xpp = session.getXmlPullParser();
        m_namespace = xpp.getNamespace( null );
        processAttribute( session, context );
        if ( session instanceof IMServerSession )
        {
            getLogger().info(
                "Start stream " + ( (IMServerSession) session ).getRemoteHostname() + " id " + session.getId() );
        }
        super.process( session, context );
        if ( session instanceof IMServerSession )
        {
            getLogger().info(
                "Stop stream " + ( (IMServerSession) session ).getRemoteHostname() + " id " + session.getId() );
        }
    }

    //-------------------------------------------------------------------------
    public void processAttribute( final IMSession session,
                                  final Object context )
        throws Exception
    {

        final XmlPullParser xpp = session.getXmlPullParser();
        String to = xpp.getAttributeValue( "", "to" );
        String from = xpp.getAttributeValue( "", "from" );

        if ( from == null || from.length() == 0 )
        {
            getLogger().debug( "from attribut not specified in stream declaration" );
        }
        else
        {
            if ( session instanceof IMServerSession )
            {
                ( (IMServerSession) session ).setRemoteHostname( from );
            }
        }


        if ( session.getConnectionType() == IMSession.S2S_L2R_CONNECTION )
        {
            getLogger().debug( "Local to Remote connection " + to );
        }
        else
        {
            String s =
                "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' " + "id='" + session.getId() + "' ";
            if ( session.getConnectionType() == IMSession.C2S_CONNECTION )
            {
                s += "xmlns='jabber:client' ";
            }
            else if ( session.getConnectionType() == IMSession.S2S_R2L_CONNECTION )
            {
                s += "xmlns='jabber:server' xmlns:db='jabber:server:dialback' ";
            }
            s += "from='" + m_serverParameters.getHostName() + "'>";
            session.writeOutputStream( s );

        }
    }

    public String getNamespace()
    {
        return m_namespace;
    }

}


