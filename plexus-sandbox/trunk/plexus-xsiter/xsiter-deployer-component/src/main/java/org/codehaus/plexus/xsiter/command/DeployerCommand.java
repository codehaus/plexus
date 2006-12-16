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
public interface DeployerCommand
{

    static final String ROLE = DeployerCommand.class.getName();

    /**
     * Invokes the command.
     * 
     * @param context {@link CommandContext} that wraps the contextual 
     *        information that the executing {@link Command} instance can use 
     *        to query adapt its behaviour.
     * @throws CommandException TODO
     */
    public void execute( CommandContext context )
        throws CommandException;

}
