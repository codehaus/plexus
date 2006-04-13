package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.security.RuntimeSecurityException;



/**
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RuntimeAuthenticationException extends RuntimeSecurityException
{

    /**
     * 
     */
    public RuntimeAuthenticationException()
    {
        super();
    }

    /**
     * @param message
     */
    public RuntimeAuthenticationException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public RuntimeAuthenticationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RuntimeAuthenticationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
