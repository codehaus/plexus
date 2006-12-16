/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

/**
 * Deployer command.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public interface Command
{

    static final String ROLE = Command.class.getName();

    /**
     * Invokes the command.
     * 
     * @param context {@link CommandContext} that wraps the contextual 
     *        information that the executing {@link Command} instance can use 
     *        to query adapt its behaviour.
     * @return TODO
     * @throws CommandException TODO
     */
    public CommandResult execute( CommandContext context )
        throws CommandException;

}
