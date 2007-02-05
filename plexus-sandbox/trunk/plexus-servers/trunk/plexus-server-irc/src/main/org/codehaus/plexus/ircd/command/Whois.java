/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.NickToken;
import org.codehaus.plexus.ircd.token.TargetToken;
import org.codehaus.plexus.ircd.token.ToToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import java.util.Vector;

public class Whois extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 1 );

    /**
     * to execute the Whois's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        MiddleToken[] middleTokens = getRequestMessage().getParamsToken().getMiddleTokens();
        TargetToken targetToken = (TargetToken) TargetToken.getToken( middleTokens[0].toString() );
        if ( targetToken == null )
        {
            return null;
        }
        ToToken[] toTokens = targetToken.getToTokens();
        if ( toTokens != null )
        {
            Vector vMessage = new Vector();
            for ( int i = 0; i < toTokens.length; i++ )
            {
                ToToken currentToToken = toTokens[i];
                NickToken nickToken = currentToToken.getNickToken();
                if ( nickToken == null )
                {
                    continue;
                }
                org.codehaus.plexus.ircd.user.User userToIdentify = null;
                try
                {
                    userToIdentify = HandleUser.getUser( nickToken.toString() );
                }
                catch ( IRCException e )
                {
                    vMessage.addElement( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
                }
                if ( userToIdentify != null )
                {
                    identifyUser( vMessage, user, userToIdentify );
                }
            }
            return Message.vector2Messages( vMessage );
        }
        return null;
    }

    /**
     * to get the settings of Whois's command
     */
    protected Settings getSettings()
    {
        return settings;
    }

    /**
     * to add all known informations about the requested user
     * @param vCurrentMessages the list of current message
     * @param user the user who sends the command
     * @param userToIdentify the user to identify
     */
    private void identifyUser( Vector vCurrentMessages, org.codehaus.plexus.ircd.user.User user, org.codehaus.plexus.ircd.user.User userToIdentify )
    {
        vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_WHOISUSER, new String[]{userToIdentify.getNickName(), userToIdentify.getUserName(), userToIdentify.getHostName(), userToIdentify.getRealName()} ) );
        String[] sChannels = userToIdentify.getChannelList( user.getNickName() );
        for ( int j = 0; j < sChannels.length; j++ )
        {
            vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_WHOISCHANNELS, new String[]{userToIdentify.getNickName(), sChannels[j]} ) );
        }
        if ( user.isAway() )
        {
            vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_AWAY, new String[]{userToIdentify.getNickName(), userToIdentify.getAwayMessage()} ) );
        }
        vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_WHOISIDLE, new String[]{userToIdentify.getNickName(), Long.toString( userToIdentify.getIdle() )} ) );
        vCurrentMessages.addElement( new Message( user.getNickName(), Replies.RPL_ENDOFWHOIS, new String[]{userToIdentify.getNickName()} ) );
    }
}

