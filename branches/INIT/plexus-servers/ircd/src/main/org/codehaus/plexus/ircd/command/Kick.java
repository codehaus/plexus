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
import org.codehaus.plexus.ircd.token.NickToken;
import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.token.TrailingToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;

public class Kick extends Command
{

    private static final Settings settings = new Settings( true, true, 2, 2 );

    /**
     * to execute the Kick's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        MiddleToken[] middleTokens = paramsToken.getMiddleTokens();
        ChannelToken channelToken = (ChannelToken) ChannelToken.getToken( middleTokens[0].toString() );
        if ( channelToken == null )
        {
            return null;
        }
        NickToken nickToken = (NickToken) NickToken.getToken( middleTokens[1].toString() );
        if ( nickToken == null )
        {
            return null;
        }
        String channelName = channelToken.getChannelName();
        String login = nickToken.getNickName();
        try
        {
            Channel channel = HandleChannel.getChannel( channelName );
            if ( channel.isOperator( user ) && channel.checkUserOn( login ) )
            {
                org.codehaus.plexus.ircd.user.User userToKick = HandleUser.getUser( login );
                String sComment = user.getNickName();
                TrailingToken trailing = paramsToken.getTrailingToken();
                if ( trailing != null )
                {
                    sComment = trailing.getTrailingValue();
                }
                HandleChannel.removeUser( userToKick, channel.getName(), true, user.getUserFullName(), getName(), channel.getName() + SPACE + userToKick.getNickName(), sComment );
            }
        }
        catch ( IRCException ircE )
        {
            return Message.createSingleMessage( new Message( user.getNickName(), ircE.getReplies(), ircE.getParameters() ) );
        }
        return null;
    }

    /**
     * to get the settings of Kick's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

