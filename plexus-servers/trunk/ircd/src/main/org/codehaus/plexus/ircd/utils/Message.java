/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.utils;

import org.codehaus.plexus.ircd.properties.Config;
import org.codehaus.plexus.ircd.server.Server;
import org.codehaus.plexus.ircd.token.CommandToken;
import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.token.PrefixToken;
import org.codehaus.plexus.ircd.user.User;

import java.text.NumberFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Message implements IRCString
{

    private PrefixToken prefixToken;
    private CommandToken commandToken;
    private ParamsToken paramsToken;
    private static int sizeLimit;
    private int state;
    private static NumberFormat errorCodeFormat = NumberFormat.getInstance();

    static
    {
        sizeLimit = Integer.parseInt( Config.getValue( "message.size.limit", "400" ) );
        errorCodeFormat.setMaximumFractionDigits( 0 );
        errorCodeFormat.setMaximumIntegerDigits( 3 );
        errorCodeFormat.setMinimumFractionDigits( 0 );
        errorCodeFormat.setMinimumIntegerDigits( 3 );
    }

    /**
     * the default message
     */
    public Message()
    {
    }

    /**
     * @param replies the corresponding replies
     * @param sParameters the parameters required
     */
    public Message( Replies replies, String[] sParameters )
    {
        this( User.getDefaultNickName(), replies, sParameters );
    }

    /**
     * @param sLogin the sender's login
     * @param replies the corresponding replies
     * @param sParameters the parameters required
     */
    public Message( String sLogin, Replies replies, String[] sParameters )
    {
        this( sLogin, errorCodeFormat.format( replies.getCode() ), replies.getMessage( sParameters ), VOID );
    }

    /**
     * @param sLogin the sender's login
     * @param sCommand the message's command
     * @param sMiddle  the message's middle
     * @param sTrailing  the message's trailing
     */
    public Message( String sLogin, String sCommand, String sMiddle, String sTrailing )
    {
        this( Server.getHost(), sLogin, sCommand, sMiddle, sTrailing );
    }

    /**
     * @param sPrefix the message's prefix
     * @param sLogin the sender's login
     * @param sCommand the message's command
     * @param sMiddle  the message's middle
     * @param sTrailing  the message's trailing
     */
    public Message( String sPrefix, String sLogin, String sCommand, String sMiddle, String sTrailing )
    {
        setPrefixToken( (PrefixToken) PrefixToken.getToken( sPrefix ) );
        setCommandToken( (CommandToken) CommandToken.getToken( sCommand ) );
        if ( sTrailing == null || sTrailing.equals( VOID ) )
        {
            setParamsToken( (ParamsToken) ParamsToken.getToken( SPACE + sLogin + SPACE + sMiddle ) );
        }
        else
        {
            setParamsToken( (ParamsToken) ParamsToken.getToken( SPACE + sLogin + SPACE + sMiddle + SPACE + DOUBLE_POINTS + sTrailing ) );
        }
    }

    /**
     * to create a list of messages from a single message
     * @return the resulting list
     */
    public static Message[] createSingleMessage( Message message )
    {
        return new Message[]{message};
    }

    /**
     * to get the command's token
     */
    public CommandToken getCommandToken()
    {
        return commandToken;
    }

    /**
     * to get the params's token
     */
    public ParamsToken getParamsToken()
    {
        return paramsToken;
    }

    /**
     * to get the prefix's token
     */
    public PrefixToken getPrefixToken()
    {
        return prefixToken;
    }

    /**
     * to get the message's size limit
     */
    public static int getSizeLimit()
    {
        return sizeLimit;
    }

    /**
     * to get the current state
     */
    public int getState()
    {
        return state;
    }

    /**
     * to know if the message is null
     */
    public boolean isNull()
    {
        return toString().equals( "\015\012" );
    }

    /**
     * to parse the mesages
     * @param sMessages the messages to parse
     * @param iInitialState  the initial state
     * @return the parsed messages
     */
    public static Message[] parse( String sMessages, int iInitialState )
    {
        Message[] messages = null;
        if ( sMessages == null )
        {
            return null;
        }
        String[] splitedMessages = Pattern.compile( "[\015\012|\012]" ).split( sMessages );
        messages = new Message[splitedMessages.length];
        for ( int i = 0; i < splitedMessages.length; i++ )
        {
            messages[i] = new Message();
            messages[i].setState( iInitialState + i );
            String sMessage = splitedMessages[i];
            try
            {
                String sHead = Utilities.getHead( sMessage );
                if ( sMessage.startsWith( DOUBLE_POINTS ) )
                {
                    messages[i].setPrefixToken( (PrefixToken) PrefixToken.getToken( sHead.substring( 1 ) ) );
                    sMessage = Utilities.getRest( sMessage.substring( sHead.length() ) );
                    sHead = Utilities.getHead( sMessage );
                }
                messages[i].setCommandToken( (CommandToken) CommandToken.getToken( sHead ) );
                sMessage = sMessage.substring( sHead.length() );
                messages[i].setParamsToken( (ParamsToken) ParamsToken.getToken( sMessage ) );
            }
            catch ( Exception e )
            {
                Log.log( Level.SEVERE, "Message", "parse", VOID, e );
            }
        }
        return messages;
    }

    /**
     * to set the command's token
     */
    private void setCommandToken( CommandToken commandToken )
    {
        this.commandToken = commandToken;
    }

    /**
     * to set the params's token
     */
    private void setParamsToken( ParamsToken paramsToken )
    {
        this.paramsToken = paramsToken;
    }

    /**
     * to set the prefix's token
     */
    private void setPrefixToken( PrefixToken prefixToken )
    {
        this.prefixToken = prefixToken;
    }

    /**
     * to set the message's size limit
     */
    public static void setSizeLimit( int sizeLimit )
    {
        Message.sizeLimit = sizeLimit;
    }

    /**
     * to set the current state
     */
    private void setState( int state )
    {
        this.state = state;
    }

    /**
     * to get the content of the message
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( 10000 );
        if ( prefixToken != null )
        {
            buffer.append( DOUBLE_POINTS + prefixToken + SPACE );
        }
        if ( commandToken != null )
        {
            buffer.append( commandToken );
        }
        if ( paramsToken != null )
        {
            buffer.append( paramsToken );
        }
        buffer.append( "\015\012" );
        return buffer.toString();
    }

    /**
     * to convert a vector of messages to an array of messages
     * @param vMessage the source
     * @return the resulting array
     */
    public static Message[] vector2Messages( Vector vMessage )
    {
        if ( vMessage != null )
        {
            Message[] messages = new Message[vMessage.size()];
            return (Message[]) vMessage.toArray( messages );
        }
        else
        {
            return null;
        }
    }
}

