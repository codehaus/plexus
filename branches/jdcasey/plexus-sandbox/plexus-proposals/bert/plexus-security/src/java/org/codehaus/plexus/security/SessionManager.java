package org.codehaus.plexus.security;

import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.request.RequestListener;
import org.codehaus.plexus.security.session.SessionLifecycleListener;


/**
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SessionManager
{
	public static final String ROLE = SessionManager.class.getName();
	
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

	/**
	 * Components wishing to be notified of request demarcation should 
	 * register themselves here
	 * 
	 * @param listener
	 */
	public void registerRequestListener(RequestListener listener);

	/**
	 * Components wishing to be notified of session lifecycle events should
	 * register themselves here
	 * 
	 * @param listener
	 */
	public void registerSessionLifecycleListener(SessionLifecycleListener listener);


	public void unRegisterRequestListener(RequestListener listener);


	public void unRegisterSessionLifecycleListener(SessionLifecycleListener listener);


	public void inValidateSession(String id);

	/** Test if the current thread is part of a request */
	public boolean isThreadWithinRequestScope();
}
