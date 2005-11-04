package org.codehaus.plexus.ircbot;

import org.codehaus.plexus.logging.AbstractLogEnabled;

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
    extends AbstractLogEnabled
    implements IrcBot
{
    private BufferedReader input;

    private BufferedWriter output;

    private Socket clientSocket;

    private String botName;

    private String botDescription;

    private String botPassword;

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
            getLogger().error( "error connecting to IRC server", e );

            return;
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
            getLogger().error( "error opening streams to IRC server", e );

            return;
        }

        input = new BufferedReader( new InputStreamReader( inputStream ) );

        output = new BufferedWriter( new OutputStreamWriter( outputStream ) );

        return;
    }

    public void disconnect()
    {
        try
        {
            if ( input != null )
            {
                input.close();
            }

            if ( output != null )
            {
                output.close();
            }
        }
        catch ( IOException e )
        {
            getLogger().error( "Error disconnecting from IRC server", e );
        }
    }

    public boolean ircsend( String message )
    {
        if ( output == null )
        {
            return false;
        }

        getLogger().info( "irc: '" + message + "'" );

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
        if ( output  == null )
        {
            return;
        }

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
            getLogger().error( "logoff error: ", e );
        }
    }

    public void logon()
    {
        if ( output  == null )
        {
            return;
        }

        BufferedWriter bw = output;

        try
        {
            bw.write( "user " + botName + " ware2 irc :" + botDescription );

            bw.newLine();

            bw.write( "nick " + botName );

            bw.newLine();

            bw.flush();

            if ( botPassword != null )
            {
                bw.write( "msg NickServ IDENTIFY " + botPassword );

                bw.newLine();

                bw.flush();

            }
        }
        catch ( Exception e )
        {
            getLogger().error( "logon error: ", e );
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
        getLogger().debug( "parse_privmsg passed '" + params + "' from '" + username + "'" );

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

            getLogger().debug( "ping pong" );

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

                    getLogger().debug( "prefix: '" + prefix + "' command: '" + command + "' params: '" + params + "'" );

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
            getLogger().error( "error: " + e, e );
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

    public void setPassword( String password )
    {
        this.botPassword = password;
    }

    protected void processPrivateMessage( String user, String text )
    {
    }
}
