/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

import org.codehaus.plexus.ircbot.IrcBot;

import java.util.Date;
import java.text.DateFormat;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TimeBotlet
    extends AbstractBotlet
{
    public void handleCommand( IrcBot bot, String channel, String user, String request )
    {
        Date now = new Date();

        DateFormat nowTime = DateFormat.getTimeInstance();

        message( bot, channel, "the time is " + nowTime.format( now ) );
    }
}
