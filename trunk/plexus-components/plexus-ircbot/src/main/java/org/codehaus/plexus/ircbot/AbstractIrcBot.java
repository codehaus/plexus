package org.codehaus.plexus.ircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractIrcBot
    implements IrcBot
{
    private BufferedReader input;

    private BufferedWriter output;

    private Socket clientSocket;

    private String botName;

    private String botDescription;

    public void connect( String serverHostname, int serverPort )
    {
        connect( serverHostname, serverPort, null );
    }
    
    public void connect( String serverHostname, int serverPort, String botName )
    {
        this.botName = botName;
        
        try
        {
            clientSocket = new Socket( serverHostname, serverPort );
        }
        catch ( Exception e )
        {
            System.err.println( "error connecting to IRC server" );

            e.printStackTrace();
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
        }

        input = new BufferedReader( new InputStreamReader( inputStream ) );

        output = new BufferedWriter( new OutputStreamWriter( outputStream ) );

        return;
    }

    public void disconnect()
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

    public void logoff()
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

            e.printStackTrace();
        }
    }

    public void logon()
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

            e.printStackTrace();
        }

        return;
    }

    private void parsePrivateMessage( String username, String params )
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
        processPrivateMessage( username, message );

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

    public boolean sendNotice( String username, String message )
    {
        String command = "notice " + username + " :" + message;

        return ircsend( command );
    }

    public boolean sendPrivateMessage( String username, String message )
    {
        String command = "privmsg " + username + " :" + message;

        return ircsend( command );
    }

    public boolean sendMessageToChannel( String channel, String message )
    {
        return sendPrivateMessage( channel, message );
    }

    public void service()
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

                        parsePrivateMessage( username, params );
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

            e.printStackTrace();
        }
    }

    public String getLogin()
    {
        return botName;
    }

    public void setLogin( String login )
    {
        this.botName = login;
    }

    public String getFullName()
    {
        return botDescription;
    }

    public void setFullName( String fullName )
    {
        this.botDescription = fullName;
    }

    protected void processPrivateMessage( String user, String text )
    {
    }
}
