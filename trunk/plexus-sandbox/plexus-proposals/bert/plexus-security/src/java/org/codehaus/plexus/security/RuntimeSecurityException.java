package org.codehaus.plexus.security;

import org.codehaus.plexus.PlexusRuntimeException;
import org.codehaus.plexus.util.ExceptionIDGenerator;

/**
  * The base exception which all security related exceptions should extend. This is a runtime
  * exception so it can be applied transparently without appearing in business level code, though
  * a few business exception may be interested in catching this.
  * 
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RuntimeSecurityException extends PlexusRuntimeException
{
	/** Flag indicating whether this exception has been logged. */
	private boolean isLogged = false;
	
	/** Unique id of this exception */
	private String id;
	
    /**
     * Constructor. Generate a unique id.
     */
    public RuntimeSecurityException()
    {
        super();
        this.id = ExceptionIDGenerator.generateId();
        isLogged = false;
    }

    /**
     * @param message
     */
    public RuntimeSecurityException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public RuntimeSecurityException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RuntimeSecurityException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public boolean isLogged()
    {
    	return isLogged;
    }
    
    /**
     * Set the flag indicating this session is logged.
     * 
     * @param b
     */
    public void setLogged(boolean b)
    {
    	isLogged = b;
    }
    
    /**
     * Return the unique id of this exception
     * 
     * @return
     */
    public String getId()
    {
    	return id;
    }

}
