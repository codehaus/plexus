package org.codehaus.plexus.security.request.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.request.RequestInterceptor;

/**
  * Invocation handler used by proxied components. THis handler first notifies a RequestInterceptor
  * that a method was invokde, then invokes the method, then informsa the interceptor when the
  * method has completed. The interceptor is notified of the end of the method call
  * even if the method invocation throws an error.
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ProxyInvocationHandler implements InvocationHandler
{
    final private Object realTarget;
    final private PlexusSession session;
    final private RequestInterceptor requestInterceptor;

    /**
     * 
     */
    public ProxyInvocationHandler(
        Object target,
        RequestInterceptor interceptor,
        PlexusSession session)
    {
        super();
        if (session == null)
        {
            throw new IllegalArgumentException("Session cannot be null");
        }
        this.session = session;

        if (target == null)
        {
            throw new IllegalArgumentException("Target Role cannot be null");
        }
        this.realTarget = target;

        if (interceptor == null)
        {
            throw new IllegalArgumentException("Request interceptor cannot be null");
        }
        this.requestInterceptor = interceptor;

    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        //DebugUtils.debug(this.getClass(), "invoking method:" + method.getName());
        if ((method.getName() == "hashCode" && args== null)
            || (method.getName() == "equals" && args.length == 1))
        {
            //DebugUtils.debug(this.getClass(), "Bypassing method:" + method.getName());
            return method.invoke(realTarget, args);
        }
        else
        {
            //DebugUtils.debug(this.getClass(),"start request and invoke method:" + method.getName());
            requestInterceptor.beginRequest(session.getId());
            try
            {
                return method.invoke(realTarget, args);
            }
            finally
            {
                requestInterceptor.endRequest();
            }
        }
    }

}