/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer.model;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public abstract class DeployerResource
{

    /**
     * SCM URL for the project
     */
    private String scmURL;

    /**
     * Label/Id for this Project instance.
     * <p>
     * This is used in workspace creation for the project.
     */
    private String label;

    /**
     * Username to use for logging in to SCM
     */
    private String scmUsername = "anonymous";

    /**
     * Password to use for logging in to SCM.
     */
    private String scmPassword = "";

    /**
     * Source control repository tag to be used for checking out a project.
     */
    private String scmTag = null;

    /**
     * @return the scmURL
     */
    public String getScmURL()
    {
        return scmURL;
    }

    /**
     * @param scmURL the scmURL to set
     */
    public void setScmURL( String scmURL )
    {
        this.scmURL = scmURL;
    }

    /**
     * @return the projectId
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setLabel( String projectId )
    {
        this.label = projectId;
    }

    /**
     * SCM Username to use for logging into Source Control
     * 
     * @return
     */
    public String getScmUsername()
    {
        // TODO Auto-generated method stub
        return this.scmUsername;
    }

    /**
     * SCM Password to user for logging into Source Control
     * 
     * @return
     */
    public String getScmPassword()
    {
        // TODO Auto-generated method stub
        return this.scmPassword;
    }

    /**
     * @param scmPassword the scmPassword to set
     */
    public void setScmPassword( String scmPassword )
    {
        this.scmPassword = scmPassword;
    }

    /**
     * @param scmUsername the scmUsername to set
     */
    public void setScmUsername( String scmUsername )
    {
        this.scmUsername = scmUsername;
    }

    /**
     * @return the scmTag
     */
    public String getScmTag()
    {
        return scmTag;
    }

    /**
     * @param scmTag the scmTag to set
     */
    public void setScmTag( String scmTag )
    {
        this.scmTag = scmTag;
    }

}
