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

/**
 * @author AlAg
 */
public interface IMPresence extends Transitable 
{
    public static final String TYPE_AVAILABLE   = "available";
    public static final String TYPE_UNAVAILABLE = "unavailable";
    public static final String TYPE_SUBSCRIBE   = "subscribe";
    public static final String TYPE_SUBSCRIBED  = "subscribed";
    public static final String TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String TYPE_UNSUBSCRIBED= "unsubscribed";
    public static final String TYPE_PROBE       = "probe";

    public void setStatus( String status );
    public String getStatus();
    
    public String getPriority();
    public void setPriority( String priority );

    public void setShow( String show );
    public String getShow();
    
    public Object clone();
    
}

