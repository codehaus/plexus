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

import java.util.Collection;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;

import org.codehaus.plexus.server.jabber.IMPresenceHolder;
import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.data.jabber.IMPresence;
import org.codehaus.plexus.server.jabber.data.jabber.IMPresenceImpl;
import org.codehaus.plexus.server.jabber.session.IMSession;
import org.codehaus.plexus.server.jabber.SubscriptionManager;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="server.Presence" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.server.Presence"
 */
public class PresenceImpl
    extends DefaultSessionProcessor
    implements Presence, ThreadSafe
{


    private IMPresenceHolder m_presenceHolder;
    private SubscriptionManager m_subscriptionManager;

    /**
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Show:1.0" key="server.Show"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Priority:1.0" key="server.Priority"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Status:1.0" key="server.Status"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.jabber.server.Error:1.0" key="server.Error"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.IMPresenceHolder:1.0" key="IMPresenceHolder"
     * @avalon.dependency type="org.codehaus.plexus.server.jabber.SubscriptionManager:1.0" key="SubscriptionManager"
     */
    public void service( org.apache.avalon.framework.service.ServiceManager serviceManager )
        throws org.apache.avalon.framework.service.ServiceException
    {
        super.service( serviceManager );
        m_presenceHolder = (IMPresenceHolder) serviceManager.lookup( "IMPresenceHolder" );
        m_subscriptionManager = (SubscriptionManager) serviceManager.lookup( "SubscriptionManager" );
    }

    //-------------------------------------------------------------------------
    public void process( final IMSession session,
                         final Object context )
        throws Exception
    {

        XmlPullParser xpp = session.getXmlPullParser();
        String type = xpp.getAttributeValue( "", "type" );
        String to = xpp.getAttributeValue( "", "to" );
        String from = xpp.getAttributeValue( "", "from" );


        IMPresence presence = new IMPresenceImpl();
        presence.setType( type );
        presence.setTo( to );
        presence.setFrom( from );


        super.process( session, presence );

        getLogger().debug( "Got presence (to " + to + "): " + presence );

        if ( to == null || to.length() == 0 )
        {
            // emit presence associated to roster friends?
            getLogger().debug( "To is not specified, what should we do?" );
        }
        else
        {
            String presenceType = presence.getType();
            if ( IMPresence.TYPE_PROBE.equals( presenceType ) )
            {
                getLogger().info( "Probed from " + from + " to " + to );
                // check availability
                Collection col = m_presenceHolder.getPresence( to );
                if ( col != null && !col.isEmpty() )
                {
                    Iterator iter = col.iterator();
                    while ( iter.hasNext() )
                    {
                        IMPresence localPresence = (IMPresence) iter.next();
                        localPresence = (IMPresence) localPresence.clone();
                        localPresence.setTo( from );
                        session.getRouter().route( session, localPresence );
                    }
                }
                // unavailable
                else
                {
                    IMPresence localPresence = new IMPresenceImpl();
                    localPresence.setType( IMPresence.TYPE_UNAVAILABLE );
                    localPresence.setFrom( to );
                    localPresence.setTo( from );
                    session.getRouter().route( session, localPresence );
                }

            }


            else
            {
                IMPresence localPresence = (IMPresence) presence.clone();
                m_subscriptionManager.process( session, localPresence );
            }


        } // if to null


    }


}


