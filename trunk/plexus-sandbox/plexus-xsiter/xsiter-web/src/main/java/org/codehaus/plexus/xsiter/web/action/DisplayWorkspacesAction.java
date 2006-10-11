/**
 * 
 */
package org.codehaus.plexus.xsiter.web.action;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.xsiter.deployer.Deployer;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @plexus.component role="com.opensymphony.xwork.Action" role-hint="displayWorkspaces"
 */
public class DisplayWorkspacesAction
    extends PlexusActionSupport
{

    /**
     * Deployer that manages deployment workspaces and deployments of 
     * projects/artifacts.
     * @plexus.requirement
     */
    private Deployer deployer;

    public String listWorkspaces()
    {
        getLogger().debug( "Obtaining list of workspaces..." );
        try
        {
            List list = deployer.getAllDeploymentWorkspaces();
            getLogger().info( "Found " + list.size() + " workspaces." );
            return SUCCESS;
        }
        catch ( Exception e )
        {
            getLogger().error( "Error obtaining list of deployment workspaces from the Deployer.", e );
            return ERROR;
        }

    }
}
