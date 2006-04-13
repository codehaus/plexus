/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.channel.HandleChannel;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.ChannelToken;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.TargetToken;
import org.codehaus.plexus.ircd.token.ToToken;
import org.codehaus.plexus.ircd.utils.Message;

import java.util.Vector;

public class Part extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 2 );

    /**
     * to execute the Part's command
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
        Vector vMessage = new Vector();
        if ( toTokens != null )
        {
            for ( int i = 0; i < toTokens.length; i++ )
            {
                ChannelToken channelToken = toTokens[i].getChannelToken();
                if ( channelToken == null )
                {
                    continue;
                }
                String channelName = channelToken.getChannelName();
                try
                {
                    HandleChannel.removeUser( user, channelName, true, user.getUserFullName(), getName(), channelName, SPACE );
                }
                catch ( IRCException e )
                {
                    vMessage.addElement( new Message( user.getNickName(), e.getReplies(), e.getParameters() ) );
                }
            }
        }
        return Message.vector2Messages( vMessage );
    }

    /**
     * to get the settings of Part's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

