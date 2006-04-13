package org.codehaus.plexus.security.authorisation;

import java.lang.reflect.Method;

/**
  * Intercepts method calls.
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface MethodInterceptor
{		
	public void check(Class role);	

	public void check(Class role,String methodName);// throws PlexusRuntimeAuthorisationException;
		
	public void check(Class role,String methodName, Class[] types, Object[] args);//  throws PlexusRuntimeAuthorisationException;
	
	public void check(Class role,String methodName, Class[] types);//  throws PlexusRuntimeAuthorisationException;
	
	public void check(Class role,Method method);//  throws PlexusRuntimeAuthorisationException;
	
	public void check(Class role,Method method, Class[] types, Object[] args);//  throws PlexusRuntimeAuthorisationException;
	
	public void check(Class role,Method method, Class[] types);//  throws PlexusRuntimeAuthorisationException;
}
