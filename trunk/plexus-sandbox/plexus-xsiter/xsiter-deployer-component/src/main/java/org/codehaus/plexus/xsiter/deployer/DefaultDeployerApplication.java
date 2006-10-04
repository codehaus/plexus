/**
 * 
 */
package org.codehaus.plexus.xsiter.deployer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployerResource;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject.ProjectProperties;

/**
 * Facade to the underlying Deployer components.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 22/09/2006, 11:01:53 AM
 * @plexus.component
 */
public class DefaultDeployerApplication
    extends AbstractLogEnabled
    implements DeployerApplication
{

    /**
     * Deployment Manager to use to delegate calls to.
     * 
     * @plexus.requirement
     */
    private Deployer deploymentManager;

    /**
     * Prompter for interactivity.
     * 
     * @plexus.requirement
     */
    private Prompter prompter;

    /**
     * @see org.codehaus.plexus.xsiter.deployer.DeployerApplication#addProject(java.util.Properties)
     */
    public void addProject( Properties properties )
        throws Exception
    {
        // List list = ProjectProperties.getRequiredProjectProperties ();
        List list = Arrays.asList( ProjectProperties.values() );
        Vector v = new Vector( list );
        // track required properties not found
        Vector v2 = (Vector) v.clone();
        DeployableProject project = new DeployableProject();

        // check if we have all required properties
        for ( Iterator it = v.iterator(); it.hasNext(); )
        {
            ProjectProperties p = (ProjectProperties) it.next();
            String val = (String) properties.get( p.getName() );
            if ( null != val )
            {
                v2.remove( p );
            }
            assignProjectProperty( project, p, val );
        }

        // Prompt for missing properties
        if ( v2.size() > 0 )
        {
            prompter.showMessage( "Please provide the following properties for Deployment workspace setup." );
            for ( Iterator it = v2.iterator(); it.hasNext(); )
            {
                ProjectProperties p = (ProjectProperties) it.next();
                String val = null;
                while ( null == val || val.trim().equals( "" ) )
                    val = prompter.prompt( "Enter a value for " + p.getName() );
                assignProjectProperty( project, p, val );
            }
        }
        getLogger().debug( "adding project '" + project.getLabel() + "'" );

        deploymentManager.addProject( project );
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

    /**
     * Service method that sets up properties for a {@link DeployableProject}
     * instance.
     * 
     * @param project
     * @param property
     * @param value
     */
    private void assignProjectProperty( DeployerResource project, ProjectProperties property, String value )
    {
        if ( property == ProjectProperties.PROP_LABEL )
            project.setLabel( value );
        if ( property == ProjectProperties.PROP_SCM_URL )
            project.setScmURL( value );
        if ( property == ProjectProperties.PROP_SCM_USER )
            project.setScmUsername( value );
        if ( property == ProjectProperties.PROP_SCM_PASSWORD )
            project.setScmPassword( value );
        if ( property == ProjectProperties.PROP_SCM_TAG )
            project.setScmTag( value );
    }
}
