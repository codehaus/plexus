/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.command;

import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.CommandToken;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;

public class CommandHandler implements IRCString
{

    /**
     * to get a command from the request's message
     * @param message the request's message
     * @return the asked command
     * @throws IRCException ERR_UNKNOWNCOMMAND if the command is unknown
     * @throws FatalException if an undesirable exception has been thrown
     */
    public static Command getInstance( Message message ) throws IRCException, FatalException
    {
        String command = VOID;

        try
        {
            CommandToken commandToken = message.getCommandToken();

            command = commandToken.getLetterValue();

            command = command.substring( 0, 1 ).toUpperCase() + command.substring( 1, command.length() ).toLowerCase();

            String className = Command.getPackageName() + DOT + command;

            System.out.println( "className = " + className );

            //!!!!

            if ( className.endsWith( "Who" ) )
            {
                className += "is";
            }

            Class commandClass = Class.forName( className );

            Command commandRequested = (Command) commandClass.newInstance();

            commandRequested.setRequestMessage( message );

            return commandRequested;
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();

            throw new IRCException( Replies.ERR_UNKNOWNCOMMAND, new String[]{command} );
        }
        catch ( Exception e )
        {
            e.printStackTrace();

            throw new FatalException( e.getMessage() );
        }
    }
}

