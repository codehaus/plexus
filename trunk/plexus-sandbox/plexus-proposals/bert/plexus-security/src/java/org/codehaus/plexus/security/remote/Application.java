package org.codehaus.plexus.security.remote;

import org.codehaus.plexus.security.authentication.AuthenticationException;

/**
  * Front end given to clients
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface Application
{
	public static final String ROLE = Application.class.getName();
	
	/**
	 * Return the name of this application
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the major version of this application
	 * @return
	 */
	public String getMajorVersion();
	
	/**
	 * Return the minor version of this application
	 * @return
	 */
	public String getMinorVersion();
	
	/**
	 * Return the build number of this application
	 * 
	 * @return
	 */
	public String getBuild();
	
	/**
	 * Return the name of the entity who this application is registered with
	 * @return
	 */
	public String getRegisteredTo();
	
	/**
	 * Authenticate an agent with the given authentication token
	 * 
	 * @param token the authentication token
	 * @return the ApplicationSession for the agent identified by the token
	 * @throws AuthenticationException if the supplied token is not supported, the token is invalid,
	 * or a backend problem occured
	 */
	public ApplicationSession authenticate(Object token) throws AuthenticationException;
		
}
