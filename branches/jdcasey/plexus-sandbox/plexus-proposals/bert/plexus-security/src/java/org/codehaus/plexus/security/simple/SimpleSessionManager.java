package org.codehaus.plexus.security.simple;

import org.apache.avalon.framework.activity.Initializable;
import org.codehaus.plexus.security.AbstractSessionManager;
import org.codehaus.plexus.security.Agent;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SimpleSessionManager extends AbstractSessionManager implements Initializable
{
	private SimpleACLService builder;
	
    /**
     * 
     */
    public SimpleSessionManager()
    {
        super();
    }

	/**
	 * @see org.codehaus.plexus.security.AbstractSessionManager#createAgent(java.lang.String)
	 */
	protected Agent createAgent(String userId) 
	{
		return new SimpleAgent( userId,userId,builder.buildACL(userId));
	}
    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
		builder = (SimpleACLService)getServiceManager().lookup(SimpleACLService.ROLE);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        getServiceManager().release(builder);
        super.dispose();
    }

}
