package org.codehaus.plexus.security.mock;

import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.security.session.InvalidSessionException;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MockRequestInterceptor implements RequestInterceptor
{
	
    /**
     * @see org.codehaus.plexus.security.request.RequestInterceptor#beginRequest(java.lang.String)
     */
    public void beginRequest(String sessionId) throws InvalidSessionException
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.request.RequestInterceptor#endRequest(java.lang.String)
     */
    public void endRequest()
    {
        // TODO Auto-generated method stub

    }

}
