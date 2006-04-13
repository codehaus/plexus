package org.codehaus.plexus.security.simple;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SimpleACL
{
    public boolean hasPermission(String name);

    public boolean hasRole(String role);

    public boolean hasPermission(String role, String permission);

	public Permission[] getPermissions();
	
	public Role[] getRoles();
}
