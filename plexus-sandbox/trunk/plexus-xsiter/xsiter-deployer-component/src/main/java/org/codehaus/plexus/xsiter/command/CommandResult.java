/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link CommandResult} instance is created and returned as a consequence of
 * {@link DeployerCommand}'s execution.<p>
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
     * {@link DeployerCommand} execution. 
     */
    private ResultState state = ResultState.FAILURE;

    /**
     * List of messages setup on the result. 
     */
    private List messages = new ArrayList();

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
    public void addmessage( String msg )
    {
        this.messages.add( msg );
    }

}
