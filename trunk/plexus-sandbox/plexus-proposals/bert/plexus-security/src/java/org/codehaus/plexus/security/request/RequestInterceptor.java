package org.codehaus.plexus.security.request;

import org.codehaus.plexus.security.session.InvalidSessionException;

/**
  * Objects which handle request demarcation should provide one of these so
  * objects can notfify the request demarcation service that a request has been
  * initiated.
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface RequestInterceptor
{
	public void beginRequest(String sessionId) throws InvalidSessionException;
	
	public void endRequest();
}
