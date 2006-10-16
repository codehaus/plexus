/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.channel.Channel;
import org.codehaus.plexus.ircd.channel.HandleChannel;
import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.ChannelToken;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.UserToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

public class Invite extends Command
{

    private static final Settings settings = new Settings( true, true, 2, 2 );

    /**
     * to execute the Invite's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        MiddleToken[] middleTokens = getRequestMessage().getParamsToken().getMiddleTokens();
        UserToken userToken = (UserToken) UserToken.getToken( middleTokens[0].toString() );
        if ( userToken == null )
        {
            return null;
        }
        ChannelToken channelToken = (ChannelToken) ChannelToken.getToken( middleTokens[1].toString() );
        if ( channelToken == null )
        {
            return null;
        }
        String channelName = channelToken.getChannelName();
        String login = userToken.getUserName();
        try
        {
            Channel channel = HandleChannel.getChannel( channelName );
            if ( channel.isOperator( user ) )
            {
                org.codehaus.plexus.ircd.user.User userTo = HandleUser.getUser( login );
                channel.addGuest( userTo.getNickName() );
                HandleChannel.udateChannel( channel );
                userTo.addResponses( Message.createSingleMessage( new Message( user.getUserFullName(), VOID, getName(), userTo.getNickName() + SPACE + channel.getName(), VOID ) ) );
                if ( userTo.isAway() )
                {
                    return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_AWAY, new String[]{userTo.getNickName(), userTo.getAwayMessage()} ) );
                }
                else
                {
                    return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_INVITING, new String[]{channel.getName(), userTo.getNickName()} ) );
                }
            }
        }
        catch ( IRCException ircE )
        {
            return Message.createSingleMessage( new Message( user.getNickName(), ircE.getReplies(), ircE.getParameters() ) );
        }
        catch ( FatalException e )
        {
        }
        return null;
    }

    /**
     * to get the settings of Invite's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

