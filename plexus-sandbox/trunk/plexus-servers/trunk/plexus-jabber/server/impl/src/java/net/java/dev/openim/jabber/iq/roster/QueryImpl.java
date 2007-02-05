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
package net.java.dev.openim.jabber.iq.roster;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.data.jabber.IQPacket;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.data.jabber.IMPresenceImpl;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;
import net.java.dev.openim.IMPresenceHolder;
import net.java.dev.openim.SubscriptionManager;





/**
 * @avalon.component version="1.0" name="iq.roster.Query" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.roster.Query"
 *
 * @version 1.0
 * @author AlAg
 */
public class QueryImpl extends DefaultSessionProcessor implements Query {
    
    private IMPresenceHolder m_presenceHolder;
    private SubscriptionManager m_subscriptionManager;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.jabber.iq.roster.Item:1.0" key="iq.roster.Item"
     * @avalon.dependency type="net.java.dev.openim.IMPresenceHolder:1.0" key="IMPresenceHolder"
     * @avalon.dependency type="net.java.dev.openim.SubscriptionManager:1.0" key="SubscriptionManager"
     */
    public void service(org.apache.avalon.framework.service.ServiceManager serviceManager) throws org.apache.avalon.framework.service.ServiceException {
        m_presenceHolder = (IMPresenceHolder)serviceManager.lookup( "IMPresenceHolder" );
        m_subscriptionManager = (SubscriptionManager)serviceManager.lookup( "SubscriptionManager" );
        super.service( serviceManager );
        
    }

    //-------------------------------------------------------------------------
    
    public void process( final IMSession session, final Object context ) throws Exception{
        
        String iqId = ((IQPacket)context).getId();
        String type = ((IQPacket)context).getType();
        
        getLogger().debug( "Roster query type = " + type + " iqId " + iqId );
        
    
        if( IQPacket.TYPE_GET.equals( type ) ){
            get( iqId, (IMClientSession)session );
        } 
        else if( IQPacket.TYPE_SET.equals( type ) ){
            set( iqId, (IMClientSession)session );
        }
       
        

    }


    // ------------------------------------------------------------------------
    private void set( String iqId, IMClientSession session ) throws Exception {
        
        IMRosterItem roster = new IMRosterItem();
        //session.setRosterItem( roster );
        super.process( session, roster );


        // shall we remove?
        if( IMRosterItem.SUBSCRIPTION_REMOVE.equals( roster.getSubscription() ) ){
            //removeFromRosterList( rosterList, roster.getJID() );
            String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
            rosterAck += roster.toString();
            rosterAck += "</query></iq>";

            rosterAck += "<iq type='result' id='"+iqId+"'/>";
            
            emitToAllRegisteredSession( session, rosterAck );
            
            // emit unsubscrib presence to removed buddy
            IMPresence presence = new IMPresenceImpl();
            presence.setTo( roster.getJID() );
            presence.setFrom( session.getUser().getJID() );
            presence.setType( IMPresence.TYPE_UNSUBSCRIBE );
            m_subscriptionManager.process( session, presence );
            // emit unsubscrib presence to removed buddy
            presence = new IMPresenceImpl();
            presence.setTo( roster.getJID() );
            presence.setFrom( session.getUser().getJID() );
            presence.setType( IMPresence.TYPE_UNSUBSCRIBED );
            m_subscriptionManager.process( session, presence );
        }
        
        
        // we set
        else {
            getLogger().debug( "Setting roster item " + roster );
            List rosterList = session.getUser().getRosterItemList();
            getLogger().debug( "RosterList for user " + session.getUser().getName() + " => "+ rosterList );
            if( rosterList == null ){
                rosterList = new ArrayList();
            }

            IMRosterItem localroster = getItemFromRosterList( roster.getJID(), rosterList );
            if( localroster == null ){
                roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            }
            else{
                localroster.setName( roster.getName() );
                localroster.setGroup( roster.getGroup() );
                roster = localroster;
            }
            // build roster ack string
            if( roster.getName() == null  || roster.getName().length() == 0  ){
                roster.setName( roster.getJID() );
            }
            if( roster.getGroup() == null || roster.getGroup().length() == 0 ){
                roster.setGroup( "General" );
            }
            
            getLogger().debug( "Got roster: " + roster );
            
            //roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
            rosterAck += roster.toString();
            rosterAck += "</query></iq>";

            rosterAck += "<iq type='result' id='"+iqId+"'/>";

            String subscription = roster.getSubscription();
            if( IMRosterItem.SUBSCRIPTION_FROM.equals( subscription ) 
                || IMRosterItem.SUBSCRIPTION_NONE.equals( subscription ) || subscription == null){
                emitToAllRegisteredSession( session, rosterAck );
            }

            // remove/replace prev occurence of the buddy
            removeFromRosterList( rosterList, roster.getJID() );
            rosterList.add( roster );

            getLogger().debug( "Push rosterList for user " + session.getUser().getName() + " => "+ rosterList );
            
            session.getUser().setRosterItemList( rosterList );
            
        } // else add buddy
        
        
    } // set
    
    // ------------------------------------------------------------------------
    private void get( String iqId, IMClientSession session ) throws Exception {
            
        String s = "<iq type='"+IQPacket.TYPE_RESULT+"' id='"+iqId+"' from='"+session.getUser().getJIDAndRessource()+"'>"
            +"<query xmlns='jabber:iq:roster'>";

        List rosterList = session.getUser().getRosterItemList();
        if( rosterList != null ){
            for( int i=0, l=rosterList.size(); i<l; i++ ){
                IMRosterItem rosterItem = (IMRosterItem)rosterList.get( i );
                s += rosterItem.toString();
            }
        } // if

        s += "</query></iq>";
        session.writeOutputStream( s );

        // get all presence associated to roster item
        if( rosterList != null ){

            for( int i=0, l=rosterList.size(); i<l; i++ ){
                IMRosterItem rosterItem = (IMRosterItem)rosterList.get( i );
                String subscription = rosterItem.getSubscription();

                if( IMRosterItem.SUBSCRIPTION_BOTH.equals( subscription )
                        || IMRosterItem.SUBSCRIPTION_TO.equals( subscription ) )
                {
                    Collection col = m_presenceHolder.getPresence( rosterItem.getJID() );
                    if( col != null && !col.isEmpty() ){
                        Iterator iter = col.iterator();
                        while( iter.hasNext() ){
                            IMPresence currentPresence = (IMPresence)iter.next();
                            session.writeOutputStream( currentPresence );
                        }
                    }
                    // probe the presence
                    else{
                        IMPresence presence = new IMPresenceImpl();
                        presence.setFrom( session.getUser().getJID() );
                        presence.setTo( rosterItem.getJID() );
                        presence.setType( IMPresence.TYPE_PROBE );
                        session.getRouter().route( session, presence );
                    }
                } // subscribtion
            }
        } // if

    } // get
    
    // ------------------------------------------------------------------------
    private final void removeFromRosterList( List rosterList, String jid ){
        getLogger().debug( "Removing roster item " + jid );
        for( int i=0, l=rosterList.size(); i<l; i++ ){
            IMRosterItem rosterItem = (IMRosterItem)rosterList.get( i );
            if( rosterItem.getJID().equals( jid ) ){
                rosterList.remove( i );
                break;
            }
        }
    }
    // ------------------------------------------------------------------------
    private final IMRosterItem getItemFromRosterList( String jid, List rosterList ){
        IMRosterItem rosterItem = null;
        for( int i=0, l=rosterList.size(); i<l; i++ ){
            rosterItem = (IMRosterItem)rosterList.get( i );
            if( rosterItem.getJID().equals( jid ) ){
                break;
            }
            rosterItem = null;
        }
        return rosterItem;
    }
    
    
    // ------------------------------------------------------------------------
    private final void emitToAllRegisteredSession( IMClientSession session, String str ) throws Exception {
        // emit roster ack to client / should be all active ressource....
        List sessionList = session.getRouter().getAllRegisteredSession( session.getUser().getName() );
        for( int i=0, l=sessionList.size(); i<l; i++ ){
            IMSession s = (IMSession)sessionList.get( i );
            s.writeOutputStream( str );
        }
    }
}


