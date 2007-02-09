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


import net.java.dev.openim.data.jabber.User;
import net.java.dev.openim.data.jabber.IMPresence;


/**
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public interface IMClientSession extends IMSession
{
    // C2S session  (will split)
    public User getUser();
    public void setUser( User user );
    public int getPriority();

    public IMPresence getPresence();
    public void setPresence( IMPresence presence );
}


