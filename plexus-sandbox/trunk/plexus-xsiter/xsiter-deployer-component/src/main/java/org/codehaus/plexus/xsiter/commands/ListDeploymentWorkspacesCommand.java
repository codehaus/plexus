/**
 * 
 */
package org.codehaus.plexus.xsiter.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.xsiter.command.AbstractCommand;
import org.codehaus.plexus.xsiter.command.Command;
import org.codehaus.plexus.xsiter.command.CommandContext;
import org.codehaus.plexus.xsiter.command.CommandException;
import org.codehaus.plexus.xsiter.command.CommandResult;
import org.codehaus.plexus.xsiter.command.ResultState;
import org.codehaus.plexus.xsiter.deployer.Deployer;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;

/**
 * When executes it returns a list of all the workspaces under the 
 * {@link Deployer}'s working directory.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @plexus.component role="org.codehaus.plexus.xsiter.command.Command" role-hint="list"
 */
public class ListDeploymentWorkspacesCommand
    extends AbstractCommand
    implements Command
{

    public ListDeploymentWorkspacesCommand( String label )
    {
        super( label );
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.xsiter.command.Command#execute(org.codehaus.plexus.xsiter.command.CommandContext)
     */
    public CommandResult execute( CommandContext context )
        throws CommandException
    {
        File deployerWorkingDirectory = context.getDeployerWorkingDirectory();
        File[] files = deployerWorkingDirectory.listFiles();

        List list = new ArrayList();
        CommandResult result = new CommandResult();
        // a directory is a deployment workspace if it has a 
        // workspace descriptor.
        for ( int i = 0; i < files.length; i++ )
        {
            File workspaceXml = new File( files[i], "workspace.xml" );
            if ( workspaceXml.exists() )
            {
                try
                {
                    DeploymentWorkspace workspace = DeployerUtils.loadWorkspaceFromDescriptor( workspaceXml );
                    list.add( workspace );
                    System.out.println( "workspace added!" );
                }
                catch ( Exception e )
                {
                    // TODO: Log error and continue adding others to the list.
                    e.printStackTrace();
                    result.addMessage( "Error obtaining workspace information from workspace dir '"
                        + files[i].getAbsolutePath() );
                    result.setState( ResultState.ERROR );

                }
            }
        }

        // check if a result status was already set 
        if ( result.getState() == ResultState.UNKNOWN )
            result.setState( ResultState.SUCCESS );

        result.addMessage( "Found " + list.size() + " deployment workspaces." );
        result.setData( list );
        return result;
    }

}
