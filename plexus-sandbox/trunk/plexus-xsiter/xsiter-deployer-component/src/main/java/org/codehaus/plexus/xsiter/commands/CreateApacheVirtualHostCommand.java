/**
 * 
 */
package org.codehaus.plexus.xsiter.commands;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xsiter.command.AbstractCommand;
import org.codehaus.plexus.xsiter.command.Command;
import org.codehaus.plexus.xsiter.command.CommandContext;
import org.codehaus.plexus.xsiter.command.CommandException;
import org.codehaus.plexus.xsiter.command.CommandResult;
import org.codehaus.plexus.xsiter.command.ResultState;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.utils.DeployerUtils;
import org.codehaus.plexus.xsiter.vhost.VirtualHostConfiguration;
import org.codehaus.plexus.xsiter.vhost.VirtualHostManager;

/**
 * Creates an Apache Virtual Host configuration from a template provided for 
 * the specified {@link DeploymentWorkspace}
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @plexus.component role="org.codehaus.plexus.xsiter.command.Command" role-hint="create-vhost"
 */
public class CreateApacheVirtualHostCommand
    extends AbstractCommand
    implements Command
{

    /**
     * Source Control Manager.
     * 
     * @plexus.requirement
     */
    protected ScmManager scmManager;

    /**
     * Component to Manage Virtual Hosts for a Project.
     * 
     * @plexus.requirement
     */
    protected VirtualHostManager virtualHostManager;

    public CreateApacheVirtualHostCommand( String label )
    {
        super( label );
    }

    /**
     * 
     */
    public CreateApacheVirtualHostCommand()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.xsiter.command.Command#execute(org.codehaus.plexus.xsiter.command.CommandContext)
     */
    public CommandResult execute( CommandContext context )
        throws CommandException
    {
        CommandResult result = new CommandResult();
        try
        {
            // Updated Implementation, use configuration from pom.xml
            DeploymentWorkspace workspace = context.getWorkspace();
            MavenProject mavenProject = DeployerUtils.getMavenProjectForCheckedoutProject( context
                .getDeployerWorkingDirectory(), workspace );
            Properties props = mavenProject.getProperties();

            String vhostsConfig = (String) props.get( VirtualHostManager.ELT_POM_VHOSTS_CONFIG );
            StringReader sr = new StringReader( vhostsConfig );
            List list = virtualHostManager.loadVirtualHostConfigurations( sr );
            boolean appendVhost = false;
            for ( Iterator it = list.iterator(); it.hasNext(); )
            {
                VirtualHostConfiguration config = (VirtualHostConfiguration) it.next();
                File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
                String scmTag = null != workspace.getScmTag() ? workspace.getScmTag() : "HEAD";
                File checkoutDir = new File( workspaceWorkingDir, scmTag );
                // Adjust the Velocity 'file.resource.loader.path' property to the
                // checkout directory, so the Vhosts template could be found.
                Velocity.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, checkoutDir.getAbsolutePath() );
                // Adjust other directory paths on the VirtualHostConfiguration
                // instance
                resetVirtualHostDirectory( config, workspace );
                virtualHostManager.addVirtualHost( config, appendVhost );
                // subsequent vhost configs to be appended
                if ( !appendVhost )
                    appendVhost = true;
            }
            result.setState( ResultState.SUCCESS );
            return result;
        }
        catch ( Exception e )
        {
            result.setState( ResultState.ERROR );
            result.addMessage( "Unable to create Apache Virtual Host for Workspace: '" + context.getWorkspace().getId()
                + "'" );
            return result;
        }
    }

    /**
     * Service method to translate and adjust the directory paths for the Virtual Host configuration
     * to be relative from the Deployment workspaces ROOT dir.
     * <p>
     * Also deletes any existing vhost resources.
     * 
     * @param config
     * @param workspace
     */
    protected void resetVirtualHostDirectory( VirtualHostConfiguration config, DeploymentWorkspace workspace )
    {
        if ( config.getVhostDirectory().startsWith( "/" ) )
        {
            config.setVhostDirectory( StringUtils.replace( workspace.getRootDirectory() + config.getVhostDirectory(),
                                                           "\\", "/" ) );
        }
        else
        {
            config.setVhostDirectory( StringUtils.replace( workspace.getRootDirectory() + "/"
                + config.getVhostDirectory(), "\\", "/" ) );
        }
        if ( config.getVhostLogDirectory().startsWith( "/" ) )
        {
            config.setVhostLogDirectory( StringUtils.replace( workspace.getRootDirectory()
                + config.getVhostLogDirectory(), "\\", "/" ) );
        }
        else
        {
            config.setVhostLogDirectory( StringUtils.replace( workspace.getRootDirectory() + "/"
                + config.getVhostLogDirectory(), "\\", "/" ) );
        }
    }

}
