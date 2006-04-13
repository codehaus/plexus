package org.codehaus.plexus.security.session;

import org.codehaus.plexus.security.RuntimeSecurityException;

/**
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class InvalidSessionException extends RuntimeSecurityException
{

    /**
     * 
     */
    public InvalidSessionException()
    {
        super();
    }

    /**
     * @param message
     */
    public InvalidSessionException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidSessionException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidSessionException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
