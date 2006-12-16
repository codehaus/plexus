/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import java.io.File;

import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;

/**
 * Creates a new {@link DeploymentWorkspace}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class CreateDeploymentWorkspaceCommand
    extends AbstractDeployerCommand
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
        DeployerUtils.persistWorkspaceDescriptor( workspace );

        CommandResult result = new CommandResult();
        result.setState( ResultState.SUCCESS );
        result.addmessage( "Workspace created for Id: " + workspace.getId() );
        return result;
    }
}
