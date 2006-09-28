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
import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.data.Deferrable;


/**
 * @author AlAg
 * @author PV
 */
public class IMMessage implements Transitable, Deferrable
{
    public static final String TYPE_CHAT = "chat";

    
    private String m_to;
    private String m_from;
    private String m_type;
    private String m_subject;
    private String m_body;
    private String m_thread;

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
    
    public final void setSubject( String subject ){
        m_subject = subject;
    }
    public final String getSubject(){
        return m_subject;
    }
    

    public final void setBody( String body ){
        m_body = body;
    }
    public final String getBody(){
        return m_body;
    }
    

    public final void setThread( String thread ){
        m_thread = thread;
    }
    public final String getThread(){
        return m_thread;
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
        XMLToString message = new XMLToString( "message" );
        message.addFilledAttribut( "to", m_to );
        message.addFilledAttribut( "from", m_from );
        message.addFilledAttribut( "type", m_type );
        
        if( m_subject != null ){
            XMLToString subject = new XMLToString( "subject" );
            subject.addTextNode( m_subject );
            message.addElement( subject );
        }
        
        if( m_body != null ){
            XMLToString body = new XMLToString( "body" );
            body.addTextNode( m_body );
            message.addElement( body );
        }
         
        if( m_thread != null ){
            XMLToString thread = new XMLToString( "thread" );
            thread.addTextNode( m_thread );
            message.addElement( thread );
        }
        
        if( m_error != null ){
            XMLToString error = new XMLToString( "error" );
            error.addTextNode( m_error );
            if( m_errorCode != null ){
                error.addFilledAttribut( "code", m_errorCode.toString() );
            }
            message.addElement( error );
        }        
        
        return message.toString();
        
    }

}

