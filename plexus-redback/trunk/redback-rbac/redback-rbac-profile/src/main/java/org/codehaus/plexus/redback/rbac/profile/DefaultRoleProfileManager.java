package org.codehaus.plexus.redback.rbac.profile;

import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.rbac.Role;

/**
 * DefaultRoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.redback.rbac.profile.RoleProfileManager"
 *   role-hint="default"
 */
public class DefaultRoleProfileManager
    extends AbstractLogEnabled
    implements RoleProfileManager
{
    /**
     * @plexus.requirement
     */
    protected PlexusContainer container;

    /**
     * @plexus.requirement role="org.codehaus.plexus.redback.rbac.profile.RoleProfile"
     */
    protected List knownRoleProfiles;

    /**
     * @plexus.requirement role="org.codehaus.plexus.redback.rbac.profile.DynamicRoleProfile"
     */
    protected List knownDynamicRoleProfiles;

    /**
     * true of this role profile manager has been initialized
     */
    protected boolean initialized = false;

    /**
     *
     * @param roleHint 
     * @return
     * @throws org.codehaus.plexus.redback.rbac.profile.RoleProfileException
     */
    public Role getRole( String roleHint )
        throws RoleProfileException
    {
        try
        {
            RoleProfile roleProfile =  (RoleProfile)container.lookup( RoleProfile.ROLE, roleHint );

            return roleProfile.getRole();
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate role profile " + roleHint, cle );
        }
    }

    public Role getDynamicRole( String roleHint, String resource )
        throws RoleProfileException
    {
        try
        {
            DynamicRoleProfile roleProfile =  (DynamicRoleProfile)container.lookup( DynamicRoleProfile.ROLE, roleHint );

            return roleProfile.getRole( resource ); 
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate dynamic role profile " + roleHint, cle );
        }
    }

    public Role mergeRoleProfiles( String roleHint, String withRoleHint )
        throws RoleProfileException
    {
        try
        {
            RoleProfile roleProfile =  (RoleProfile)container.lookup( RoleProfile.ROLE, roleHint );

            return roleProfile.mergeWithRoleProfile( withRoleHint );
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate role profile " + roleHint, cle );
        }
    }

    public void deleteDynamicRole( String roleHint, String resource )
        throws RoleProfileException
    {
        try
        {
            DynamicRoleProfile roleProfile =  (DynamicRoleProfile)container.lookup( DynamicRoleProfile.ROLE, roleHint );

            roleProfile.deleteRole( resource );
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate dynamic role profile " + roleHint, cle );
        }

    }


    public void renameDynamicRole( String roleHint, String oldResource, String newResource )
        throws RoleProfileException
    {
        try
        {
            DynamicRoleProfile roleProfile =  (DynamicRoleProfile)container.lookup( DynamicRoleProfile.ROLE, roleHint );

            roleProfile.renameRole( oldResource, newResource );
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate dynamic role profile " + roleHint, cle );
        }

    }


    public void initialize()
        throws RoleProfileException
    {
        initialized = true;
    }

    public List getKnownRoleProfiles()
    {
        return knownRoleProfiles;
    }

    public List getKnownDynamicRoleProfiles()
    {
        return knownDynamicRoleProfiles;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public void setInitialized( boolean initialized )
    {
        this.initialized = initialized;
    }
}