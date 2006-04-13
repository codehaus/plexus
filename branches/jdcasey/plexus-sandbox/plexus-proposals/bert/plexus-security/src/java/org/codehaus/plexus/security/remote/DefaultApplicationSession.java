package org.codehaus.plexus.security.remote;

import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.security.Agent;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.security.request.proxy.ProxyServiceManagerDelegate;
import org.codehaus.plexus.security.session.InvalidSessionException;
import org.codehaus.plexus.util.ServiceManagerUtils;

/**
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultApplicationSession implements ApplicationSession
{
	/** The real session we deleagte session related stufff to. Don't want to expose 
	 * this direct to the client */
	private PlexusSession session;
	
	/** The proxy service manager we obtain client side components from  */
	private ProxyServiceManagerDelegate service;

	private RequestInterceptor interceptor;
	
    /**
     * 
     */
    public DefaultApplicationSession(PlexusSession session, ProxyServiceManagerDelegate service,RequestInterceptor interceptor)
    {
        super();
        this.service = service;
        this.session = session;
        this.interceptor = interceptor;
    }


    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String role)
    {
    	checkValidSession();
        return service.hasService(role);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String role) throws ServiceException
    {
    	if( session.isValid())
    	{    	
        	return  service.lookup(role,interceptor,session);
    	}
    	else
    	{
    		throw new ServiceException(role,"The session is invalid. Can no longer lookup components through this session");
    	}
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object obj)
    {
		service.release(obj);
    }

	private void checkValidSession() throws IllegalStateException
   {
	   if (session.isValid() == false )
	   {
		   throw new IllegalStateException("the session '" + session.getId() + "' is invalid'");
	   }
   }

    /**
     * @param key
     * @return
     */
    public boolean contains(String key)
    {
        return session.contains(key);
    }

    /**
     * @param key
     * @return
     */
    public Object get(String key)
    {
        return session.get(key);
    }

    /**
     * @return
     */
    public Agent getAgent()
    {
        return session.getAgent();
    }

    /**
     * @return
     */
    public long getCreationTime()
    {
        return session.getCreationTime();
    }

    /**
     * @return
     */
    public String getId()
    {
        return session.getId();
    }

    /**
     * @return
     */
    public long getLastAccessTime()
    {
        return session.getLastAccessTime();
    }

    /**
     * @return
     */
    public long getTimeout()
    {
        return session.getTimeout();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return session.hashCode();
    }

    /**
     * 
     */
    public void invalidate()
    {
        session.invalidate();
    }

    /**
     * @return
     */
    public boolean isValid()
    {
        return session.isValid();
    }

    /**
     * @return
     */
    public Set keys()
    {
        return session.keys();
    }

    /**
     * @param key
     * @param value
     */
    public void put(String key, Object value)
    {
        session.put(key, value);
    }

    /**
     * @param key
     * @return
     */
    public Object remove(String key)
    {
        return session.remove(key);
    }

    /**
     * @see org.codehaus.plexus.security.remote.ApplicationSession#lookup(java.lang.String, java.lang.String)
     */
    public Object lookup(String role, String id) throws ServiceException
    {
        return lookup( ServiceManagerUtils.getKey(role, id) );
    }

}
