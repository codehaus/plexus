package org.codehaus.plexus.security.request.proxy;

import java.lang.reflect.Proxy;

import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;

/**
  * Creates a proxy class to wrap a particular role
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ProxyFactory
{
	/**
	 * Creates a proxy class to ensure session and request are bound to thread
	 *  
	 * @param interfce
	 * @param realComponent
	 * @return
	 */
    public static Object createProxyComponent(Class interfce, Object component,RequestInterceptor interceptor,PlexusSession sess)
    {
		ProxyInvocationHandler handler = new ProxyInvocationHandler(component,interceptor,sess);
		return  Proxy.newProxyInstance(interfce.getClassLoader(), new Class[]{interfce}, handler);
    }


}


