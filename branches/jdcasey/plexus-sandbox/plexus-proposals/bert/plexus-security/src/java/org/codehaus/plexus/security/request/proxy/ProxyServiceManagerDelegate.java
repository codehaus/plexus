package org.codehaus.plexus.security.request.proxy;

import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * Generates proxy classes for components.Wraps a serviceManager from which 
  * it obtains components and dynamically generates proxies for them.
  * 
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ProxyServiceManagerDelegate implements Serviceable // extends RestrictedServiceManager
{

    /** **/
    private Map proxyByTarget = new ThreadSafeMap();
    private Map targetByProxy = new ThreadSafeMap();

    /**
     * The service manager to lookup for the real components
     * 
     */
    private ServiceManager service;

    /**
     * 
     */
    public ProxyServiceManagerDelegate()
    {
        super();
    }

    /**
     *  Return a proxied component for the given role, using the given interceptor and session.
     * 
     * @param role the role of the component to lookup
     * @param requestInterceptor the interceptor to use to intercept method calls
     * @param session the session to associate with method calls
     * @return a proxied component implemrnting the given role
     * @throws ServiceException if the component with the given role could not be found
     */
    public Object lookup(String role, RequestInterceptor requestInterceptor, PlexusSession session)
        throws ServiceException
    {
		Object proxy = null;
        requestInterceptor.beginRequest(session.getId());
        try
        {
            Object target = service.lookup(role);
            proxy = proxyByTarget.get(target);
            if (proxy == null)
            {
                proxy =
				ProxyFactory.createProxyComponent(
                        obtainClass(role),
                        target,
                        requestInterceptor,
                        session);
                proxyByTarget.put(target, proxy);
                targetByProxy.put(proxy, target);
            }
        }
        finally
        {
            requestInterceptor.endRequest();
        }
        return proxy;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object role)
    {
        if (role == null)
        {
            return;
        }
        //obtain the original component and return it
        //back to the service manager
        Object target = targetByProxy.remove(role);
        if (target != null)
        {
            proxyByTarget.remove(role);
            service.release(target);
        }
    }

    /**
     * Return the interface to implement for the given role. For now just assume the role
     * is a name of an interface
     * 
     * @param role
     * @return
     */
    private Class obtainClass(String role) throws ServiceException
    {
        Class clazz = null;
        try
        {
            clazz = getClass().getClassLoader().loadClass(role);
        }
        catch (ClassNotFoundException e)
        {
            throw new ServiceException(role, "Could not generate proxy class for role");
        }
        return clazz;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String role)
    {
        return service.hasService(role);
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager service) throws ServiceException
    {
        this.service = service;
    }

}
