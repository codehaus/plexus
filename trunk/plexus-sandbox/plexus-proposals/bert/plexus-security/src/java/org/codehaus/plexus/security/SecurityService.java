package org.codehaus.plexus.security;

import org.codehaus.plexus.security.authentication.AuthenticationException;


/**
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SecurityService
{
	public static final String ROLE = SecurityService.class.getName();
	
	/**
	 * Authenticate and obtain a PlexusSession using the given token.
	 * 
	 * @param authenticationToken
	 * @return
	 * @throws AuthenticationException
	 */
	public PlexusSession authenticate(Object authenticationToken) throws AuthenticationException;
	
	/**
	 * Return the Agent associated with the current thread. Always returns a non-null value.
	 * 
	 */
	public Agent getAgent();
	
	/**
	 * Return the session associated with the current thread, or null if no session
	 * is currently bound.
	 * 
	 * @return PlexusSession the session
	 */
	public PlexusSession getSession();	

	/**
	 * Begin a request within the session identified by the given id. The
	 * current thread has the Session, and agent bound to it.
	 *
	 * @todo what todo if the thread already has something bound to it?? do
	 * we need to notify anything??
	 *
	 * @param sessionId
	 */
	public void beginRequest(String sessionId);


	/**
	 * End the request associated with this thread
	 *
	 * @param sessionId
	 */
	public void endRequest();



}
