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
import org.codehaus.plexus.ircd.token.ModeToken;
import org.codehaus.plexus.ircd.token.NickToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import java.util.Vector;

public class Mode extends Command
{

    private static final Settings settings = new Settings( true, true, 1, Integer.MAX_VALUE );

    /**
     * to add the list of ban masks in the list of messages
     * @param vCurrentMessages the current list of messages
     * @param user the user who sends the command
     * @param channel the asked channel
     */
    private void addBanList( Vector vCurrentMessages, org.codehaus.plexus.ircd.user.User user, Channel channel )
    {
        String[] sMasks = channel.getBanMasks();
        for ( int j = 0; j < sMasks.length; j++ )
        {
            vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_BANLIST, new String[]{channel.getName(), sMasks[j]} ) );
        }
        vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_ENDOFBANLIST, new String[]{channel.getName()} ) );
    }

    /**
     * to execute the Mode's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        MiddleToken[] middleTokens = getRequestMessage().getParamsToken().getMiddleTokens();
        NickToken nickToken = (NickToken) NickToken.getToken( middleTokens[0].toString() );
        if ( nickToken != null && middleTokens.length > 1 )
        {
            try
            {
                HandleUser.changeMode( user, nickToken.toString(), (ModeToken) ModeToken.getToken( middleTokens[1].toString() ) );
            }
            catch ( IRCException e )
            {
                return Message.createSingleMessage( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
            }
            return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_UMODEIS, new String[]{user.getMode()} ) );
        }
        else
        {
            ChannelToken channelToken = (ChannelToken) ChannelToken.getToken( middleTokens[0].toString() );
            if ( channelToken == null )
            {
                return null;
            }
            String channelName = channelToken.getChannelName();
            Channel channel = null;
            try
            {
                channel = HandleChannel.getChannel( channelName );
            }
            catch ( IRCException e )
            {
                return Message.createSingleMessage( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
            }
            if ( middleTokens.length == 1 )
            {
                return Message.createSingleMessage( new Message( user.getNickName(), Replies.RPL_CHANNELMODEIS, new String[]{channel.getName(), channel.displayModes()} ) );
            }
            else
            {
                ModeToken modeToken = (ModeToken) ModeToken.getToken( middleTokens[1].toString() );
                try
                {
                    HandleChannel.changeMode( user, channel.getName(), modeToken, middleTokens );
                    String sArgs = VOID;
                    for ( int i = 0; i < middleTokens.length; i++ )
                    {
                        sArgs += SPACE + middleTokens[i].toString();
                    }
                    channel.sendMessage( user.getUserFullName(), getName(), sArgs, SPACE );
                }
                catch ( IRCException e )
                {
                    Replies replies = e.getReplies();
                    if ( replies.getCode() == Replies.ERR_NEEDMOREPARAMS.getCode() && new Boolean( e.getParameters()[1] ).booleanValue() && e.getParameters()[2].equalsIgnoreCase( CHANNEL_BAN_MASK ) )
                    {
                        Vector vAllMessage = new Vector();
                        addBanList( vAllMessage, user, channel );
                        return Message.vector2Messages( vAllMessage );
                    }
                    else
                    {
                        return Message.createSingleMessage( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
                    }
                }
            }
        }
        return null;
    }

    /**
     * to get the settings of Mode's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}
