/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.channel.Channel;
import org.codehaus.plexus.ircd.channel.HandleChannel;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.ChannelToken;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.token.TargetToken;
import org.codehaus.plexus.ircd.token.ToToken;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import java.util.Enumeration;
import java.util.Vector;

public class List extends Command
{

    private static final Settings settings = new Settings();

    /**
     * to add a channel in the list of messages
     * @param vCurrentMessages the list of current messages
     * @param channel the channel to add
     * @param sNickName the nickname of the sender
     */
    private void addChannelInList( Vector vCurrentMessages, Channel channel, String sNickName )
    {
        if ( channel.isUserOn( sNickName ) || ( !channel.isTypePrivate() && !channel.isTypeSecret() ) )
        {
            vCurrentMessages.addElement( new Message( sNickName, Replies.RPL_LIST, new String[]{channel.getName(), Integer.toString( channel.getUserVisibleNumber() ), channel.getTopic()} ) );
        }
        else if ( channel.isTypePrivate() )
        {
            vCurrentMessages.addElement( new Message( sNickName, Replies.RPL_LIST, new String[]{channel.getName(), Integer.toString( channel.getUserVisibleNumber() )} ) );
        }
    }

    /**
     * to execute the List's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        String sNickName = user.getNickName();
        MiddleToken[] middleTokens = null;

        if ( paramsToken != null )
        {
            middleTokens = paramsToken.getMiddleTokens();
        }

        Vector vMessage = new Vector();
        vMessage.addElement( new Message( sNickName, Replies.RPL_LISTSTART, new String[]{} ) );

        if ( middleTokens == null )
        {
            for ( Enumeration enum = HandleChannel.getAllChannels(); enum.hasMoreElements(); )
            {
                Channel channel = (Channel) enum.nextElement();
                addChannelInList( vMessage, channel, sNickName );
            }
        }
        else if ( middleTokens.length != 1 )
        {
            return null;
        }
        else
        {
            TargetToken targetToken = (TargetToken) TargetToken.getToken( middleTokens[0].toString() );
            if ( targetToken == null )
            {
                return null;
            }
            ToToken[] toTokens = targetToken.getToTokens();
            if ( toTokens == null )
            {
                return null;
            }
            for ( int i = 0; i < toTokens.length; i++ )
            {
                ChannelToken channelToken = toTokens[i].getChannelToken();
                if ( channelToken == null )
                {
                    continue;
                }
                String channelName = channelToken.getChannelName();
                Channel channel = null;
                try
                {
                    channel = HandleChannel.getChannel( channelName );
                }
                catch ( IRCException e )
                {
                }
                if ( channel != null )
                {
                    addChannelInList( vMessage, channel, sNickName );
                }
            }
        }
        vMessage.addElement( new Message( sNickName, Replies.RPL_LISTEND, new String[]{} ) );
        return Message.vector2Messages( vMessage );
    }

    /**
     * to get the settings of List's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}
