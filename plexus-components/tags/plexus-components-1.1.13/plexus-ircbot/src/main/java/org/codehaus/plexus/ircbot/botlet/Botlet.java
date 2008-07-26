/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

import org.codehaus.plexus.ircbot.IrcBot;

/**
 * A botlet can handle any number of irc commands. A botlet
 * may do something simple like respond to a command like
 *
 * !time
 *
 * And simply return the time.
 *
 * A botlet can also handle multiple commands to deal with
 * something like logging and irc channel:
 *
 * !logging start
 *
 * !logging status
 *
 * !logging stop
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Botlet
{
    void handleCommand( IrcBot bot, String channel, String user, String command );

    void handleText( IrcBot bot, String channel, String user, String command );
}
