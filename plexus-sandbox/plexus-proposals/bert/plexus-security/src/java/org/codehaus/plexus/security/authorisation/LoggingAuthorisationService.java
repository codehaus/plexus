package org.codehaus.plexus.security.authorisation;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class LoggingAuthorisationService extends AbstractLogEnabled implements AuthorisationService
{
	//private Map componentAccessMaps = new ThreadSafeMap();
	//private SecurityService security;
	
	/**
	 * Used to intercept method calls
	 */
	private MethodInterceptor interceptor;
	    
    /**
     * Return the interceptor used to intercept and test method calls.
     * 
     * @return
     */
    public MethodInterceptor getMethodInterceptor()
    {
    	if( interceptor == null )
    	{
    		interceptor = new LoggingInterceptor( getLogger() );
    	}
    	return interceptor;
    }

}
