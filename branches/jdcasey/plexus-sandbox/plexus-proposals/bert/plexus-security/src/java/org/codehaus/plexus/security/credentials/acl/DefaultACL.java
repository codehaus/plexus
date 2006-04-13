package org.codehaus.plexus.security.credentials.acl;

import java.util.Collection;



/**
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultACL implements ACL
{
	/** True if allow all, false otherwise*/
	private boolean allow = false;
    /**
     * 
     */
    public DefaultACL(boolean allow)
    {
        super();
        this.allow = allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#getRoles(java.lang.String)
     */
    public Collection getRoles(String resource)
    {
        return null;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasPermission(java.lang.String, java.util.Collection)
     */
    public boolean hasPermission(String permission, Collection resources)
    {
        return allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasPermission(java.lang.String, java.lang.String)
     */
    public boolean hasPermission(String permission, String resource)
    {
		return allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasPermission(java.lang.String)
     */
    public boolean hasPermission(String name)
    {
		return allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasRole(java.lang.String, java.util.Collection)
     */
    public boolean hasRole(String role, Collection resources)
    {
		return allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasRole(java.lang.String, java.lang.String)
     */
    public boolean hasRole(String role, String resource)
    {
		return allow;
    }

    /**
     * @see org.codehaus.plexus.security.ACL#hasRole(java.lang.String)
     */
    public boolean hasRole(String name)
    {
		return allow;
    }

}
