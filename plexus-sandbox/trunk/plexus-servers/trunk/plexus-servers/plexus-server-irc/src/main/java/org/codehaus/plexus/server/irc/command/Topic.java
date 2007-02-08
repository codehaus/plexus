/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.server.irc.command;

import org.codehaus.plexus.server.irc.channel.Channel;
import org.codehaus.plexus.server.irc.channel.HandleChannel;
import org.codehaus.plexus.server.irc.exception.FatalException;
import org.codehaus.plexus.server.irc.exception.IRCException;
import org.codehaus.plexus.server.irc.token.ChannelToken;
import org.codehaus.plexus.server.irc.token.MiddleToken;
import org.codehaus.plexus.server.irc.token.ParamsToken;
import org.codehaus.plexus.server.irc.token.TrailingToken;
import org.codehaus.plexus.server.irc.utils.Message;
import org.codehaus.plexus.server.irc.utils.Replies;

public class Topic extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 1 );

    /**
     * to execute the Topic's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.server.irc.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        MiddleToken[] middleTokens = paramsToken.getMiddleTokens();
        TrailingToken trailingToken = paramsToken.getTrailingToken();
        ChannelToken channelToken = (ChannelToken) ChannelToken.getToken( middleTokens[0].toString() );
        if ( channelToken == null )
        {
            return null;
        }
        Channel channel = null;
        try
        {
            channel = HandleChannel.getChannel( channelToken.toString() );
        }
        catch ( IRCException e )
        {
            return Message.createSingleMessage( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
        }
        if ( trailingToken != null )
        {
            try
            {
                if ( channel.isAllowedToChangeTopic( user ) )
                {
                    String topic = trailingToken.getTrailingValue();
                    channel.setTopic( topic );
                    HandleChannel.udateChannel( channel );
                    channel.sendMessage( user.getNickName(), getName(), channel.getName(), topic );
                }
            }
            catch ( IRCException e )
            {
                return Message.createSingleMessage( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
            }
            catch ( FatalException e )
            {
            }
        }
        else
        {
            if ( !channel.getTopic().equals( VOID ) )
            {
                return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_TOPIC, new String[]{channel.getName(), channel.getTopic()} ) );
            }
            else
            {
                return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_NOTOPIC, new String[]{channel.getName()} ) );
            }
        }
        return null;
    }

    /**
     * to get the settings of Topic's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

