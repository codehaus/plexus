package org.codehaus.plexus.security.simple;

import java.util.ArrayList;
import java.util.Collection;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RoleUserEntry
{
	/** Name of the role */
	private String roleName;
	/** List of user names */
	private Collection users =new  ArrayList();
    /**
     * 
     */
    public RoleUserEntry()
    {
        super();
    }

    /**
     * 
     */
    public RoleUserEntry(String roleName)
    {
        super();
        this.roleName = roleName;
    }

    /**
     * @return
     */
    public String getRoleName()
    {
        return roleName;
    }

    /**
     * @return
     */
    public Collection getUsers()
    {
        return users;
    }

    /**
     * @param string
     */
    public void setRoleName(String name)
    {
        this.roleName = name;
    }

    /**
     * @param collection
     */
    public void setUsers(Collection users)
    {
        this.users = users;
    }
    
    public void grant(String userId)
    {
    	if( userId != null && users.contains(userId) == false)
    	{
    		users.add(userId);
    	}
    }
    
    public void revoke(String userId)
    {
    	if( userId != null)
    	{
    		users.remove(userId);
    	}
    }
    
    public boolean containsUser(String userId)
    {
    	return userId == null ? false:users.contains(userId);
    }

}
