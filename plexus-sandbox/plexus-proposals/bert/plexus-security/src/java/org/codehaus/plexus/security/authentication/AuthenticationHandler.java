package org.codehaus.plexus.security.authentication;



/**
  * Used by the SessionManager to handle multiple tokens.
  * 
  * <p>Created on 12/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface AuthenticationHandler
{
	public static final String ROLE = AuthenticationHandler.class.getName();
	
	/** Return the id of the agent  with the given token*/
	public String authenticate(Object token) throws AuthenticationException;//throw authException if token invalid or no such token
	
	/** Test if the token of the given class is handled by this handler */
	public boolean handlesToken(Class clazz);
	
	/** Return the class of the token this authenticator handles */
	//public Class getHandledToken();
	
	/** Obtain a new token for the given agent*/
	//public Object newToken(Object credentials,Agent user);
	
	
	/** Clear the token for the given agent */
	//public void clearToken(Agent user);
}
