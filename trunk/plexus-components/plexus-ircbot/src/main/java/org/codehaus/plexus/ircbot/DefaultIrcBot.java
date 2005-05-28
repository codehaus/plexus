package org.codehaus.plexus.ircbot;

import org.codehaus.plexus.ircbot.botlet.manager.BotletManager;
import org.codehaus.plexus.ircbot.botlet.manager.BotletNotFoundException;
import org.codehaus.plexus.ircbot.botlet.Botlet;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;

public class DefaultIrcBot
    implements IrcBot, Startable
{
    private BufferedReader input;

    private BufferedWriter output;

    private Socket clientSocket;

    public boolean running;

    private int port;

    private String host;

    private String botName;

    private String botDescription;

    private String channel;

    private BotletManager botletManager;

    protected void connect( String serverHostname, int serverPort )
    {
        try
        {
            clientSocket = new Socket( serverHostname, serverPort );
        }
        catch ( Exception e )
        {
            System.err.println( "error connecting to IRC server" );

            e.printStackTrace();

            System.exit( 0 );
        }

        InputStream inputStream = null;

        OutputStream outputStream = null;

        try
        {
            inputStream = clientSocket.getInputStream();

            outputStream = clientSocket.getOutputStream();
        }
        catch ( Exception e )
        {
            System.err.println( "error opening streams to IRC server" );

            e.printStackTrace();

            System.exit( 0 );
        }

        input = new BufferedReader( new InputStreamReader( inputStream ) );

        output = new BufferedWriter( new OutputStreamWriter( outputStream ) );

        return;
    }

    protected void disconnect()
    {
        try
        {
            input.close();

            output.close();
        }
        catch ( IOException e )
        {
            System.err.println( "Error disconnecting from IRC server" );

            e.printStackTrace();
        }
    }

    public boolean ircsend( String message )
    {
        System.out.println( "irc: '" + message + "'" );

        try
        {
            output.write( message );

            output.newLine();

            output.flush();
        }
        catch ( IOException e )
        {
            return false;
        }

        return true;

    }

    protected void logoff()
    {
        BufferedWriter bw = output;

        try
        {
            if ( !ircsend( "quit terminating" ) )
                ;

            bw.write( "quit terminating" );

            bw.newLine();

            bw.flush();
        }
        catch ( Exception e )
        {
            System.out.println( "logoff error: " + e );

            System.exit( 0 );
        }
    }

    protected void logon()
    {
        BufferedWriter bw = output;

        try
        {
            bw.write( "user " + botName + " ware2 irc :" + botDescription );

            bw.newLine();

            bw.write( "nick " + botName );

            bw.newLine();

            bw.flush();
        }
        catch ( Exception e )
        {
            System.out.println( "logon error: " + e );

            System.exit( 0 );
        }

        return;
    }

    private void parse_privmsg( String username, String params )
    {
        /*
            params are in the form
            <my nick> :<message>
            or
            <my nick> <message>
        */
        System.out.println( "parse_privmsg passed '" + params + "' from '" + username + "'" );

        String message;

        // get my own nick
        String me = params.substring( 0, params.indexOf( ' ' ) );

        params = params.substring( params.indexOf( ' ' ) + 1 );

        // extract the command
        if ( params.substring( 0, 1 ).equals( ":" ) )
        {
            message = params.substring( 1 );
        }
        else
        {
            message = params.substring( 0 );
        }

        // call the private message processing method
        srv_privmsg( username, message );

        return;
    }

    /**
     * Checks for a ping and replys with a pong
     *
     * @param msg java.lang.String the IRC message
     */
    private boolean pingpong( String msg )
        throws IOException
    {
        if ( msg.substring( 0, 4 ).equalsIgnoreCase( "ping" ) )
        {
            // send a pong back
            String pongmsg = "pong " + msg.substring( 5 );

            output.write( pongmsg );

            output.newLine();

            output.flush();

            System.out.println( "ping pong" );

            return true;
        }

        return false;
    }

    protected void send_notice( String username, String message )
    {
        String command = "notice " + username + " :" + message;

        ircsend( command );

        return;
    }

    protected void send_privmsg( String username, String message )
    {
        String command = "privmsg " + username + " :" + message;

        ircsend( command );

        return;
    }

    protected void service()
    {
        try
        {
            if ( input.ready() )
            {
                String msg = input.readLine();

                if ( !pingpong( msg ) )
                {
                    String prefix = null;

                    String command = null;

                    String params = null;

                    // check for the prefix
                    if ( msg.substring( 0, 1 ).equals( ":" ) )
                    {
                        prefix = msg.substring( 1, msg.indexOf( ' ' ) );

                        msg = msg.substring( msg.indexOf( ' ' ) + 1 );
                    }

                    // extract the command
                    command = msg.substring( 0, msg.indexOf( ' ' ) );

                    // get the parameters (the rest of the message)
                    params = msg.substring( msg.indexOf( ' ' ) + 1 );

                    System.out.println( "prefix: '" + prefix + "' command: '" + command + "' params: '" + params + "'" );

                    // deal with privmsgs
                    if ( command.equalsIgnoreCase( "privmsg" ) )
                    {
                        String username = null;

                        if ( prefix.indexOf( '!' ) != -1 )
                        {
                            username = prefix.substring( 0, prefix.indexOf( "!" ) );
                        }
                        else
                        {
                            username = prefix;
                        }

                        parse_privmsg( username, params );
                    }
                }
            }
            else
            {
                try
                {
                    Thread.sleep( 100 );
                }
                catch ( InterruptedException e )
                {
                }

            }
        }
        catch ( IOException e )
        {
            System.out.println( "error: " + e );

            System.exit( 0 );
        }
    }

    protected void srv_privmsg( String user, String text )
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
