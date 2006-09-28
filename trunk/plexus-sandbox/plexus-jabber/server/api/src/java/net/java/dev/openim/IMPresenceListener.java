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

import net.java.dev.openim.data.jabber.IMPresence;

/**
 * @author PV
 * @version 1.0
 */
public interface IMPresenceListener {
    public void onSetPresence(String jidAndRessource, IMPresence presence);
    public void onRemovePresence(String jidAndRessource);
}
