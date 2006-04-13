package org.apache.plexus;

import org.apache.commons.lang.exception.NestableException;

/**
  * Base class of checked exceptions thrown from within plexus.
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PlexusException extends  NestableException
{

    /**
     * 
     */
    public PlexusException()
    {
        super();
    }

    /**
     * @param message
     */
    public PlexusException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public PlexusException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PlexusException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
