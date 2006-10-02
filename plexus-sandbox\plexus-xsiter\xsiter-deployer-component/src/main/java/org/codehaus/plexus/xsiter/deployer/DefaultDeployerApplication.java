/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer;

import java.util.Properties;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;

/**
 * Facade to the underlying Deployer components.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 22/09/2006, 11:01:53 AM
 * @plexus.component
 */
public class DefaultDeployerApplication
    implements DeployerApplication
{

    /**
     * Deployment Manager to use to delegate calls to.
     * 
     * @plexus.requirement
     */
    private DeploymentManager deploymentManager;

    /**
     * Prompter for interactivity.
     * 
     * @plexus.requirement
     */
    private Prompter prompter;

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#addProject(java.util.Properties)
     */
    public void addProject( Properties props )
        throws Exception
    {
        deploymentManager.addProject( props );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#addVirtualHost(java.lang.String)
     */
    public void addVirtualHost( String pid )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        deploymentManager.addVirtualHost( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#checkoutProject(java.lang.String)
     */
    public void checkoutProject( String pid )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        deploymentManager.checkoutProject( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#deployProject(java.lang.String)
     */
    public void deployProject( String pid )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        String tag = prompter.prompt( "Enter SCM tag" );
        if ( null == tag || tag.trim().equals( "" ) )
            tag = "HEAD";
        project.setScmTag( tag );
        deploymentManager.deployProject( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#deployProject(java.lang.String,
     *      java.lang.String)
     */
    public void deployProject( String pid, String goals )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        String tag = prompter.prompt( "Enter SCM tag" );
        if ( null == tag || tag.trim().equals( "" ) )
            tag = "HEAD";
        project.setScmTag( tag );
        deploymentManager.deployProject( project, goals );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#removeProject(java.lang.String)
     */
    public void removeProject( String pid )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        deploymentManager.removeProject( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#checkoutProject(java.lang.String,
     *      java.lang.String)
     */
    public void checkoutProject( String pid, String scmTag )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        project.setScmTag( scmTag );
        deploymentManager.checkoutProject( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#deployProject(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void deployProject( String pid, String goals, String scmTag )
        throws Exception
    {
        DeployableProject project = new DeployableProject();
        project.setLabel( pid );
        project.setScmTag( scmTag );
        // first checkout and then deploy
        deploymentManager.checkoutProject( project );
        deploymentManager.deployProject( project, goals );
    }

}
