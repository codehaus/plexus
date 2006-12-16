/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import java.io.File;

import org.codehaus.plexus.xsiter.deployer.Deployer;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Command execution context. Wraps up the contextual information required 
 * by a {@link DeployerCommand} to execute and
 * do its stuff.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class CommandContext
{

    /**
     * {@link DeploymentWorkspace} that is the target for the
     *  {@link DeployerCommand}.
     */
    private DeploymentWorkspace workspace;

    /**
     * Absolute path to {@link Deployer}'s working directory where all the 
     * {@link DeploymentWorkspace}s are created.<p>
     * This can never be <code>null</code>.
     */
    private File deployerWorkingDirectory;

    /**
     * @return the {@link DeploymentWorkspace} instance that is the target
     *         for the {@link DeployerCommand}. Returns <code>null</code>
     *         if there is none.
     */
    public DeploymentWorkspace getWorkspace()
    {
        return workspace;
    }

    /**
     * Sets the the {@link DeploymentWorkspace} instance that is the target for
     * the {@link DeployerCommand}.
     * 
     * @param workspace
     *            the workspace to set
     */
    public void setWorkspace( DeploymentWorkspace workspace )
    {
        this.workspace = workspace;
    }

    /**
     * @return the deployerWorkingDirectory
     */
    public File getDeployerWorkingDirectory()
    {
        return deployerWorkingDirectory;
    }

    /**
     * @param deployerWorkingDirectory the deployerWorkingDirectory to set
     */
    public void setDeployerWorkingDirectory( File deployerWorkingDirectory )
    {
        this.deployerWorkingDirectory = deployerWorkingDirectory;
    }

}
