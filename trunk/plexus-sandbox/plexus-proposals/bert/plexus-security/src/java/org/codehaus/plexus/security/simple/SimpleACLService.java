package org.codehaus.plexus.security.simple;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SimpleACLService
{
	public static final String ROLE = SimpleACLService.class.getName();
	
	public SimpleACL buildACL(String userId);
	
	public boolean hasPermission(String gname);
	
	public boolean hasRole(String name);
	
	public Role  getRole(String name);
	
	public Permission getPermission(String name);
}
