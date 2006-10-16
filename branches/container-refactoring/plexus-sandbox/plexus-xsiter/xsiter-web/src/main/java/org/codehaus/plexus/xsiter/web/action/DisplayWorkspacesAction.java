/**
 * 
 */
package org.codehaus.plexus.xsiter.web.action;

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
        // TODO: Obtain workspaces
        return SUCCESS;
    }
}
