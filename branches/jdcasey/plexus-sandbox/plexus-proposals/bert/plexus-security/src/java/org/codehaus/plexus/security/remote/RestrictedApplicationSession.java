package org.codehaus.plexus.security.remote;

import java.util.Collection;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.security.Agent;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.security.request.proxy.ProxyServiceManagerDelegate;
import org.codehaus.plexus.util.ServiceManagerUtils;

/**
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RestrictedApplicationSession implements ApplicationSession
{
    /** The real session we deleagte session related stufff to. Don't want to expose 
     * this direct to the client */
    private PlexusSession session;

    /** The proxy service manager we obtain client side components from  */
    private ProxyServiceManagerDelegate service;

    private RequestInterceptor interceptor;

    /** List of components which access is granted to. The names are the
     * names of the roles used to lookup components. Since this list is not
     * expected to be modified once generated then there will only be
     * concurrent reads, so a  write threadsafe list is not required. */
    private Collection allowedComponents;

    /**
     * 
     */
    public RestrictedApplicationSession(
        PlexusSession session,
        ProxyServiceManagerDelegate service,
        RequestInterceptor interceptor,
        Collection allowedComponents)
    {
        super();
        this.service = service;
        this.session = session;
        this.interceptor = interceptor;
        this.allowedComponents = allowedComponents;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String role)
    {
        return service.hasService(role);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String role) throws ServiceException
    {
        if (allowedComponents.contains(role))
        {
            return service.lookup(role, interceptor, session);
        }
        else
        {
            throw new ServiceException(role, "Can not find the specified role");
        }
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object obj)
    {
        service.release(obj);
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
        return lookup(ServiceManagerUtils.getKey(role, id));
    }

}
