package org.codehaus.plexus.security.authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
/**
  * Provides basic services to handle authentication.
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public abstract class AbstractAuthenticationService extends AbstractLogEnabled implements AuthenticationService
{
	/** Authentication handlers keyed by authentication token type */
	private Map authHandlersByToken = new HashMap(  );
	   
    /**
     * 
     */
    public AbstractAuthenticationService()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.security.authentication.AuthenticationService#authenticate(java.lang.Object)
     */
    public String authenticate(Object token) throws AuthenticationException
    {
		String agentId = null;
    	try
    	{    	
			agentId = getAuthenticationHandler( token.getClass(  ) ) .authenticate( token );
    	}
    	catch( AuthenticationException e)
    	{
    		throw e;
    	}
    	catch(Exception e)
    	{
    	}
    	return agentId;
    }
    
    /**
     * Return the handler for the given token class.
     * 
     * @param token the token class to retrieve the handler for
     * @return the handler or null if no handler for the given token was registered. Also
     * returns null for a null token.
     */
	protected AuthenticationHandler getAuthenticationHandler( Class token )
	{
		if( token == null )
		{
			return null;
		}
		return  (AuthenticationHandler) authHandlersByToken.get(token.getName());
	}

	/**
	 * Register an AuthenticationHandler to handle tokens of the given class.
	 *  
	 * @param tokenClass the token class
	 * @param handler the handler instance
	 * @throws DuplicateHandlerException if a handler is already registered to handle
	 * the given token class.
	 * @throws NullPointerException if the token is null
	 */
	protected void registerAuthenticationHandler( Class tokenClass,
		AuthenticationHandler handler ) throws DuplicateHandlerException,NullPointerException
	{
		
		final String key = tokenClass.getName();
		getLogger().info("Registering AuthenticationHandler for token class:" + key );
		if( authHandlersByToken.containsKey( key ) )
		{
			throw new DuplicateHandlerException("Handler already registered for token class:" + key);
		}
		authHandlersByToken.put( key, handler );
	}

	/**
	 * Unregister a currently registered AuthenticationHandler.
	 * 
	 * @param tokenClass the token class for which to unregister the handler. Any tokens
	 * of the given class will no longer be handled by this AuthenticationService and any
	 * calls to <code>authenticate(Object)</code> using this token class will fail. 
	 * 
	 * @return the AuthenticationHandler previously registered to handle this token type, or null
	 * if this token is not currently handled or the token class is null.
	 * 
	 */
	protected AuthenticationHandler unRegisterAuthenticationHandler( 
		Class tokenClass )
	{
		if( tokenClass == null )
		{
			return null;
		}
		getLogger().info("UnRegistering AuthenticationHandler for token class:" + tokenClass.getName() );		
		return (AuthenticationHandler) authHandlersByToken.remove( tokenClass.getName() );
	}
	
	/**
	 * Test if there is a handler registered for a given token class 
	 *  
	 * @param tokenClass the token class to test. If null this method returns false.
	 * @return true if a handler is registered, false otherwise. 
	 */
	protected boolean isHandlerRegisteredFor(Class tokenClass)
	{
		if( tokenClass == null)
		{
			return false;
		}
		return authHandlersByToken.containsKey(tokenClass.getName() );		
	}
	
	/**
	 * Return all the registered handlers.
	 * 
	 * @return
	 */
	protected Collection getHandlers()
	{
		return authHandlersByToken.values();
	}

}
