package org.codehaus.plexus.xsiter.deployer;

import java.util.Properties;

import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployedProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Deploymen Manager Role.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $$Id$$
 */
public interface DeploymentManager
{

    /** The role associated with the component. */
    String ROLE = DeploymentManager.class.getName();

    /**
     * Deploys a Project to associated Deployment Workspace using the default
     * goal(s).
     * <p>
     * This will create a deployment workspace if one does not exists.
     * 
     * @param project project to deploy.
     * @return an instance of {@link DeployedProject}.
     * @throws Exception
     */
    public DeployedProject deployProject( DeployableProject project )
        throws Exception;

    /**
     * Deploys a Project to the associated Appserver in the Deployment Workspace
     * using the specified goal(s).
     * 
     * @param project
     * @param goals
     * @return
     * @throws Exception
     */
    public DeployedProject deployProject( DeployableProject project, String goals )
        throws Exception;

    /**
     * Delegates the specified goal(s) on the checked out project.
     * 
     * @param project
     * @param goals
     * @throws Exception
     */
    public void buildProject( DeployableProject project, String goals )
        throws Exception;

    /**
     * Sets up a Deployment Workspace by creating a {@link DeployableProject}
     * instance from the passed in properties and calling
     * {@link #addProject(DeployableProject)}.
     * 
     * @param properties
     * @throws Exception
     */
    public void addProject( Properties properties )
        throws Exception;

    /**
     * Sets up a Deployment Workspace for the specified Project
     * 
     * @param project project to setup a Deployment Workspace for.
     * @throws Exception if a Duplicate workspace exists
     */
    public void addProject( DeployableProject project )
        throws Exception;

    /**
     * Deletes a Deployment Workspace for the specified Project
     * 
     * @param project Project whose deployment workspace is to be removed.
     * @throws Exception if workspace could be deleted (is locked)
     */
    public void removeProject( DeployableProject project )
        throws Exception;

    /**
     * Checks out, or, updates an existing checkout for the specified project.
     * 
     * @param project Project to check out
     * @throws Exception
     */
    public void checkoutProject( DeployableProject project )
        throws Exception;

    /**
     * Obtains and returns the deployment workspace for the project.
     * 
     * @param project
     * @return
     * @throws Exception if workpace not found for the specified project.
     */
    public DeploymentWorkspace getDeploymentWorkspace( DeployableProject project )
        throws Exception;

    /**
     * Sets up a Virtual Host for a given Project with the specified
     * configuration.
     * 
     * @param project
     * @throws Exception
     */
    public void addVirtualHost( DeployableProject project )
        throws Exception;
}
