package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.security.PlexusSecurityException;

/**
  * Checked authentication exception
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class AuthenticationException extends PlexusSecurityException
{

    /**
     * 
     */
    public AuthenticationException()
    {
        super();
    }

    /**
     * @param message
     */
    public AuthenticationException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public AuthenticationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public AuthenticationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
