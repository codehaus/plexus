package org.codehaus.plexus.util;

import java.lang.reflect.Method;

/**
  * Some useful utility methods for playing around with methods.
  * 
  * <p>Created on 18/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MethodUtils
{
	/**
	 * Returns a method signature consisting of the methodName and parameter types
	 *  
	 * @param method
	 * @param params
	 * @return
	 */
    public static final String generateMethodSignature(Method method, Class[] params)
    {
        StringBuffer hash = new StringBuffer();
        hash.append(method.getName()).append('(');
        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
                if (i == 0)
                {
                    hash.append(',');
                }
                hash.append(params[i].getName());
            }
        }
        hash.append(')');
        return hash.toString();
    }

	/**
	 * Returns a signature for an array of parameter types
	 * 
	 * @param params
	 * @return
	 */
    public static final String generateParameterSignature(Class[] params)
    {
        StringBuffer hash = new StringBuffer();
        hash.append('(');
        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
                if (i == 0)
                {
                    hash.append(',');
                }
                hash.append(params[i].getName());
            }
        }
        hash.append(')');
        return hash.toString();
    }

}
