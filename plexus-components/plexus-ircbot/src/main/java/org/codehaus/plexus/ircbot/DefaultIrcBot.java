package org.codehaus.plexus.ircbot;

import org.codehaus.plexus.ircbot.botlet.manager.BotletManager;
import org.codehaus.plexus.ircbot.botlet.manager.BotletNotFoundException;
import org.codehaus.plexus.ircbot.botlet.Botlet;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import java.util.Iterator;

public class DefaultIrcBot
    extends AbstractIrcBot
    implements Startable
{
    public boolean running;

    private int port;

    private String host;

    private String channel;

    private BotletManager botletManager;

    protected void processPrivateMessage( String user, String text )
    {
        if ( text.startsWith( "!" ) && text.length() > 1 )
        {
            String command = null;

            String commandText = null;

            int i = text.indexOf( " " );

            if ( i > 1 )
            {
                command = text.substring( 1, i );

                commandText = text.substring( i + 1);
            }
            else
            {
                command = text.substring( 1 );
            }

            try
            {
                botletManager.lookup( command ).handleCommand( this, channel, user, commandText );
            }
            catch ( BotletNotFoundException e )
            {
                // do nothing we don't know what that command is but another
                // bot might be able to handle it so we'll be quiet.
            }
        }

        for ( Iterator i = botletManager.getBotlets().values().iterator(); i.hasNext(); )
        {
            ((Botlet)i.next()).handleText( this, channel, user, text );
        }
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        connect( host, port );
        
        logon();

        running = true;

        while ( running )
        {
            service();
        }
    }

    public void stop()
        throws StoppingException
    {
        running = false;

        logoff();

        disconnect();
    }
}
