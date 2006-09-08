package org.codehaus.plexus.security.authorization.rbac.web.action;

import org.codehaus.plexus.xwork.action.PlexusActionSupport;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.security.rbac.Role;

/**
 * PermissionActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityPermission"

 */
public class PermissionActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RbacStore store;

    private int roleId;

    private Role role;

    public String display()
        throws RbacStoreException
    {
        role = store.getRole( roleId );

        return SUCCESS;
    }

}
