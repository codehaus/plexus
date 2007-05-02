package org.codehaus.plexus.redback.rbac.profile;

import org.codehaus.plexus.redback.rbac.Role;

/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public interface RoleProfileManager
{
    String ROLE = RoleProfileManager.class.getName();

    public Role getRole( String roleName )
        throws RoleProfileException;

    public Role getDynamicRole( String roleName, String resource )
        throws RoleProfileException;

    public Role mergeRoleProfiles( String roleHint, String withRoleHint )
        throws RoleProfileException;

    public void deleteDynamicRole( String roleHint, String resource )
        throws RoleProfileException;

    public void renameDynamicRole( String roleHint, String oldResource, String newResource )
        throws RoleProfileException;

    /*
     * TODO get rid of this initialization in the api, its not appropriate for this manager and
     * could be much better done using plexus initializing interface
     */
    public void initialize()
        throws RoleProfileException;

    public boolean isInitialized();

    public void setInitialized( boolean initialized );
}