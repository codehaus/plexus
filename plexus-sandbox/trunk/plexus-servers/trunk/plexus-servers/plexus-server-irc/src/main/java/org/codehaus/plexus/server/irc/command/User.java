/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.server.irc.command;

import org.codehaus.plexus.server.irc.properties.Config;
import org.codehaus.plexus.server.irc.token.MiddleToken;
import org.codehaus.plexus.server.irc.token.ParamsToken;
import org.codehaus.plexus.server.irc.token.TrailingToken;
import org.codehaus.plexus.server.irc.utils.Message;
import org.codehaus.plexus.server.irc.utils.Replies;
import org.codehaus.plexus.server.irc.utils.Utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class User extends Command
{

    private static final Settings settings = new Settings( true, true, 3, 3 );

    /**
     * to add the content of the host file in the list of message
     * @param allMessage the list of current message
     * @param user the user who sends the command
     */
    private void displayHostFile( Vector allMessage, org.codehaus.plexus.server.irc.user.User user )
    {
        try
        {
            FileReader hostFileReader =
                new FileReader( System.getProperty( "org.server.path" ) +
                                SLASH + Config.getValue( "command.user.host.file.path", "conf/HostFile.txt" ) );

            BufferedReader in = new BufferedReader( hostFileReader );
            String buffer;
            while ( ( buffer = in.readLine() ) != null )
            {
                allMessage.addElement( new Message( user.getNoNullNickName(), Replies.RPL_MOTD, new String[]{buffer} ) );
            }
        }
        catch ( IOException e )
        {
            allMessage.addElement( new Message( user.getNickName(), Replies.ERR_NOMOTD, new String[]{} ) );
        }
    }

    /**
     * to execute the User's command
     * @param user the user who sends the command
     * @return the list of message to send back to the sender
     */
    public Message[] exec( org.codehaus.plexus.server.irc.user.User user )
    {
        ParamsToken paramsToken = getRequestMessage().getParamsToken();
        MiddleToken[] middleTokens = paramsToken.getMiddleTokens();
        TrailingToken trailingToken = paramsToken.getTrailingToken();

        if ( trailingToken == null )
        {
            trailingToken = new TrailingToken( "Fake name" );
            //return Message.createSingleMessage( new Message( Replies.ERR_NEEDMOREPARAMS, new String[]{getName()} ) );
        }

        if ( user != null && user.getUserName() == null )
        {
            user.setUserName( middleTokens[0].toString() );
            user.setHostName( user.getHost() );
            //!!!
            user.setServerName( "localhost" );
            user.setRealName( trailingToken.toString() );
            Vector vMessage = new Vector();
            vMessage.addElement( new Message( user.getNoNullNickName(), Replies.RPL_MOTDSTART, new String[]{"localhost"} ) );
            vMessage.addElement( new Message( user.getNoNullNickName(), Replies.RPL_MOTD, new String[]{Utilities.getMessage( Config.getValue( "command.user.host.message.syntax" ), new String[]{user.getUserFullName()} )} ) );
            displayHostFile( vMessage, user );
            vMessage.addElement( new Message( user.getNoNullNickName(), Replies.RPL_ENDOFMOTD, new String[]{} ) );
            return Message.vector2Messages( vMessage );
        }
        return null;
    }

    /**
     * to get the settings of User's command
     */
    protected Settings getSettings()
    {
        return settings;
    }
}
