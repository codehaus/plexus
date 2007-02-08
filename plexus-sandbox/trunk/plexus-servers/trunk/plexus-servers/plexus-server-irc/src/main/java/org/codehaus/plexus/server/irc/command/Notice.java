/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.server.irc.command;

import org.codehaus.plexus.server.irc.exception.IRCException;
import org.codehaus.plexus.server.irc.token.MiddleToken;
import org.codehaus.plexus.server.irc.token.NickToken;
import org.codehaus.plexus.server.irc.token.ParamsToken;
import org.codehaus.plexus.server.irc.token.TargetToken;
import org.codehaus.plexus.server.irc.token.ToToken;
import org.codehaus.plexus.server.irc.token.TrailingToken;
import org.codehaus.plexus.server.irc.user.HandleUser;
import org.codehaus.plexus.server.irc.utils.Message;
import org.codehaus.plexus.server.irc.utils.Replies;

public class Notice extends Command
{

    private static final Settings settings = new Settings( true, true, 1, 1 );

    /**
     * to execute the Notice's command
     * @param userExp the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.server.irc.user.User userExp )
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
                    NickToken nickToken = toTokens[i].getNickToken();
                    org.codehaus.plexus.server.irc.user.User user = null;
                    if ( nickToken == null )
                    {
                        continue;
                    }
                    String login = nickToken.getNickName();
                    try
                    {
                        user = HandleUser.getUser( login );
                    }
                    catch ( IRCException e )
                    {
                    }
                    if ( user != null )
                    {
                        user.addResponses( Message.createSingleMessage( new Message( userExp.getNickName(), user.getNickName(), getName(), VOID, sMessage ) ) );
                    }
                }
            }
            return null;
        }
    }

    /**
     * to get the settings of Notice's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}
