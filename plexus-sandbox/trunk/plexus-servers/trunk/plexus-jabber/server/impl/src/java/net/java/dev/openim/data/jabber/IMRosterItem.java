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
package net.java.dev.openim.data.jabber;

import net.java.dev.openim.tools.XMLToString;
import net.java.dev.openim.tools.JIDParser;

/**
 * @author AlAg
 */
public class IMRosterItem implements java.io.Serializable
{
    public static final String SUBSCRIPTION_REMOVE      = "remove";
    public static final String SUBSCRIPTION_BOTH        = "both";
    public static final String SUBSCRIPTION_NONE        = "none";
    public static final String SUBSCRIPTION_TO          = "to";
    public static final String SUBSCRIPTION_FROM        = "from";

    public static final String ASK_SUBSCRIBE            = "subscribe";
    public static final String ASK_UNSUBSCRIBE          = "unsubscribe";
    
    
    
    private String m_name;
    private String m_jid;
    private String m_group;
    private String m_subscription;
    private String m_ask;

    
    public final void setName( String name ){
        m_name = name;
    }
    public final String getName(){
        return m_name;
    }
    
    public final void setJID( String jid ){
        if( jid != null ){
            m_jid = JIDParser.getJID( jid );
        }
    }
    public final String getJID(){
        return m_jid;
    }
    
    public final void setGroup( String group ){
        m_group = group;
    }
    public final String getGroup(){
        return m_group;
    }
    
    public final void setSubscription( String subscription ){
        m_subscription = subscription;
    }
    public final String getSubscription(){
        return m_subscription;
    }
    
    public final void setAsk( String ask ){
        m_ask = ask;
    }
    public final String getAsk(){
        return m_ask;
    }
    
    public boolean equals( Object obj ){
        return m_jid.equals( ((IMRosterItem)obj).m_jid );
    }
    
    public String toString(){
        
        XMLToString item = new XMLToString( "item" );
        item.addFilledAttribut( "name", m_name );
        item.addFilledAttribut( "jid", m_jid );
        item.addFilledAttribut( "subscription", m_subscription );
        item.addFilledAttribut( "ask", m_ask );
        
        XMLToString group = new XMLToString( "group" );
        group.addTextNode( m_group );
        item.addElement( group );
        
        return item.toString();
    }
}

