package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.PlexusException;

/**
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DuplicateHandlerException extends PlexusException
{

    /**
     * 
     */
    public DuplicateHandlerException()
    {
        super();
    }

    /**
     * @param message
     */
    public DuplicateHandlerException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public DuplicateHandlerException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateHandlerException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
