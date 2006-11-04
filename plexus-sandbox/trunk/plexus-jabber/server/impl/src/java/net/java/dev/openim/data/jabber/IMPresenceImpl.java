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

/**
 * @author AlAg
 * @author PV
 */
public class IMPresenceImpl implements IMPresence, java.io.Serializable
{
    

    
    private String m_to;
    private String m_from;
    
    private String m_type;
    private String m_show;
    private String m_priority;
    private String m_status;
    
    private String m_error;
    private Integer m_errorCode;

    public final void setTo( String to ){
        m_to = to;
    }
    public final String getTo(){
        return m_to;
    }
    
    public final void setFrom( String from ){
        m_from = from;
    }
    public final String getFrom(){
        return m_from;
    }

    public final void setType( String type ){
        m_type = type;
    }
    public final String getType(){
        return m_type;
    }
    
    public final void setShow( String show ){
        m_show = show;
    }
    public final String getShow(){
        return m_show;
    }
    
    public final void setPriority( String priority ){
        m_priority = priority;
    }
    public final String getPriority(){
        return m_priority;
    }
    
    public final void setStatus( String status ){
        m_status = status;
    }
    public final String getStatus(){
        return m_status;
    }
    

    public final void setError( String error ){
        m_error = error;
    }
    
    public void setErrorCode( int errorCode ) {
        m_errorCode = new Integer( errorCode );
    }

    
    public Object clone(){
        IMPresenceImpl clone = new IMPresenceImpl();

        clone.m_to = m_to;
        clone.m_from = m_from;
        clone.m_type = m_type;
        clone.m_show = m_show;
        clone.m_priority = m_priority;
        clone.m_status = m_status;
        clone.m_error = m_error;
        clone.m_errorCode = m_errorCode;
        
        return clone;
    }

    public String toString(String encoding) {
        return toString();
    }

    public String toString(){
        
        XMLToString presence = new XMLToString( "presence" );
        presence.addFilledAttribut( "to", m_to );
        presence.addFilledAttribut( "from", m_from );
        presence.addFilledAttribut( "type", m_type );


        if( m_priority != null ){
            XMLToString priority = new XMLToString( "priority" );
            priority.addTextNode( m_priority );
            presence.addElement( priority );
        }
        
        
        if( m_show != null ){
            XMLToString show = new XMLToString( "show" );
            show.addTextNode( m_show );
            presence.addElement( show );
        }
        
        if( m_status != null ){
            XMLToString status = new XMLToString( "status" );
            status.addTextNode( m_status );
            presence.addElement( status );
        }
        
        if( m_error != null ){
            XMLToString error = new XMLToString( "error" );
            error.addTextNode( m_error );
            if( m_errorCode != null ){
                error.addFilledAttribut( "code", m_errorCode.toString() );
            }
            presence.addElement( error );
        }        

        return presence.toString();
    }
    
    
}

