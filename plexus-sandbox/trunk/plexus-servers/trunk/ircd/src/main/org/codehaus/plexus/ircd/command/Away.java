/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.utils.Message;

import org.codehaus.plexus.ircd.user.User;

public class Away extends Command
{

    private static final Settings settings = new Settings();

    /**
     * to execute the Away's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        String sMessage = null;
        if ( paramsToken != null )
        {
            if ( paramsToken.getTrailingToken() != null )
            {
                sMessage = paramsToken.getTrailingToken().toString();
            }
            else if ( paramsToken.getMiddleTokens() != null && paramsToken.getMiddleTokens().length > 0 )
            {
                sMessage = paramsToken.getMiddleTokens()[0].toString();
            }
        }
        if ( user != null )
        {
            if ( sMessage == null )
            {
                try
                {
                    HandleUser.removeAwayState( user.getNickName() );
                }
                catch ( IRCException e )
                {
                }
            }
            else
            {
                try
                {
                    HandleUser.setAwayState( user.getNickName(), sMessage );
                }
                catch ( IRCException e )
                {
                }
            }
        }
        return null;
    }

    /**
     * to get the settings of Away's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

