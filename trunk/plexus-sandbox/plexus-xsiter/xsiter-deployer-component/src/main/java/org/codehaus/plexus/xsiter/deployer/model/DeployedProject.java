/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer.model;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 3:59:42 PM
 */
public class DeployedProject
{

    /**
     * String Id, usually the composite of groupId, artifactId and version
     * sourced from the pom.xml
     */
    private String id;

    /**
     * Workspace that this instance is currently deployed to.
     */
    private DeploymentWorkspace workspace;

    /**
     * Project that is deployed to the deployment workspace
     */
    private DeployableProject project;

    private String state = "UNKNOWN";

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return the project
     */
    public DeployableProject getProject()
    {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject( DeployableProject project )
    {
        this.project = project;
    }

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

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState( String state )
    {
        this.state = state;
    }

}
