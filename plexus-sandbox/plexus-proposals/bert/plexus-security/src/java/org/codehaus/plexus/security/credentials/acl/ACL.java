package org.codehaus.plexus.security.credentials.acl;

import java.util.Collection;

/**
  * 
  * <p>Created on 7/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ACL
{
	/** Test if the agent has the given permission in any role */
	public boolean hasPermission(String name);
	
	/** Test if the agent has the given role within any resource */
	public boolean hasRole(String name);
	
	/** Test if the agent has the given permission in the given resource */
	public boolean hasPermission(String permission, String resource);
	
	/** Test if the agent has the given permission in any of the given resources */
	public boolean hasPermission(String permission, Collection resources);
	
	/** Test if the agent has the given role in the given resource */
	public boolean hasRole(String role,String resource);
	
	/** Test if the agent has the given role in any of the given resources */
	public boolean hasRole(String role,Collection resources);
	
	/** Test if the agents has any of the given roles */
	public Collection getRoles(String resource);
	
	
}
