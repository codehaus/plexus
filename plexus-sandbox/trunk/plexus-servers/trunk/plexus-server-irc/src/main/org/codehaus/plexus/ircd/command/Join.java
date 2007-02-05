/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.channel.Channel;
import org.codehaus.plexus.ircd.channel.HandleChannel;
import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.AnyToken;
import org.codehaus.plexus.ircd.token.ChannelToken;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.TargetToken;
import org.codehaus.plexus.ircd.token.ToToken;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import org.codehaus.plexus.ircd.user.User;

import java.util.Vector;

public class Join extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 2 );

    /**
     * to add in the list of messages the names of the current members of the channel
     * @param vCurrentMessages the current list of message
     * @param channel the corresponding channel
     * @param sNickName the nickname of the sender
     */
    private void addUserList( Vector vCurrentMessages, Channel channel, String sNickName )
    {
        String topic;
        if ( channel != null && ( topic = channel.getTopic() ) != null && !topic.equals( VOID ) )
        {
            vCurrentMessages.addElement( new Message( sNickName, Replies.RPL_TOPIC, new String[]{channel.getName(), topic} ) );
        }
        String[] sUsers = channel.getUserList();
        for ( int j = 0; j < sUsers.length; j++ )
        {
            vCurrentMessages.addElement( new Message( sNickName, Replies.RPL_NAMREPLY, new String[]{channel.getName(), sUsers[j]} ) );
        }
        vCurrentMessages.addElement( new Message( sNickName, Replies.RPL_ENDOFNAMES, new String[]{channel.getName()} ) );
    }

    /**
     * to execute the Join's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( User user )
    {
        System.out.println( "executing join command" );

        MiddleToken[] middleTokens = getRequestMessage().getParamsToken().getMiddleTokens();

        TargetToken targetToken = (TargetToken) TargetToken.getToken( middleTokens[0].toString() );

        System.out.println( "targetToken = " + targetToken );

        if ( targetToken == null )
        {
            return null;
        }
        ToToken[] toTokensPwd = null;
        if ( middleTokens.length > 1 )
        {
            TargetToken targetTokenPwd = (TargetToken) TargetToken.getToken( middleTokens[1].toString() );
            if ( targetTokenPwd != null )
            {
                toTokensPwd = targetTokenPwd.getToTokens();
            }
        }
        ToToken[] toTokens = targetToken.getToTokens();
        Vector vMessage = new Vector();
        if ( toTokens != null )
        {
            for ( int i = 0; i < toTokens.length; i++ )
            {
                ToToken currentToToken = toTokens[i];
                ChannelToken channelToken = currentToToken.getChannelToken();
                Channel channel = null;
                if ( channelToken == null )
                {
                    continue;
                }
                String channelName = channelToken.getChannelName();
                try
                {
                    channel = HandleChannel.getChannel( channelName );
                }
                catch ( IRCException e )
                {
                }
                try
                {
                    String sNickName = user.getNickName();
                    if ( channel != null )
                    {
                        String sPassword = getPassword( toTokensPwd, i );
                        channel.checkUser( user, sPassword );
                    }
                    HandleChannel.addUser( user, channelName );
                    if ( channel == null )
                    {
                        channel = HandleChannel.getChannel( channelName );
                    }
                    addUserList( vMessage, channel, sNickName );
                }
                catch ( IRCException e )
                {
                    vMessage.addElement( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
                }
                catch ( FatalException e )
                {
                }
            }
            return Message.vector2Messages( vMessage );
        }
        return null;
    }

    /**
     * to get the password in the list of parameters
     * @param toTokensPwd the list of parameters
     * @param index the index of the requested parameter
     * @return the requested parameter
     */
    private String getPassword( ToToken[] toTokensPwd, int index )
    {
        if ( toTokensPwd != null && toTokensPwd.length > index )
        {
            AnyToken anyToken = toTokensPwd[index].getAnyToken();
            return anyToken.toString();
        }
        return null;
    }

    /**
     * to get the settings of Join's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

