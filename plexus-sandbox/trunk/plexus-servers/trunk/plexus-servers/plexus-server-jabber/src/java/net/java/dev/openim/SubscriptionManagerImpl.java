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
package net.java.dev.openim;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.data.jabber.IMPresenceImpl;
import net.java.dev.openim.data.jabber.DeferrableIMPresenceImpl;
import net.java.dev.openim.data.storage.RosterListRepositoryHolder;
import net.java.dev.openim.tools.JIDParser;
import net.java.dev.openim.data.jabber.IMRosterItem;
import net.java.dev.openim.IMPresenceHolder;

import net.java.dev.openim.session.IMSession;


/**
 * @avalon.component version="1.0" name="SubscriptionManager" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.SubscriptionManager"
 *
 * @version 1.0
 * @author AlAg
 */
public class SubscriptionManagerImpl extends AbstractLogEnabled 
implements SubscriptionManager, Serviceable {
    
    
    private IMPresenceHolder m_presenceHolder;
    private RosterListRepositoryHolder m_rosterListReposityHolder;
    private ServerParameters    m_serverParameters;
    
    /**
     * @avalon.dependency type="net.java.dev.openim.IMPresenceHolder:1.0" key="IMPresenceHolder"
     * @avalon.dependency type="net.java.dev.openim.data.storage.RosterListRepositoryHolder:1.0" key="RosterListRepositoryHolder"
     * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        m_rosterListReposityHolder = (RosterListRepositoryHolder)serviceManager.lookup( "RosterListRepositoryHolder" );
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
        m_presenceHolder = (IMPresenceHolder)serviceManager.lookup( "IMPresenceHolder" );
    }

    
    //-------------------------------------------------------------------------
    public void process( final IMSession session, IMPresence presence ) throws Exception {

        presence = new DeferrableIMPresenceImpl( presence );
        
        // remove ressources
        String to = JIDParser.getJID( presence.getTo() );
        String from = JIDParser.getJID( presence.getFrom() );
        presence.setTo( to );
        presence.setFrom( from );
        
        boolean toLocalHost = false;
        if( m_serverParameters.getHostNameList().contains( JIDParser.getHostname( to ) ) ){
            toLocalHost = true;
        }
        boolean fromLocalHost = false;
        if( m_serverParameters.getHostNameList().contains( JIDParser.getHostname( from ) ) ){
            fromLocalHost = true;
        }
        
        
        
        String type = presence.getType();
        
        if( IMPresence.TYPE_SUBSCRIBE.equals( type ) ){
            if( fromLocalHost  ){
                preSubscribe( session, presence, toLocalHost );
            }
            else{
                postSubscribe( session, presence );
            }
        }
        
        else if( IMPresence.TYPE_SUBSCRIBED.equals( type ) ){
            if( fromLocalHost  ){
                preSubscribed( session, presence, toLocalHost );
            }
            else{
                postSubscribed( session, presence );
            }            
        }
        
        else if( IMPresence.TYPE_UNSUBSCRIBE.equals( type ) ){
            if( fromLocalHost ){
                preUnsubscribe( session, presence, toLocalHost );
            }
            else{
                postUnsubscribe( session, presence );
            }

        }
        
        else if( IMPresence.TYPE_UNSUBSCRIBED.equals( type ) ){
            if( fromLocalHost ){
                preUnsubscribed( session, presence, toLocalHost );
            }
            else{
                postUnsubscribed( session, presence );
            }            
        }
        
        else{
            session.getRouter().route( session, presence );
        }
            
            
        
    }
    // ------------------------------------------------------------------------
    private void preSubscribe( IMSession session, IMPresence presence, boolean toLocalHost ) throws Exception {
        String to = presence.getTo();
        String from = presence.getFrom();

        
        IMRosterItem roster = getRoster( from, to );
        String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
        if( roster != null ){
            roster.setAsk( IMRosterItem.ASK_SUBSCRIBE );
            rosterAck += roster.toString();
        }
        rosterAck += "</query></iq>";
        if ( roster!=null) {
           roster.setAsk( null );
        } // end of if ()
;
        emitToAllRegisteredSession( session, from, rosterAck );

        if( toLocalHost ){
            postSubscribe( session, presence );
        }
        else{
            session.getRouter().route( session, presence );
        }
        
    }
    
    // ------------------------------------------------------------------------
    private void postSubscribe( IMSession session, IMPresence presence ) throws Exception {
        String to = presence.getTo();
        String from = presence.getFrom();
        
        IMRosterItem roster = getRoster( to, from );
        if( roster == null ){
            roster = new IMRosterItem();
            roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            roster.setJID( from );
            roster.setName( from );
            roster.setGroup( "General" );
            addRoster( to, roster );
        }
        
        String subscription = roster.getSubscription();
        if( IMRosterItem.SUBSCRIPTION_TO.equals( subscription ) 
           || IMRosterItem.SUBSCRIPTION_NONE.equals( subscription ) || subscription == null){
               
            if( IMRosterItem.SUBSCRIPTION_FROM.equals( subscription ) ){
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_BOTH );
            }
            else{
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_TO );
            }
            
            if( IMRosterItem.SUBSCRIPTION_TO.equals( subscription ) ){
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_BOTH );
            }
            else{
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_FROM );
            }
            
            session.getRouter().route( session, presence );
        }

        
    }
    // ------------------------------------------------------------------------
    
    // ------------------------------------------------------------------------    
    private void preSubscribed( IMSession session, IMPresence presence, boolean toLocalHost ) throws Exception {
        String to = presence.getTo();
        String from = presence.getFrom();
        
        IMRosterItem roster = getRoster( from, to );
        String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
        if( roster != null ){
            rosterAck += roster.toString();
        }
        rosterAck += "</query></iq>";
        emitToAllRegisteredSession( session, from, rosterAck );




        if( toLocalHost ){
            postSubscribed( session, presence );
        }
        else{
            session.getRouter().route( session, presence );
        }


        //IMPresence currentPresence = m_presenceHolder.getPresence( from  );
        Collection col = m_presenceHolder.getPresence( from  );
        if( col != null && !col.isEmpty() ){
            Iterator iter = col.iterator();
            while( iter.hasNext() ){
                IMPresence currentPresence = (IMPresence)iter.next();
                currentPresence = (IMPresence)currentPresence.clone();
                currentPresence.setTo( to );
                session.getRouter().route( session, currentPresence );
            }
        }

        
        
    }
    
    // ------------------------------------------------------------------------
    private void postSubscribed( IMSession session, IMPresence presence ) throws Exception {
        
        String to = presence.getTo();
        String from = presence.getFrom();
        
        IMRosterItem roster = getRoster( to, from );
        if( roster == null ){
            roster = new IMRosterItem();
            roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            roster.setJID( from );
            roster.setName( from );
            roster.setGroup( "General" );
            addRoster( to, roster );
        }
        
        String subscription = roster.getSubscription();
        if( IMRosterItem.SUBSCRIPTION_FROM.equals( subscription ) 
           || IMRosterItem.SUBSCRIPTION_NONE.equals( subscription ) || subscription == null){
               
            session.getRouter().route( session, presence );

            //roster.setSubscription( IMRosterItem.SUBSCRIPTION_TO );
            
            if( IMRosterItem.SUBSCRIPTION_FROM.equals( subscription ) ){
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_BOTH );
                roster.setSubscription( IMRosterItem.SUBSCRIPTION_BOTH );
            }
            else{
                setRosterSubcription( to, from, IMRosterItem.SUBSCRIPTION_TO );
                roster.setSubscription( IMRosterItem.SUBSCRIPTION_TO );
            }
            
            
            
            String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
            rosterAck += roster.toString();
            rosterAck += "</query></iq>";
            emitToAllRegisteredSession( session, to, rosterAck );
            
        }
        
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private void preUnsubscribe( IMSession session, IMPresence presence, boolean toLocalHost ) throws Exception {
        
        String to = presence.getTo();
        String from = presence.getFrom();
        
        
        IMRosterItem roster = getRoster( from, to );
        if( roster != null ){
            roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
            rosterAck += roster.toString();
            rosterAck += "</query></iq>";
            emitToAllRegisteredSession( session, from, rosterAck );

            removeRoster( from, to );
        }        
        
        if( toLocalHost ){
            postUnsubscribe( session, presence );
        }
        else{
            session.getRouter().route( session, presence );
        }
    }
    
    // ------------------------------------------------------------------------
    private void postUnsubscribe( IMSession session, IMPresence presence ) throws Exception {
                session.getRouter().route( session, presence );
    }
    // ------------------------------------------------------------------------
    
    // ------------------------------------------------------------------------
    private void preUnsubscribed( IMSession session, IMPresence presence, boolean toLocalHost ) throws Exception {        
                
        String to = presence.getTo();
        String from = presence.getFrom();
                
        
                
        if( toLocalHost ){
            postUnsubscribed( session, presence );
        }
        else{
            session.getRouter().route( session, presence );
        }
        
        IMPresence currentPresence = new IMPresenceImpl();
        currentPresence.setFrom( from );
        currentPresence.setTo( to );
        currentPresence.setType( IMPresence.TYPE_UNAVAILABLE );
        
    }
    
    // ------------------------------------------------------------------------
    private void postUnsubscribed( IMSession session, IMPresence presence ) throws Exception {
        
        String to = presence.getTo();
        String from = presence.getFrom();
        
        IMRosterItem roster = getRoster( to, from );
        if( roster != null ){
            roster.setSubscription( IMRosterItem.SUBSCRIPTION_NONE );
            String rosterAck = "<iq type='set'><query xmlns='jabber:iq:roster'>";
            rosterAck += roster.toString();
            rosterAck += "</query></iq>";
            emitToAllRegisteredSession( session, to, rosterAck );

            removeRoster( to, from );
        }
        session.getRouter().route( session, presence );
    }
    // ------------------------------------------------------------------------
    
    // ------------------------------------------------------------------------
    private final void addRoster( String usernameJID, IMRosterItem roster ) {
        String username = JIDParser.getName( usernameJID );
        List rosterList = m_rosterListReposityHolder.getRosterList( username );
        if( rosterList == null ){
            rosterList = new ArrayList();
        }
        rosterList.add( roster );
        m_rosterListReposityHolder.setRosterList( username, rosterList );
    }
    // ------------------------------------------------------------------------
    private final void setRosterSubcription( String usernameJID, String itemJID, String subscription ){

        String username = JIDParser.getName( usernameJID );
        List rosterList = m_rosterListReposityHolder.getRosterList( username );
        if( rosterList == null ){
            rosterList = new ArrayList();
        }
        IMRosterItem roster = getItemFromRosterList( itemJID, rosterList );
        if( roster != null ){
            roster.setSubscription( subscription );
        }
        m_rosterListReposityHolder.setRosterList( username, rosterList );
    }
    // ------------------------------------------------------------------------
    private final void removeRoster( String usernameJID, String itemJID ){
        String username = JIDParser.getName( usernameJID );
        List rosterList = m_rosterListReposityHolder.getRosterList( username );
        if( rosterList == null ){
            rosterList = new ArrayList();
        }
        removeFromRosterList( rosterList, itemJID );
        m_rosterListReposityHolder.setRosterList( username, rosterList );
    }
    // ------------------------------------------------------------------------
    private final IMRosterItem getRoster( String usernameJID, String itemJID ){
        String username = JIDParser.getName( usernameJID );
        List rosterList = m_rosterListReposityHolder.getRosterList( username );
        if( rosterList == null ){
            rosterList = new ArrayList();
        }
        return getItemFromRosterList( itemJID, rosterList );
    }
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
    private final void emitToAllRegisteredSession( IMSession session, String usernameJID, String str ) throws Exception {
        // emit roster ack to client / should be all active ressource....
        String username = JIDParser.getName( usernameJID );
        List sessionList = session.getRouter().getAllRegisteredSession( username );
        for( int i=0, l=sessionList.size(); i<l; i++ ){
            IMSession s = (IMSession)sessionList.get( i );
            s.writeOutputStream( str );
        }
    }

}


