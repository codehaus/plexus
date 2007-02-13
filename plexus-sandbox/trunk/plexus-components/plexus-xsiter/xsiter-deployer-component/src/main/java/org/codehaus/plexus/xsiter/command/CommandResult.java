/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link CommandResult} instance is created and returned as a consequence of
 * {@link Command}'s execution.<p>
 * {@link CommandResult} has a {@link ResultState} which can be either of - 
 * {@link ResultState#SUCCESS}, {@link ResultState#FAILURE} or {@link ResultState#ERROR.<p>
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *  
 */
public class CommandResult
{

    /**
     * State of {@link CommandResult} set as a consequnce of 
     * {@link Command} execution. 
     */
    private ResultState state = ResultState.UNKNOWN;

    /**
     * List of messages setup on the result. 
     */
    private List messages = new ArrayList();

    /**
     * Result data created as a consequence of command execution.<p> 
     * This assumes that the client that invokes the command already
     * knows what type of result data to expect in return.
     */
    private Object data = null;

    /**
     * @return the state
     */
    public ResultState getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState( ResultState state )
    {
        this.state = state;
    }

    /**
     * @return the messages
     */
    public List getMessages()
    {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages( List messages )
    {
        this.messages = messages;
    }

    /**
     * Adds the specified message to the result.
     * @param msg
     */
    public void addMessage( String msg )
    {
        this.messages.add( msg );
    }

    /**
     * @return the data
     */
    public Object getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData( Object data )
    {
        this.data = data;
    }

}
