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

/**
 * @version 1.0
 *
 * @author AlAg
 */
public interface Account {
    
    public static final int AUTH_TYPE_PLAIN = 1;
    public static final int AUTH_TYPE_DIGEST = 2;
    
    public void setName( String name );
    public String getName();

    
    public void setPassword( String password );
    public String getPassword();
    

    public boolean isAuthenticationTypeSupported( int type );
    public void authenticate( int type, String value, String sessionId ) throws Exception;
        
    
}

