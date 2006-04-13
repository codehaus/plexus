package org.apache.plexus;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
  * Base class of runtime exceptions thrown within plexus
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class PlexusRuntimeException extends NestableRuntimeException
{

    /**
     * 
     */
    public PlexusRuntimeException()
    {
        super();
    }

    /**
     * @param message
     */
    public PlexusRuntimeException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public PlexusRuntimeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public PlexusRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
