package org.codehaus.plexus.util;

/**
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ServiceManagerUtils
{
	/**
	 * Generate a key for the service manager when using role and ids. 
	 * 
	 * <p>As a note: there are quite a few  uses of role/ids's within the plexus components codebase
	 * and they all simply concatenate the role and id together then lookup the component using this
	 * new key. This is not good as component
	 * lookups should not entail having to know how the underlying service manager works, so currently
	 * when the role/id lookup is fixed to not be simple concatenation all this code will break. It is 
	 * therefore advised to use this utility method to generate keys so there is only one
	 * location in the codebase to change. - Bert van Brakel
	 *  </p>
	 * @param role
	 * @param id
	 * @return
	 */
	public static final String getKey(String role, String id)
	{
		return role + id;		
	}
}
