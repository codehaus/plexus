package org.codehaus.plexus.security.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultSimpleACL implements SimpleACL
{
    private List permissionList = new ArrayList();

    private List roleList = new ArrayList();

    private SimpleACLService system;
    /**
     * 
     */
    public DefaultSimpleACL(Collection roles, SimpleACLService service)
    {
        super();
        this.system = service;
        init(roles);
    }

    private void init(Collection roles)
    {
        Iterator iterR = roles.iterator();
        while (iterR.hasNext())
        {
            Role role = (Role) iterR.next();
            if (roleList.contains(role.getName()) == false)
            {
                roleList.add(role.getName());
            }
            Iterator iterP = role.getPermissions().iterator();
            while (iterP.hasNext())
            {
                Permission perm = (Permission) iterP.next();
                if (permissionList.contains(perm.getName()) == false)
                {
                    permissionList.add(perm.getName());
                }
            }
        }
    }
    /**
     * @see org.codehaus.plexus.security.credentials.tuaacl.ACL#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String roleName, String permission)
    {
        if (roleList.contains(roleName))
        {
            Role role = system.getRole(roleName);
            if (role == null)
            {
                return false;
            }
            else
            {
                return role.hasPermission(permission);
            }

        }
        else
            return false;
    }

    /**
     * @see org.codehaus.plexus.security.credentials.tuaacl.ACL#hasPermission(java.lang.String)
     */
    public boolean hasPermission(String name)
    {
        return permissionList.contains(name) && system.hasPermission(name);
    }

    /**
     * @see org.codehaus.plexus.security.credentials.tuaacl.ACL#hasRole(java.lang.String)
     */
    public boolean hasRole(String role)
    {
        return roleList.contains(role) && system.hasRole(role);
    }

}
