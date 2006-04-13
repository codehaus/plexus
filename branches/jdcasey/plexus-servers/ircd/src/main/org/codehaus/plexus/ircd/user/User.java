/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.user;

import org.codehaus.plexus.ircd.channel.Channel;
import org.codehaus.plexus.ircd.channel.HandleChannel;
import org.codehaus.plexus.ircd.channel.Rights;
import org.codehaus.plexus.ircd.command.Quit;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.properties.Config;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class User implements IRCString
{

    private String nickName;
    private String userName;
    private String hostName;
    private String serverName;
    private String realName;
    private String awayMessage;
    private boolean invisible;
    private boolean operator;
    private boolean away;
    private ArrayList channelList;
    private Socket socket;
    private Listening listeningThread;
    private Talking talkingThread;
    private static String defaultNickName;

    static
    {
        defaultNickName = Config.getValue( "user.default.nickname", "AUTH" );
    }

    /**
     * @param socket the socket corresponding to the connection with the client
     */
    public User( Socket socket ) throws IOException
    {
        this.socket = socket;
        this.channelList = new ArrayList();
    }

    /**
     * to add a channel's name to the channel list
     * @param sName the name of the channel to add
     */
    public void addChannel( String sName )
    {
        if ( channelList.indexOf( sName ) == -1 )
        {
            channelList.add( sName );
        }
    }

    /**
     * to add responses to the list of responses to send back to the client
     * @param messages the messages to add
     */
    public synchronized void addResponses( Message[] messages )
    {
        talkingThread.addResponses( messages );
    }

    /**
     * to disconnect the client
     */
    public void disconnect() throws Exception
    {
        disconnect( VOID );
    }

    /**
     * to disconnect the client with a special exit's message
     * @param sMessage the exist's message
     */
    public void disconnect( String sMessage ) throws Exception
    {
        Channel[] channels = getChannels();
        for ( int i = 0; i < channels.length; i++ )
        {
            channels[i].sendMessage( getNickName(), VOID, new Quit().getName(), VOID, sMessage );
        }
        disconnectUser();
    }

    /**
     * to handle only the dosconnection of the user
     */
    public synchronized void disconnectUser() throws Exception
    {
        HandleUser.deleteUser( getNickName() );
        listeningThread.disconnect();
        talkingThread.disconnect();
        talkingThread.notifyTalking();
        socket.close();
    }

    /**
     * to get the away message
     */
    public String getAwayMessage()
    {
        return awayMessage;
    }

    /**
     * to get a channel's name from the channel's list
     * @param index the index of the channel
     */
    private String getChannelAt( int index )
    {
        if ( channelList.size() > index )
        {
            return (String) channelList.get( index );
        }
        else
        {
            return null;
        }
    }

    /**
     * to get the list of channels which contain the current user and which can be see
     * by the user who is asking for
     * @param sAskerNickName the nickname of the user who asks for
     */
    public String[] getChannelList( String sAskerNickName )
    {
        Vector vAllChannels = new Vector();
        StringBuffer buffer = new StringBuffer( Message.getSizeLimit() + 20 );
        String sCurrentChannel;
        for ( int i = 0; i < channelList.size(); i++ )
        {
            sCurrentChannel = getDisplayedValue( getChannelAt( i ), sAskerNickName );
            if ( !sCurrentChannel.equals( VOID ) )
            {
                buffer.append( sCurrentChannel + SPACE );
            }
            if ( buffer.length() > Message.getSizeLimit() )
            {
                vAllChannels.addElement( buffer.toString() );
                buffer = new StringBuffer( Message.getSizeLimit() + 20 );
            }
        }
        if ( buffer.length() > 0 )
        {
            vAllChannels.addElement( buffer.toString() );
        }
        String[] sChannels = new String[vAllChannels.size()];
        return (String[]) vAllChannels.toArray( sChannels );
    }

    /**
     * to get all the list of channels which contain the current user
     */
    public Channel[] getChannels()
    {
        Vector vAllChannels = new Vector();
        for ( int i = 0; i < channelList.size(); i++ )
        {
            try
            {
                vAllChannels.addElement( HandleChannel.getChannel( (String) channelList.get( i ) ) );
            }
            catch ( Exception e )
            {
            }
        }
        Channel[] channels = new Channel[vAllChannels.size()];
        return (Channel[]) vAllChannels.toArray( channels );
    }

    /**
     * to get the default nickname
     */
    public static String getDefaultNickName()
    {
        return defaultNickName;
    }

    /**
     * to get the displayed value of a specific channel for a specific user
     * @param sChannelName the name of the channel to display
     * @param sAskerNickName the nickname of the user who is asking for
     */
    private String getDisplayedValue( String sChannelName, String sAskerNickName )
    {
        try
        {
            Channel channel = HandleChannel.getChannel( sChannelName );
            Rights rights = channel.getRights( nickName );
            if ( ( channel.isUserOn( sAskerNickName ) ) || ( !channel.isTypePrivate() && !channel.isTypeSecret() ) )
            {
                if ( rights.isOperator() )
                {
                    return CHANNEL_IS_OPERATOR + sChannelName;
                }
                else if ( rights.isAbleToSpeakIfModerated() )
                {
                    return CHANNEL_CAN_SPEAK_ON_MODERATED + sChannelName;
                }
                else
                {
                    return sChannelName;
                }
            }
        }
        catch ( IRCException e )
        {
        }
        return VOID;
    }

    /**
     * to get the host address
     */
    public String getHost()
    {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * to get the host name
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * to get the idle's time of the user
     */
    public long getIdle()
    {
        return listeningThread.getIdle();
    }

    /**
     * to get the modes of the user
     */
    public String getMode()
    {
        StringBuffer buffer = new StringBuffer( 50 );
        buffer.append( PLUS_CHAR );
        if ( isInvisible() )
        {
            buffer.append( USER_INVISIBLE_CHAR );
        }
        if ( isOperator() )
        {
            buffer.append( USER_OPERATOR_CHAR );
        }
        return buffer.toString();
    }

    /**
     * to get the nickname
     */
    public String getNickName()
    {
        return nickName;
    }

    /**
     * to get the nickname if it exists the default nickname otherwise
     */
    public String getNoNullNickName()
    {
        if ( getNickName() == null || getNickName().equals( VOID ) )
        {
            return defaultNickName;
        }
        else
        {
            return getNickName();
        }
    }

    /**
     * to get the real name
     */
    public String getRealName()
    {
        return realName;
    }

    /**
     * to get the server name
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * to get the user's full name
     */
    public String getUserFullName()
    {
        return getNoNullNickName() + "!~" + getUserName() + "@" + getHostName();
    }

    /**
     * to get the user's name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * to know if the user is away
     */
    public boolean isAway()
    {
        return away;
    }

    /**
     * to know if the user is invisible
     */
    public boolean isInvisible()
    {
        return invisible;
    }

    /**
     * to know if the given nickname is matching with the user
     * @param sNickName the nickname to check
     * @throws IRCException ERR_USERSDONTMATCH if the nickname does not match
     */
    public boolean isMatching( String sNickName ) throws IRCException
    {
        if ( sNickName != null && getNickName().equals( sNickName ) )
        {
            return true;
        }
        throw new IRCException( Replies.ERR_USERSDONTMATCH, new String[]{} );
    }

    /**
     * to know if the user is an operator
     */
    public boolean isOperator()
    {
        return operator;
    }

    /**
     * to remove the user from all the channels
     */
    public void remove()
    {
        for ( int i = 0; i < channelList.size(); i++ )
        {
            try
            {
                HandleChannel.removeUser( this, getChannelAt( i ), true, getUserFullName(), new Quit().getName(), VOID, Config.getValue( "command.quit.default.message", "Bye bye" ) );
            }
            catch ( IRCException e )
            {
            }
        }
    }

    /**
     * to remove the away's state
     */
    public void removeAwayState()
    {
        setAway( false );
        setAwayMessage( null );
        addResponses( Message.createSingleMessage( new Message( nickName, Replies.RPL_UNAWAY, new String[]{} ) ) );
    }

    /**
     * to remove the user from a specific channel
     * @param sChannelName the name of the concerning channel
     */
    public void removeChannel( String sChannelName )
    {
        channelList.remove( sChannelName );
    }

    /**
     * to rename the user in all the channels
     * @param sOldNickName the old nickname
     * @param sNewNickName the new nickname
     */
    public void rename( String sOldNickName, String sNewNickName )
    {
        for ( int i = 0; i < channelList.size(); i++ )
        {
            try
            {
                HandleChannel.renameUser( sOldNickName, sNewNickName, getChannelAt( i ) );
            }
            catch ( IRCException e )
            {
            }
        }
    }

    /**
     * to set the away's state
     */
    private void setAway( boolean away )
    {
        this.away = away;
    }

    /**
     * to set the away's message
     * @param awayMessage the away's message
     */
    private void setAwayMessage( String awayMessage )
    {
        this.awayMessage = awayMessage;
    }

    /**
     * to set the away's state with a specific message
     * @param sMessage the away's message
     */
    public void setAwayState( String sMessage )
    {
        setAway( true );
        setAwayMessage( sMessage );
        addResponses( Message.createSingleMessage( new Message( nickName, Replies.RPL_NOWAWAY, new String[]{} ) ) );
    }

    /**
     * to set the default nickname
     */
    public static void setDefaultNickName( String defaultNickName )
    {
        User.defaultNickName = defaultNickName;
    }

    /**
     * to set the host name
     */
    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }

    /**
     * to set the invisible mode
     */
    public void setInvisible( boolean invisible )
    {
        this.invisible = invisible;
    }

    /**
     * to set the nickname
     */
    public void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    /**
     * to set the operator mode
     */
    public void setOperator( boolean operator )
    {
        this.operator = operator;
    }

    /**
     * to set the real name
     */
    public void setRealName( String realName )
    {
        this.realName = realName;
    }

    /**
     * to set the server name
     */
    public void setServerName( String serverName )
    {
        this.serverName = serverName;
    }

    /**
     * to set the user name
     */
    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    /**
     * to start the listening and the talking thread
     */
    public void startProcess() throws IOException
    {
        if ( listeningThread == null )
        {
            listeningThread = new Listening( socket.getInputStream(), this );
            listeningThread.start();
        }

        if ( talkingThread == null )
        {
            talkingThread = new Talking( socket.getOutputStream(), this );
            talkingThread.start();
        }
    }
}
