/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.ircbot.botlet;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.ircbot.IrcBot;

import java.util.Date;
import java.text.DateFormat;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoggerBotlet
    extends AbstractBotlet
{
    private boolean logging;

    private PrintWriter log;

    public void handleCommand( IrcBot bot, String channel, String user, String request )
    {
        if ( request.startsWith( "start" ) )
        {
            start( bot, channel );
        }
        else if ( request.startsWith( "stop" ) )
        {
            stop( bot, channel );
        }
        else if ( request.startsWith( "status" ) )
        {
            status( bot, channel );
        }
    }

    public void handleText( IrcBot bot, String channel, String user, String text )
    {
        if ( logging )
        {
            log.println( "<" + user + "> " + text );

            log.flush();
        }
    }

    protected void status( IrcBot bot, String channel )
    {
        message( bot, channel, "I'm not giving you anything until you give me some spanish peanuts." );
    }

    protected void stop( IrcBot bot, String channel )
    {
        message( bot, channel, "Fine. Up yours. I won't record you anymore." );

        log.close();

        log = null;

        logging = false;
    }

    protected void start( IrcBot bot, String channel )
    {
        message( bot, channel, "I will start logging your fascinating conversation, I promise not to tell ACO." );

        logging = true;

        if ( log == null )
        {
            try
            {
                log = new PrintWriter( new FileWriter( "log.txt", true ) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

}