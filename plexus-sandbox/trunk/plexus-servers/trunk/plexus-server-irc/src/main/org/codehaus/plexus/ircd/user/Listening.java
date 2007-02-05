/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.user;

import org.codehaus.plexus.ircd.utils.Log;
import org.codehaus.plexus.ircd.utils.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class Listening
    extends Thread
{

    private User user;
    private int state;
    private long startIdleTime;
    private boolean disconnected;
    private InputStream inputStream;

    /**
     * The listening thread of a specific user. This thread waits for the client's request
     * @param inputStream stream corresponding to the connection with the client
     * @param user the connected user
     */
    Listening( InputStream inputStream, User user )
    {
        this.inputStream = inputStream;
        this.user = user;
        setIdleTime();
    }

    /**
     * to add requests to the list of messages to treat
     */
    private void addRequest() throws IOException
    {
        byte[] receive = receive();
        if ( receive != null )
        {
            Message[] messages = Message.parse( new String( receive ), getNextState() );
            Processing processing = new Processing( messages, user );
            processing.start();
            state = messages[messages.length - 1].getState();
            setIdleTime();
        }
    }

    /**
     * to disconnect the user's connection
     */
    void disconnect()
    {
        setDisconnected( true );
    }

    /**
     * to know the idle's time of the client
     */
    public long getIdle()
    {
        return (int) ( ( System.currentTimeMillis() - startIdleTime ) / 1000 );
    }

    /**
     * to get the state of connection
     */
    public synchronized int getNextState()
    {
        int iLastState = 3;
        if ( state < iLastState - 1 )
        {
            state++;
        }
        else
        {
            state = iLastState;
        }
        return state;
    }

    /**
     * to know if the user is already disconnected
     */
    public boolean isDisconnected()
    {
        return disconnected;
    }

    /**
     * to get the bytes send by the user through the connection
     */
    private byte[] receive() throws IOException
    {
        int nbr = inputStream.available();
        if ( nbr == 0 )
        {
            return null;
        }
        byte[] buffer = new byte[nbr];
        try
        {
            inputStream.read( buffer );

            Log.log( Level.INFO, "Listening", "receive", "<----" + new String( buffer ) );

            //Command command = CommandHandler.getInstance();

            System.out.println( "here we need to look up the command and execute it." );
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
        return buffer;
    }

    /**
     * the run's method of the thread
     */
    public void run()
    {
        Log.log( Level.INFO, "Listening", "run", "start run" );
        while ( !isDisconnected() )
        {
            try
            {
                sleep( 50 );
                addRequest();
            }
            catch ( IOException e )
            {
            }
            catch ( Exception e )
            {
                Log.log( Level.WARNING, "Listening", "run", "", e );
            }
        }
        try
        {
            inputStream.close();
        }
        catch ( IOException e )
        {
            Log.log( Level.WARNING, "Listening", "run", "", e );
        }
        Log.log( Level.INFO, "Listening", "run", "end run" );
    }

    /**
     * to change the thread's status to disconnect
     */
    private void setDisconnected( boolean disconnected )
    {
        this.disconnected = disconnected;
    }

    /**
     * to set the idle's time
     */
    public void setIdleTime()
    {
        startIdleTime = System.currentTimeMillis();
    }
}

