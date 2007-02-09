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
 * @author AlAg
 * @author PV
 */
public interface Transitable 
{
    public static final String TYPE_ERROR = "error";

    public void setTo( String to );
    public String getTo();
    
    public void setFrom( String from );
    public String getFrom();

    public void setType( String type );
    public String getType();
    
    public void setError( String errormsg );
    public void setErrorCode( int errorCode );
    
    public String toString(String encoding);
}

