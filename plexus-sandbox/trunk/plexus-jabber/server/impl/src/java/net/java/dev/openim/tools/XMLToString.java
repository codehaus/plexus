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
package net.java.dev.openim.tools;

import org.apache.commons.lang.StringUtils;

/**
 * @author AlAg
 */
public class XMLToString {
    
    private String m_elementName;
    private StringBuffer m_buffer;
    private StringBuffer m_innerBuffer;
    
    public XMLToString( String elementName ){
        m_buffer = new StringBuffer();
        m_buffer.append( '<' ).append( elementName );
        m_elementName = elementName;
    }
    
    public void addAttribut( String name, String value ){
        if( name != null && name.length() > 0 && value != null ){
            m_buffer.append( ' ' ).append( name ).append( "='" ).append( value ).append( "'" );
        }
    }
    public void addFilledAttribut( String name, String value ){
        if( name != null && name.length() > 0 && value != null && value.length() > 0 ){
            m_buffer.append( ' ' ).append( name ).append( "='" ).append( value ).append( "'" );
        }
    }
    
    public void addTextNode( String text ){
        if( text != null && text.length() > 0 ){
            if( m_innerBuffer == null ){
                m_innerBuffer = new StringBuffer();
            }
            m_innerBuffer.append( convert( text ) );
        }
    }

    public void addStringElement( String stringElement ){
        if( stringElement != null ){
            if( m_innerBuffer == null ){
                m_innerBuffer = new StringBuffer();
            }
            m_innerBuffer.append( stringElement );
        }
    }
    
    public void addElement( XMLToString xmlToString ){
        if( xmlToString != null  ){
            if( m_innerBuffer == null ){
                m_innerBuffer = new StringBuffer();
            }
            m_innerBuffer.append( xmlToString.toStringBuffer() );
        }
    }
    
    public String toString(){
        return toStringBuffer().toString();
    }

    public StringBuffer toStringBuffer(){
        StringBuffer buffer = new StringBuffer();
        if( m_innerBuffer != null ){
            buffer.append( m_buffer ).append( '>' ).append( m_innerBuffer ).append( "</" ).append( m_elementName ).append( '>' );
        }
        else{
            buffer.append( m_buffer ).append( "/>" );
        }
        return buffer;
    }
    
    
    // -----------------------------------------------------------------------
    // should be optimized...
    private static final String convert( String s ){
        s = StringUtils.replace( s, "&", "&amp;" );
        s = StringUtils.replace( s, "<", "&lt;" );
        s = StringUtils.replace( s, ">", "&gt;" );
        return s;
    }
    
    
    
}

