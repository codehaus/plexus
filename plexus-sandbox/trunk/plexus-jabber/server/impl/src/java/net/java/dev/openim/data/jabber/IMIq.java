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

import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.tools.XMLToString;

/**
 * @author AlAg
 * @author PV
 */
public class IMIq implements Transitable 
{
    public static final String TYPE_GET     = "get";
    public static final String TYPE_SET     = "set";
    public static final String TYPE_RESULT  = "result";
    public static final String TYPE_ERROR   = "error";
    
    private String m_type;
    private String m_to;
    private String m_id;
    private String m_from;
    private String m_data;
    
    private String m_error;
    private Integer m_errorCode;

    public final void setType( String type ){
        m_type = type;
    }
    public final String getType(){
        return m_type;
    }
    
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
    
    public final void setId( String id ){
        m_id = id;
    }
    public final String getId(){
        return m_id;
    }
    
    public final void setStringData( String data ){
        m_data = data;
    }
    
    public final void setError( String error ){
        m_error = error;
    }
    
    public void setErrorCode( int errorCode ) {
        m_errorCode = new Integer( errorCode );
    }
    
    public String toString(String enconding) {
        return toString();
    }
    
    public String toString(){
        
        XMLToString iq = new XMLToString( "iq" );
        iq.addFilledAttribut( "to", m_to );
        iq.addFilledAttribut( "from", m_from );
        iq.addFilledAttribut( "type", m_type );
        iq.addFilledAttribut( "id", m_id );
        
        if( m_error != null ){
            XMLToString error = new XMLToString( "error" );
            error.addTextNode( m_error );
            if( m_errorCode != null ){
                error.addFilledAttribut( "code", m_errorCode.toString() );
            }
            iq.addElement( error );
        }        
        
        iq.addStringElement( m_data );
        
        return iq.toString();
        
    }
    
}

