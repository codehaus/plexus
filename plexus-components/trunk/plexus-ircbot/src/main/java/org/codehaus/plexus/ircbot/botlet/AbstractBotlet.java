/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

import org.codehaus.plexus.ircbot.IrcBot;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractBotlet
    implements Botlet
{
    protected void message( IrcBot bot, String channel, String message )
    {
        bot.sendMessageToChannel( channel, message );
    }

    public void handleText( IrcBot bot, String channel, String user, String text )
    {
    }
}
