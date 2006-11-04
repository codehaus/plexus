/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.token.TrailingToken;
import org.codehaus.plexus.ircd.utils.Message;

public class Quit extends Command
{

    private static final Settings settings = new Settings();

    /**
     * to execute the Quit's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.ircd.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        String sMessage = VOID;
        if ( paramsToken != null )
        {
            TrailingToken trailing = paramsToken.getTrailingToken();
            if ( trailing != null )
            {
                sMessage = trailing.toString();
            }
        }
        try
        {
            user.disconnect( sMessage );
        }
        catch ( Exception e )
        {
        }
        return null;
    }

    /**
     * to get the settings of Quit's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}

