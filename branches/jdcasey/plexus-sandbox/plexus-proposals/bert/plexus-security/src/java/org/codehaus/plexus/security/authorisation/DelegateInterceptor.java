package org.codehaus.plexus.security.authorisation;

import java.lang.reflect.Method;


/**
  * Delegates to another interceptor. Useful if one wants to change an interceptor during
  * runtime and other classes may already have reference to the interceptor.
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public final class DelegateInterceptor
{
	private MethodInterceptor realInterceptor;
	
    /**
     * 
     */
    public DelegateInterceptor(MethodInterceptor interceptor)
    {
        super();
        this.realInterceptor = interceptor;
    }
    
    protected MethodInterceptor getRealInterceptor()
    {
    	return realInterceptor;
    }
    
    protected void setRealInterceptor(MethodInterceptor interceptor)
    {
    	this.realInterceptor = interceptor;
    }

    /**
     * @param role
     */
    public void check(Class role)
    {
        realInterceptor.check(role);
    }

    /**
     * @param role
     * @param method
     * @throws AuthorisationExeption
     */
    public void check(Class role, Method method) 
    {
        realInterceptor.check(role, method);
    }

    /**
     * @param role
     * @param method
     * @param types
     * @throws AuthorisationExeption
     */
    public void check(Class role, Method method, Class[] types) 
    {
        realInterceptor.check(role, method, types);
    }

    /**
     * @param role
     * @param method
     * @param types
     * @param args
     * @throws AuthorisationExeption
     */
    public void check(Class role, Method method, Class[] types, Object[] args)
        
    {
        realInterceptor.check(role, method, types, args);
    }

    /**
     * @param role
     * @param methodName
     * @throws AuthorisationExeption
     */
    public void check(Class role, String methodName) 
    {
        realInterceptor.check(role, methodName);
    }

    /**
     * @param role
     * @param methodName
     * @param types
     * @throws AuthorisationExeption
     */
    public void check(Class role, String methodName, Class[] types) 
    {
        realInterceptor.check(role, methodName, types);
    }

    /**
     * @param role
     * @param methodName
     * @param types
     * @param args
     * @throws AuthorisationExeption
     */
    public void check(Class role, String methodName, Class[] types, Object[] args)
        
    {
        realInterceptor.check(role, methodName, types, args);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return realInterceptor.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return realInterceptor.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return realInterceptor.toString();
    }

}
