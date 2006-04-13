package org.codehaus.plexus.security;

import org.codehaus.plexus.PlexusException;

/**
  * Base class for all checked security related exceptions
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PlexusSecurityException extends PlexusException
{

    /**
     * 
     */
    public PlexusSecurityException()
    {
        super();
    }

    /**
     * @param message
     */
    public PlexusSecurityException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public PlexusSecurityException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PlexusSecurityException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
