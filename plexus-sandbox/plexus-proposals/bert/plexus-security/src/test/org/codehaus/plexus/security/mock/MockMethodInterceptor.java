package org.codehaus.plexus.security.mock;

import java.lang.reflect.Method;

import org.codehaus.plexus.security.authorisation.MethodInterceptor;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MockMethodInterceptor implements MethodInterceptor
{

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method, java.lang.Class[], java.lang.Object[])
     */
    public void check(Class role, Method method, Class[] types, Object[] args)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method, java.lang.Class[])
     */
    public void check(Class role, Method method, Class[] types)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.reflect.Method)
     */
    public void check(Class role, Method method)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String, java.lang.Class[], java.lang.Object[])
     */
    public void check(Class role, String methodName, Class[] types, Object[] args)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String, java.lang.Class[])
     */
    public void check(Class role, String methodName, Class[] types)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class, java.lang.String)
     */
    public void check(Class role, String methodName)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.MethodInterceptor#check(java.lang.Class)
     */
    public void check(Class role)
    {
        // TODO Auto-generated method stub

    }

}
