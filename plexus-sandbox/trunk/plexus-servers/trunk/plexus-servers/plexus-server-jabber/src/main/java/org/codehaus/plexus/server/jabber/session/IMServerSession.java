/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.session;


/**
 * @author AlAg
 * @version 1.0
 */
public interface IMServerSession
    extends IMSession
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


