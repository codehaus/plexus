package org.codehaus.plexus.security.authorisation;

import org.codehaus.plexus.security.RuntimeSecurityException;


/**
  * Thrown to indicate access to a resource is denied. This would usually be for 
  * method invocation on a SecureCompoennt when the agent invoking the method does not have
  * the required access rights. 
  * 
  * <p>Created on 17/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RuntimeAuthorisationException extends RuntimeSecurityException
{

    /**
     * 
     */
    public RuntimeAuthorisationException()
    {
        super();
    }

    /**
     * @param message
     */
    public RuntimeAuthorisationException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public RuntimeAuthorisationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RuntimeAuthorisationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
