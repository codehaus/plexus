package org.codehaus.plexus.security.request;

import org.codehaus.plexus.security.SecurityService;
import org.codehaus.plexus.security.session.InvalidSessionException;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SecurityRequestInterceptorDelegate implements RequestInterceptor
{
	/** The securityService to notify of the begin and end of requests*/
    private SecurityService security;

    /**
     * 
     */
    public SecurityRequestInterceptorDelegate(SecurityService security)
    {
        super();
        this.security = security;
    }

    /**
    * @see org.codehaus.plexus.security.request.RequestInterceptor#beginRequest(java.lang.String)
    */
    public void beginRequest(String sessionId) throws InvalidSessionException
    {
        security.beginRequest(sessionId);
    }

    /**
     * @see org.codehaus.plexus.security.request.RequestInterceptor#endRequest()
     */
    public void endRequest()
    {
        security.endRequest();
    }

}
