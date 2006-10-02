/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer.model;

/**
 * Deployment Workspace wraps up the requisite set of resources required to
 * setup a Project for Deployment.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 3:54:43 PM
 */
public class DeploymentWorkspace
{

    /**
     * Root directory for the workspace.
     * <p>
     * Absolute path to the Deployment workspace. All other directories are
     * resolved relative to this.
     */
    private String rootDirectory = null;

    /**
     * Webapp deployment directory for the workspace.
     */
    private String webappDirectory = "deploy/webapp";

    /**
     * Directory where webserver config and log files are stored.
     */
    private String webserverDirectory = "deploy/apache";

    /**
     * Temporary directory
     */
    private String tempDirectory = "tmp";

    /**
     * Working directory under the workspace for checkouts.
     */
    private String workingDirectory = "working";

    /**
     * SCM URL of the project that this workspace was created for.
     */
    private String scmURL = null;

    /**
     * SCM Username to use to log in to the Source control repo.
     */
    private String scmUsername = null;

    /**
     * SCM Passwor 
     */
    private String scmPassword = null;

    /**
     * Workspace Id, same as label of the project.
     */
    private String id = null;

    /**
     * @return the rootDir
     */
    public String getRootDirectory()
    {
        return rootDirectory;
    }

    /**
     * @param rootDir the rootDir to set
     */
    public void setRootDirectory( String rootDir )
    {
        this.rootDirectory = rootDir;
    }

    /**
     * @return the tmpDir
     */
    public String getTempDirectory()
    {
        return tempDirectory;
    }

    /**
     * @param tmpDir the tmpDir to set
     */
    public void setTempDirectory( String tmpDir )
    {
        this.tempDirectory = tmpDir;
    }

    /**
     * @return the webappDir
     */
    public String getWebappDirectory()
    {
        return webappDirectory;
    }

    /**
     * @param webappDir the webappDir to set
     */
    public void setWebappDirectory( String webappDir )
    {
        this.webappDirectory = webappDir;
    }

    /**
     * @return the webserverDir
     */
    public String getWebserverDirectory()
    {
        return webserverDirectory;
    }

    /**
     * @param webserverDir the webserverDir to set
     */
    public void setWebserverDirectory( String webserverDir )
    {
        this.webserverDirectory = webserverDir;
    }

    /**
     * @return the workingDirectoy
     */
    public String getWorkingDirectory()
    {
        return workingDirectory;
    }

    /**
     * @param workingDirectoy the workingDirectoy to set
     */
    public void setWorkingDirectory( String workingDirectoy )
    {
        this.workingDirectory = workingDirectoy;
    }

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
     * @return the scmPassword
     */
    public String getScmPassword()
    {
        return scmPassword;
    }

    /**
     * @param scmPassword the scmPassword to set
     */
    public void setScmPassword( String scmPassword )
    {
        this.scmPassword = scmPassword;
    }

    /**
     * @return the scmUsername
     */
    public String getScmUsername()
    {
        return scmUsername;
    }

    /**
     * @param scmUsername the scmUsername to set
     */
    public void setScmUsername( String scmUsername )
    {
        this.scmUsername = scmUsername;
    }

}
