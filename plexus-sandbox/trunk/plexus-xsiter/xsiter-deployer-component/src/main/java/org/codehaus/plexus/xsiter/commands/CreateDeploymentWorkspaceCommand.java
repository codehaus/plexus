/**
 * 
 */
package org.codehaus.plexus.xsiter.commands;

import java.io.File;

import org.codehaus.plexus.xsiter.command.AbstractCommand;
import org.codehaus.plexus.xsiter.command.Command;
import org.codehaus.plexus.xsiter.command.CommandContext;
import org.codehaus.plexus.xsiter.command.CommandException;
import org.codehaus.plexus.xsiter.command.CommandResult;
import org.codehaus.plexus.xsiter.command.ResultState;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;

/**
 * Creates a new {@link DeploymentWorkspace}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @plexus.component role="org.codehaus.plexus.xsiter.command.Command" role-hint="create-workspace"
 */
public class CreateDeploymentWorkspaceCommand
    extends AbstractCommand
    implements Command
{

    /**
     * {@inheritDoc} 
     */
    public CreateDeploymentWorkspaceCommand( String label )
    {
        super( label );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.xsiter.command.Command#execute(org.codehaus.plexus.xsiter.command.CommandContext)
     */
    public CommandResult execute( CommandContext context )
        throws CommandException
    {
        DeploymentWorkspace workspace = context.getWorkspace();
        File workingDirectory = context.getDeployerWorkingDirectory();
        File rootDir = new File( workingDirectory, workspace.getId() );
        DeployerUtils.createIfNonExistent( rootDir );
        DeployerUtils.createIfNonExistent( new File( rootDir, workspace.getTempDirectory() ) );
        DeployerUtils.createIfNonExistent( new File( rootDir, workspace.getWebappDirectory() ) );
        DeployerUtils.createIfNonExistent( new File( rootDir, workspace.getWebserverDirectory() ) );
        DeployerUtils.createIfNonExistent( new File( rootDir, workspace.getWorkingDirectory() ) );
        DeployerUtils.persistWorkspaceDescriptor( workingDirectory, workspace );

        CommandResult result = new CommandResult();
        result.setState( ResultState.SUCCESS );
        result.addMessage( "Workspace created for Id: " + workspace.getId() );
        return result;
    }
}
