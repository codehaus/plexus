package org.codehaus.plexus.security.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class Role
{
    private Map permissions = new HashMap();

    private String name;
    private String description;

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public Collection getPermissions()
    {
        return permissions.values();
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @param collection
     */
    public void setPermissions(Collection perms)
    {
        if (perms == null)
        {
            permissions.clear();
        }
        else
        {
            Iterator iter = perms.iterator();
            while (iter.hasNext())
            {
                Permission perm = (Permission) iter.next();
                permissions.put(perm.getName(), perm);
            }
        }

    }

    public boolean hasPermission(String name)
    {
        return permissions.containsKey(name);
    }
    
    public boolean hasPermission(Permission perm)
    {
        return perm == null ? false : permissions.containsKey(perm.getName());
    }

    public void revoke(String perm)
    {
        if (perm != null)
        {
            permissions.remove(perm);
        }
    }
    public void revoke(Permission perm)
    {
        if (perm != null)
        {

            permissions.remove(perm.getName());
        }
    }
    public void grant(Permission perm)
    {
        if (perm != null)
        {
            permissions.put(perm.getName(), perm);
        }
    }

}
