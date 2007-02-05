/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.properties.Config;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

import org.codehaus.plexus.ircd.user.User;

public abstract class Command implements IRCString
{

    private static final String packageName = Config.getValue( "command.package.name", "org.codehaus.plexus.ircd.command" );

    private Message requestMessage;

    /**
     * to execute a specific command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    protected abstract Message[] exec( User user );

    /**
     * to execute a specific command with the given settings
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] execute( User user )
    {
        Settings commandSettings = getSettings();
        if ( !commandSettings.isParamNeeded() )
        {
            return exec( user );
        }
        MiddleToken[] middleTokens = null;
        if ( requestMessage.getParamsToken() != null )
        {
            middleTokens = requestMessage.getParamsToken().getMiddleTokens();
        }
        else if ( commandSettings.isMiddleTokenNeeded() )
        {
            return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
        }
        if ( !commandSettings.isMiddleTokenNeeded() )
        {
            return exec( user );
        }
        if ( middleTokens != null )
        {
            if ( middleTokens.length < commandSettings.getMinimumMiddleTokenNeeded() )
            {
                return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
            }
            if ( middleTokens.length > commandSettings.getMaximumMiddleTokenNeeded() )
            {
                return null;
            }
        }
        else
        {
            return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
        }
        return exec( user );
    }

    /**
     * to get the name of the command
     */
    public String getName()
    {
        return getClass().getName().substring( getPackageName().length() + 1 ).toUpperCase();
    }

    /**
     * to get the package's name of the command's module
     */
    protected static String getPackageName()
    {
        return packageName;
    }

    /**
     * to get the request message
     */
    protected Message getRequestMessage()
    {
        return requestMessage;
    }

    /**
     * to get the settings of the command
     */
    protected abstract Settings getSettings();

    /**
     * to set the request message
     */
    protected void setRequestMessage( Message requestMessage )
    {
        this.requestMessage = requestMessage;
    }
}

