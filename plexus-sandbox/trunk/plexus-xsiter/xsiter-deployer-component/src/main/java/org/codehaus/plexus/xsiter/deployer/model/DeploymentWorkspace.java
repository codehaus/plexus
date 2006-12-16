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
    extends DeployerResource
{

    /**
     * Root directory for the workspace.
     * <p>
     * Absolute path to the Deployment workspace. All other directories are
     * resolved relative to this.
     */
    private String rootDirectory = getId();

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
     * @return the rootDir
     * @deprecated <em>Marked for removal.</em>
     */
    public String getRootDirectory()
    {
        return rootDirectory;
    }

    /**
     * @param rootDir the rootDir to set
     * @deprecated <em>Marked for removal.</em>
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
     * @return the label
     */
    public String getId()
    {
        return super.getLabel();
    }

    /**
     * @param label the label to set
     */
    public void setid( String id )
    {
        super.setLabel( id );
    }

}
