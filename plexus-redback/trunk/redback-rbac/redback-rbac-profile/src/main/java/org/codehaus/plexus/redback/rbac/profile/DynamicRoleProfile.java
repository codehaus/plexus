package org.codehaus.plexus.redback.rbac.profile;

import java.util.List;

import org.codehaus.plexus.redback.rbac.Role;

/**
 * RoleProfile: Implementations of this interface should construct a role and pass it
 * back to the calling code.  The implementation should also add the role to the store as well
 * via the RBACManager.
 *
 * Implementations of this interface address the need for the generation of a new role
 * specifically targeted at a particular resource.  This necessitates the creation of a
 * consistent set of Permissions for dealing with Operations on the resource, and then the
 * adding of the Permissions to the Role and finally the Role itself.
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 */
public interface DynamicRoleProfile
{
    public static final String ROLE = DynamicRoleProfile.class.getName();

    public String getRoleName( String resource );

    public List getOperations();

    public List getGlobalOperations();

     /**
     * is this role profile assignable
     * @return
     */
    public boolean isAssignable();

    /**
     * are roles based on this role profile permanent
     */
    public boolean isPermanent();

    public List getChildRoles();

    public List getDynamicChildRoles( String resource );

    public Role getRole( String resource ) throws RoleProfileException;

    public void deleteRole( String resource ) throws RoleProfileException;

    public void renameRole( String oldResource, String newResource ) throws RoleProfileException;
}
