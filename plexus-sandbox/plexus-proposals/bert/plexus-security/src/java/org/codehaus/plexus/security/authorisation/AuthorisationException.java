package org.codehaus.plexus.security.authorisation;

import org.codehaus.plexus.security.PlexusSecurityException;

/**
  * Checked exception indicating access to a resource was denied.
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class AuthorisationException extends PlexusSecurityException
{

    /**
     * 
     */
    public AuthorisationException()
    {
        super();
    }

    /**
     * @param message
     */
    public AuthorisationException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public AuthorisationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public AuthorisationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
