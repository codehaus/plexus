/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.token.ParamsToken;
import org.codehaus.plexus.ircd.user.User;
import org.codehaus.plexus.ircd.utils.Message;

public class Ping extends Command
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

