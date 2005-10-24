/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.ircbot.IrcBot;

import java.util.Date;
import java.text.DateFormat;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TellBotlet
    extends AbstractBotlet
{
    public void handleCommand( IrcBot bot, String channel, String user, String request )
    {
        String s[] = StringUtils.split( request );

        if ( s.length < 3 )
        {
            return;
        }

        String name = s[0];

        StringBuffer sb = new StringBuffer();

        sb.append( name ).append( " " );

        for ( int i = 2; i < s.length; i++ )
        {
            sb.append( s[i] ).append( " " );
        }

        message( bot, channel, sb.toString() );
    }
}
