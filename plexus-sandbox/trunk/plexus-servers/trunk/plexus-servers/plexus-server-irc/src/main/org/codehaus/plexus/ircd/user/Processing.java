/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.user;

import org.codehaus.plexus.ircd.command.Command;
import org.codehaus.plexus.ircd.command.CommandHandler;
import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Log;
import org.codehaus.plexus.ircd.utils.Message;

import java.util.Vector;
import java.util.logging.Level;

public class Processing extends Thread
{

    private User user;
    private Message[] allRequests;
    private Vector allResponses = new Vector();

    /**
     * The processing thread of a specific user. This thread treats the client's requests
     * @param allRequests all the requests to treat
     * @param user the sender of the requests
     */
    Processing( Message[] allRequests, User user )
    {
        this.allRequests = allRequests;
        this.user = user;
    }

    /**
     * to add messages to the list of messages to treat
     * @param messages the messages to treat
     */
    private void addResponses( Message[] messages )
    {
        if ( messages != null )
        {
            for ( int i = 0; i < messages.length; i++ )
            {
                Message message = messages[i];
                if ( message != null )
                {
                    allResponses.addElement( message );
                }
            }
        }
    }

    /**
     * to get the responses corresponding to the requests already treated
     */
    private Message[] getResponses()
    {
        Message[] result = new Message[allResponses.size()];
        return (Message[]) allResponses.toArray( result );
    }

    /**
     * to treat a single request
     * @param request the request
     */
    private void handleRequest( Message request ) throws FatalException
    {
        try
        {
            Command command = CommandHandler.getInstance( request );
            addResponses( command.execute( user ) );
        }
        catch ( IRCException e )
        {
            if ( user != null )
            {
                String sLogin = user.getNickName();
                if ( sLogin != null && !sLogin.equals( IRCString.VOID ) )
                {
                    addResponses( Message.createSingleMessage( new Message( sLogin, e.getReplies(), e.getParameters() ) ) );
                }
            }
            else
            {
                addResponses( Message.createSingleMessage( new Message( e.getReplies(), e.getParameters() ) ) );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new FatalException( e.getMessage() );
        }
    }

    /**
     * to treat several requests
     * @param requests the requests
     */
    private void handleRequest( Message[] requests ) throws FatalException
    {
        if ( requests != null )
        {
            for ( int i = 0; i < requests.length; i++ )
            {
                Message request = requests[i];
                if ( request != null && !request.isNull() )
                {
                    handleRequest( request );
                }
            }
        }
    }

    /**
     * the run's method of the thread
     */
    public void run()
    {
        Log.log( Level.INFO, "Processing", "run", "start run" );
        try
        {
            handleRequest( allRequests );
            user.addResponses( getResponses() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        Log.log( Level.INFO, "Processing", "run", "end run" );
    }
}

