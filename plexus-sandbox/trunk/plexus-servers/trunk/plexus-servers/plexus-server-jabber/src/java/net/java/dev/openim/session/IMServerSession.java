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
package net.java.dev.openim.session;



/**
 * @version 1.0
 * @author AlAg
 */
public interface IMServerSession extends IMSession
{
    public boolean getDialbackValid();
    public void setDialbackValid( boolean value );

    public String getDialbackValue();
    public void setDialbackValue( String dialback );

    public String getRemoteHostname();
    public void setRemoteHostname( String hostname );
    
    public IMServerSession getTwinSession();
    public void setTwinSession( IMServerSession session );    
    
}


