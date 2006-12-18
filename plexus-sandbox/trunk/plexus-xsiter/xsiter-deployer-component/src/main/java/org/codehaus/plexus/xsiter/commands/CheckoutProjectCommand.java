/**
 * 
 */
package org.codehaus.plexus.xsiter.commands;

import java.io.File;
import java.io.IOException;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xsiter.command.AbstractCommand;
import org.codehaus.plexus.xsiter.command.Command;
import org.codehaus.plexus.xsiter.command.CommandContext;
import org.codehaus.plexus.xsiter.command.CommandException;
import org.codehaus.plexus.xsiter.command.CommandResult;
import org.codehaus.plexus.xsiter.command.ResultState;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @plexus.component role="org.codehaus.plexus.xsiter.command.Command" role-hint="checkout"
 */
public class CheckoutProjectCommand
    extends AbstractCommand
    implements Command
{

    /**
     * Source Control Manager.
     * 
     * @plexus.requirement 
     */
    private ScmManager scmManager;

    /**
     * 
     */
    public CheckoutProjectCommand()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.xsiter.command.Command#execute(org.codehaus.plexus.xsiter.command.CommandContext)
     */
    public CommandResult execute( CommandContext context )
        throws CommandException
    {
        DeploymentWorkspace workspace = context.getWorkspace();

        if ( null == workspace.getScmURL() || workspace.getScmURL().trim().equals( "" ) )
            throw new CommandException( "Unable to perform checkout. No SCM URL found for workspace '"
                + workspace.getId() + "'" );

        CommandResult cmdResult = new CommandResult();

        try
        {
            ScmRepository repository = getScmRepository( workspace );
            ScmResult result;
            synchronized ( this )
            {
                File checkoutDir = DeployerUtils.getWorkspaceCheckoutDirectory( context.getDeployerWorkingDirectory(),
                                                                                context.getWorkspace() );

                try
                {
                    if ( checkoutDir.exists() )
                        FileUtils.cleanDirectory( checkoutDir );
                }
                catch ( IOException e )
                {
                    throw new CommandException( "Could not clean directory : " + workspace.getWorkingDirectory(), e );
                }

                // check out to a tagged directory
                String scmTag = null != workspace.getScmTag() ? workspace.getScmTag() : "HEAD";                
                ScmFileSet fileSet = new ScmFileSet( checkoutDir );
                result = scmManager.getProviderByRepository( repository ).checkOut( repository, fileSet,
                                                                                    workspace.getScmTag() );                
            }

            // TODO: Use logger
            System.out.println( "Command output: " + result.getCommandOutput() );
            System.out.println( "Provider message: " + result.getProviderMessage() );
            
            if ( !result.isSuccess() )
            {
                cmdResult.setState( ResultState.FAILURE );
                cmdResult.addMessage( "Command output: " + result.getCommandOutput() );
                cmdResult.addMessage( "Provider message: " + result.getProviderMessage() );
                return cmdResult;
            }
            else
            {
                // TODO: Use logger
                System.out.println( "Project checked out successfully." );
                cmdResult.setState( ResultState.SUCCESS );
                cmdResult.addMessage( "Command output: " + result.getCommandOutput() );
                cmdResult.addMessage( "Provider message: " + result.getProviderMessage() );
                return cmdResult;
            }

            // return result;
        }
        catch ( NoSuchScmProviderException e )
        {
            throw new CommandException( "Cannot checkout project for workspace '" + workspace.getId() + "'", e );
        }
        catch ( ScmException e )
        {
            throw new CommandException( "Cannot checkout project for workspace '" + workspace.getId() + "'", e );
        }

    }

    /**
     * Return an {@link ScmRepository} from the SCM URL.
     * 
     * @param project
     * @return
     * @throws ScmRepositoryException
     * @throws NoSuchScmProviderException
     */
    /**
     * @param workspace
     * @return
     * @throws ScmRepositoryException
     * @throws NoSuchScmProviderException
     */
    private ScmRepository getScmRepository( DeploymentWorkspace workspace )
        throws ScmRepositoryException, NoSuchScmProviderException
    {
        ScmRepository repository = scmManager.makeScmRepository( workspace.getScmURL().trim() );

        repository.getProviderRepository().setPersistCheckout( true );

        if ( !StringUtils.isEmpty( workspace.getScmUsername() ) )
        {
            repository.getProviderRepository().setUser( workspace.getScmUsername() );

            if ( !StringUtils.isEmpty( workspace.getScmPassword() ) )
            {
                repository.getProviderRepository().setPassword( workspace.getScmPassword() );
            }
            else
            {
                repository.getProviderRepository().setPassword( "" );
            }
        }

        return repository;
    }

}
