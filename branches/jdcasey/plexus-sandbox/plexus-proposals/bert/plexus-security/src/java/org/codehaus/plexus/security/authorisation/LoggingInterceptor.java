package org.codehaus.plexus.security.authorisation;

import java.lang.reflect.Method;

import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.util.MethodUtils;

/**
  * A logging interceptor which logs all method calls.
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class LoggingInterceptor implements MethodInterceptor
{
    private Logger logger;

    /**
     *
     */
    public LoggingInterceptor(Logger logger)
    {
        super();
        this.logger = logger;
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method, java.lang.Class[])
     */
    public void check(Class role, Method method, Class[] types)
    {
        check(role, method.getName(), types);
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method, java.lang.Object[])
     */
    public void check(Class role, Method method, Class[] types, Object[] args)
    {
        check(role, method.getName(), types);
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method)
     */
    public void check(Class role, Method method)
    {
        check(role, method.getName());
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String, java.lang.Object[])
     */
    public void check(Class role, String methodName, Class[] types, Object[] args)
    {
        check(role, methodName, types);
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String)
     */
    public void check(Class role, String methodName)
    {
        StringBuffer buff = new StringBuffer();
        buff.append("method called:").append(role.getName()).append("#").append(methodName);
        logger.info(buff.toString());
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class)
     */
    public void check(Class role)
    {
        logger.info("A public method on class:" + role + " is being invoked");
    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String, java.lang.Class[])
     */
    public void check(Class role, String methodName, Class[] types)
    {
        StringBuffer buff = new StringBuffer();
        buff.append("method called:").append(role.getName()).append("#").append(methodName).append(
            MethodUtils.generateParameterSignature(types));
        logger.info(buff.toString());
    }
}
