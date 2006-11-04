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
package net.java.dev.openim.data;

import java.io.Serializable;

import net.java.dev.openim.tools.Digester;

/**
 * @avalon.component version="1.0" name="Account" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.data.Account"
 *
 * @author AlAg
 */
public class AccountImpl implements Serializable, Account {
        
    private String m_name;
    private String m_password;
    
    public final void setName( String name ){
        m_name = name;
    }
    public final String getName(){
        return m_name;
    }

    
    public final void setPassword( String password ){
        m_password = password;
    }
    public final String getPassword(){
        return m_password;
    }
    
    // -----------------------------------------------------------------------
    public boolean isAuthenticationTypeSupported( int type ){
        boolean isSupported = false;
        if( type == Account.AUTH_TYPE_DIGEST || type == Account.AUTH_TYPE_PLAIN ){
            isSupported = true;
        }
        return isSupported;
    }
    
    // -----------------------------------------------------------------------
    public final void authenticate( int type, String value, String sessionId ) throws Exception {
        if( type == AUTH_TYPE_PLAIN ){
            if( !m_password.equals( value ) ){
                throw new Exception( "Unvalid plain password" );
            }

        }
        else if( type == AUTH_TYPE_DIGEST ){ 
            String digest = Digester.digest( sessionId + m_password );    

            if( !digest.equals( value ) ){
                throw new Exception( "Unvalid digest password" + " \nGot   : " + value + "\nExpect: " + digest );
            }

        }
    }
    
    
    // -----------------------------------------------------------------------
    public String toString(){
        String s = "Username: " + m_name + " password: " + m_password;
        return s;
    }

    
    
}

