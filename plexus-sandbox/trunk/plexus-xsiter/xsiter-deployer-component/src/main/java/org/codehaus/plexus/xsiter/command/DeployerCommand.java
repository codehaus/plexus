/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import org.codehaus.plexus.command.Command;

/**
 * Deployer command.
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public interface DeployerCommand
    extends Command
{

    static final String ROLE = DeployerCommand.class.getName();

    /**
     * Sets the command's execution context.
     * 
     * @param context execution for the command.
     */
    public void setContext( CommandContext context );

}
