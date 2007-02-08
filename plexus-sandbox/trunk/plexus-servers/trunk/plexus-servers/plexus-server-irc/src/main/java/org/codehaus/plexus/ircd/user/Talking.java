/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.user;

import org.codehaus.plexus.ircd.utils.Log;
import org.codehaus.plexus.ircd.utils.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Level;

public class Talking extends Thread
{

    private User user;
    private boolean disconnected;
    private Vector allResponses;
    private OutputStream outputStream;

    /**
     * The talking thread of a specific user. This thread answers to the client
     * @param outputStream stream corresponding to the connection with the client
     * @param user the connected user
     */
    Talking( OutputStream outputStream, User user )
    {
        this.user = user;
        this.allResponses = new Vector();
        this.outputStream = outputStream;
    }

    /**
     * to add responses to the list of response's messages to send back to the client
     * @param messages the response to add
     */
    public synchronized void addResponses( Message[] messages )
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
        notify();
    }

    /**
     * to disconnect the user's connection
     */
    void disconnect()
    {
        setDisconnected( true );
    }

    /**
     * to know if the user is already disconnected
     */
    public boolean isDisconnected()
    {
        return disconnected;
    }

    /**
     * to notify the talking's thread that it has responses to send
     */
    synchronized void notifyTalking()
    {
        addResponses( null );
    }

    /**
     * the run's method of the thread
     */
    public void run()
    {
        Log.log( Level.INFO, "Talking", "run", "start run" );
        while ( !isDisconnected() )
        {
            try
            {
                sendAll();
            }
            catch ( Exception e )
            {
                Log.log( Level.WARNING, "Talking", "run", "", e );
            }
        }
        try
        {
            outputStream.close();
        }
        catch ( IOException e )
        {
            Log.log( Level.WARNING, "Talking", "run", "", e );
        }
        Log.log( Level.INFO, "Talking", "run", "end run" );
    }

    /**
     * to send back a message to the client
     * @param message the message to send
     */
    private void send( Message message ) throws IOException
    {
        send( message.toString() );
    }

    /**
     * to send back a specific content to the client
     * @param sMessage the content of the response to send to the client
     */
    private synchronized void send( String sMessage ) throws IOException
    {
        try
        {
            Log.log( Level.INFO, "Talking", "send", "------>" + sMessage );
            outputStream.write( sMessage.getBytes() );
        }
        catch ( java.net.SocketException e )
        {
            try
            {
                user.disconnect();
            }
            catch ( Exception ex )
            {
            }
        }
    }

    /**
     * to send all the buffered responses
     */
    private synchronized void sendAll() throws IOException, InterruptedException
    {
        waitResponse();
        int size = allResponses.size();
        for ( int i = 0; i < size; i++ )
        {
            Object object = allResponses.firstElement();
            send( (Message) object );
            allResponses.removeElementAt( 0 );
        }
    }

    /**
     * to change the thread's status to disconnect
     */
    private void setDisconnected( boolean disconnected )
    {
        this.disconnected = disconnected;
    }

    /**
     * to wait for a response to send
     */
    private void waitResponse() throws InterruptedException
    {
        Log.log( Level.INFO, "Talking", "waitResponse", "start waiting" );
        wait();
        Log.log( Level.INFO, "Talking", "waitResponse", "end waiting" );
    }
}

