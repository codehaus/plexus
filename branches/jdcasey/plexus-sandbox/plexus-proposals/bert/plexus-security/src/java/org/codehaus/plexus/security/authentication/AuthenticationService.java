package org.codehaus.plexus.security.authentication;




/**
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface AuthenticationService
{
	public static final String ROLE = AuthenticationService.class.getName().toString();
	
	/**
	 * Authenticate an agent using the given authentication token. Returns the id of the 
	 * agent this represents.
	 * 
	 * @param authenticationToken the authentication token to authenticate with
	 * @return the id of the agent identified by this token
	 * @throws AuthenticationException if the token was invalid or an internal error occurred.
	 */
	public String authenticate(Object authenticationToken) throws AuthenticationException;
	
	
	
}
