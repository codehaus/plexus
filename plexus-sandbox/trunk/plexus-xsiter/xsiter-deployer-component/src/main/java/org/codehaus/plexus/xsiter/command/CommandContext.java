/**
 * 
 */
package org.codehaus.plexus.xsiter.command;

import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Command execution context. Wraps up the contextual information required 
 * by a {@link DeployerCommand} to execute and do its stuff.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class CommandContext
{

    /**
     * {@link DeploymentWorkspace} that is the target for execution of a 
     * {@link DeployerCommand}.
     */
    private DeploymentWorkspace workspace;

    /**
     * @return the workspace
     */
    public DeploymentWorkspace getWorkspace()
    {
        return workspace;
    }

    /**
     * @param workspace the workspace to set
     */
    public void setWorkspace( DeploymentWorkspace workspace )
    {
        this.workspace = workspace;
    }

}
