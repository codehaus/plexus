package org.codehaus.plexus.security.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.util.DebugUtils;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultSimpleACL implements SimpleACL
{
    private Map permissionList = new HashMap();
	
	
	private Permission[] permissionArray;
	
	private Role[] roleArray;
	
    private Map roleList = new HashMap();

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
            if (roleList.containsKey(role.getName()) == false)
            {
                roleList.put( role.getName(), role );
            }
            Iterator iterP = role.getPermissions().iterator();
            while (iterP.hasNext())
            {
                Permission perm = (Permission) iterP.next();
                if (permissionList.containsKey(perm.getName()) == false)
                {
                    permissionList.put(perm.getName(),perm);
                }
            }
        }
    }
    /**
     * @see org.codehaus.plexus.security.credentials.tuaacl.ACL#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String roleName, String permission)
    {
        if (roleList.containsKey(roleName))
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
        return permissionList.containsKey(name) && system.hasPermission(name);
    }

    /**
     * @see org.codehaus.plexus.security.credentials.tuaacl.ACL#hasRole(java.lang.String)
     */
    public boolean hasRole(String role)
    {
        return roleList.containsKey(role) && system.hasRole(role);
    }

    /**
     * @see org.codehaus.plexus.security.simple.SimpleACL#getPermissions()
     */
    public Permission[] getPermissions()
    {
    	if( permissionArray == null)
    	{
    		permissionArray =  (Permission[]) permissionList.values().toArray(new Permission[]{});
    	}
        return permissionArray;
    }

    /**
     * @see org.codehaus.plexus.security.simple.SimpleACL#getRoles()
     */
    public Role[] getRoles()
    {
        if( roleArray == null)
        {
        	roleArray = (Role[])roleList.values().toArray(new Role[]{});
        }
        return roleArray;
    }

}
