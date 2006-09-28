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
package net.java.dev.openim;

import java.util.Collection;

import net.java.dev.openim.data.jabber.IMPresence;


/**
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public interface IMPresenceHolder 
{
    public void setPresence( String jidAndRessource, IMPresence presence );
    public Collection getPresence( String jid );
    public IMPresence removePresence( String jidAndRessource );

    public void registerListener(IMPresenceListener listener);
    public void unregisterListener(IMPresenceListener listener);
}


