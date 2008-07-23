/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.server.irc.command;

import org.codehaus.plexus.server.irc.exception.FatalException;
import org.codehaus.plexus.server.irc.exception.IRCException;
import org.codehaus.plexus.server.irc.properties.Config;
import org.codehaus.plexus.server.irc.token.MiddleToken;
import org.codehaus.plexus.server.irc.token.NickToken;
import org.codehaus.plexus.server.irc.token.ParamsToken;
import org.codehaus.plexus.server.irc.token.TrailingToken;
import org.codehaus.plexus.server.irc.user.HandleUser;
import org.codehaus.plexus.server.irc.utils.Message;
import org.codehaus.plexus.server.irc.utils.Replies;
import org.codehaus.plexus.server.irc.utils.Utilities;

public class Nick extends Command
{

    private static final Settings settings = new Settings( true, false, 1, Integer.MAX_VALUE );

    /**
     * to execute the Nick's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.server.irc.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        MiddleToken[] middleTokens = paramsToken.getMiddleTokens();
        TrailingToken trailingToken = paramsToken.getTrailingToken();
        if ( ( middleTokens == null || middleTokens.length == 0 ) && ( trailingToken == null ) )
        {
            return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
        }
        else
        {
            String sLogin = null;
            if ( middleTokens != null )
            {
                sLogin = middleTokens[0].toString();
            }
            else
            {
                sLogin = trailingToken.toString();
            }
            String sOldLogin = user.getNickName();
            NickToken nickToken = (NickToken) NickToken.getToken( sLogin );
            if ( nickToken == null )
            {
                return null;
            }
            sLogin = nickToken.getNickName();
            if ( sLogin.equals( VOID ) )
            {
                return null;
            }
            if ( sOldLogin == null || sOldLogin.equals( VOID ) )
            {
                sOldLogin = VOID;
                user.setNickName( sLogin );
                try
                {
                    HandleUser.udateUser( user );
                }
                catch ( IRCException e )
                {
                    user.setNickName( sOldLogin );
                    return Message.createSingleMessage( new Message( user.getNoNullNickName(), e.getReplies(), e.getParameters() ) );
                }
                catch ( FatalException e )
                {
                    user.setNickName( sOldLogin );
                    return null;
                }
            }
            else
            {
                try
                {
                    HandleUser.renameUser( sLogin, sOldLogin );
                }
                catch ( IRCException e )
                {
                    user.setNickName( sOldLogin );
                    return Message.createSingleMessage( new Message( user.getNoNullNickName(), e.getReplies(), e.getParameters() ) );
                }
            }
            return Message.createSingleMessage( new Message( user.getNoNullNickName(), new Notice().getName(), VOID, Utilities.getMessage( Config.getValue( "command.nick.change.message.syntax" ), new String[]{user.getNoNullNickName()} ) ) );
        }
    }

    /**
     * to get the settings of Nick's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

