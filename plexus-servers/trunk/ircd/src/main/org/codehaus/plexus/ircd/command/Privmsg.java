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
import org.codehaus.plexus.ircd.token.TargetToken;
import org.codehaus.plexus.ircd.token.ToToken;
import org.codehaus.plexus.ircd.token.TrailingToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

public class Privmsg extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 1 );

    /**
     * to execute the Privmsg's command
     * @param userExp the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User userExp )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        MiddleToken[] middleTokens = paramsToken.getMiddleTokens();
        TrailingToken trailingToken = paramsToken.getTrailingToken();
        if ( trailingToken == null )
        {
            return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
        }
        else
        {
            String sMessage = trailingToken.getTrailingValue();
            TargetToken targetToken = (TargetToken) TargetToken.getToken( middleTokens[0].toString() );
            if ( targetToken == null )
            {
                return null;
            }
            ToToken[] toTokens = targetToken.getToTokens();
            if ( toTokens != null )
            {
                for ( int i = 0; i < toTokens.length; i++ )
                {
                    ToToken currentToToken = toTokens[i];
                    NickToken nickToken = currentToToken.getNickToken();
                    org.codehaus.plexus.ircd.user.User user = null;
                    if ( nickToken != null )
                    {
                        try
                        {
                            user = HandleUser.getUser( nickToken.toString() );
                        }
                        catch ( IRCException e )
                        {
                            userExp.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), e.getReplies(), e.getParameters() ) ) );
                        }
                        if ( user != null )
                        {
                            if ( !user.isAway() )
                            {
                                if ( sMessage == null || sMessage.equals( VOID ) )
                                {
                                    userExp.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), Replies.ERR_NOTEXTTOSEND, new String[]{} ) ) );
                                }
                                else
                                {
                                    user.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), user.getNickName(), getName(), VOID, sMessage ) ) );
                                }
                            }
                            else
                            {
                                userExp.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), Replies.RPL_AWAY, new String[]{user.getNickName(), user.getAwayMessage()} ) ) );
                            }
                            continue;
                        }
                    }
                    ChannelToken channelToken = currentToToken.getChannelToken();
                    if ( channelToken != null )
                    {
                        try
                        {
                            Channel channel = HandleChannel.getChannel( channelToken.toString() );
                            if ( channel.isAllowedToSpeak( userExp ) )
                            {
                                channel.sendMessage( userExp.getNickName(), sMessage );
                            }
                        }
                        catch ( IRCException e )
                        {
                            userExp.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), e.getReplies(), e.getParameters() ) ) );
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * to get the settings of Privmsg's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

