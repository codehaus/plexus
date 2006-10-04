package org.codehaus.plexus.xsiter.deployer;

import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployedProject;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Deployment Manager Role.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $$Id$$
 */
public interface Deployer
{
    /** 
     * The role associated with the component. 
     */
    public static final String ROLE = Deployer.class.getName();

    // workspace descriptor elements
    public static final String ELT_WORKING_DIRECTORY = "workingDirectory";

    public static final String ELT_WEBSERVER_DIRECTORY = "webserverDirectory";

    public static final String ELT_WEBAPP_DIRECTORY = "webappDirectory";

    public static final String ELT_TEMP_DIRECTORY = "tempDirectory";

    public static final String ELT_ROOT_DIRECTORY = "rootDirectory";

    public static final String ELT_SCM_URL = "scmURL";

    public static final String ELT_SCM_PASSWORD = "scmPassword";

    public static final String ELT_SCM_USERNAME = "scmUsername";

    public static final String ELT_ID = "id";

    public static final String ELT_WORKSPACE = "workspace";

    /**
     * Maven 2.x executable name that we expect to find to be able to build and
     * deploy projects.
     */
    public static final String MAVEN_EXECUTABLE = "mvn";

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
     * Checks out the specified project.
     * 
     * @param project Project to check out
     * @throws Exception
     */
    public void checkoutProject( DeployableProject project )
        throws Exception;

    /**
     * Updates an existing project.<p>
     *  
     * @param project
     * @throws Exception
     */
    public void updateProject( DeployableProject project )
        throws Exception;

    /**
     * Obtains and returns the deployment workspace for the project.
     * 
     * @param project
     * @return
     * @throws Exception if workspace not found for the specified project.
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
