package org.codehaus.plexus.security.authorisation.lifecycle;

import org.codehaus.plexus.security.authorisation.MethodInterceptor;



/**
  * Implement this interface on your component if you want plexus to apply method
  * invocation security to it. (implemented using aspects)
  * 
  * <p>Created on 18/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SecureComponent
{
	/**
	 * Sets the interceptor to use to intercept method calls to determine method access
	 *  
	 * @param service
	 */
	public void setMethodInterceptor(MethodInterceptor interceptor);
}
