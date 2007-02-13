/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

/**
 * A {@link CommandResult}'s possible states are setup here. 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public enum ResultState {

    UNKNOWN("Unknown"), SUCCESS("Success"), FAILURE("Failure"), ERROR("Error");

    /**
     * State label, for display.
     */
    private String label;

    ResultState( String s )
    {
        this.label = s;
    }

    /**
     * Returns the String representation for the state.
     * @return state label.
     */
    public String toString()
    {
        return this.label;
    }
}
