/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

/**
 * Exception that is created and thrown from a 
 * {@link Command#execute(CommandContext)} operation.<p>
 * This should wrap an appropriate error message and populate the 
 * context with messages.
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class CommandException
    extends Throwable
{

    /**
     * 
     */
    public CommandException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public CommandException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * @param message
     */
    public CommandException( String message )
    {
        super( message );
    }

    /**
     * @param cause
     */
    public CommandException( Throwable cause )
    {
        super( cause );
    }

}
