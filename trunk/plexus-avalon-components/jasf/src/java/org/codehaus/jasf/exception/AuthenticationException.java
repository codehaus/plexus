package org.codehaus.jasf.exception;

/**
 * EntityAuthenticationException.java
 * 
 * @author Dan Diephouse
 * @since Nov 24, 2002 
 */
public class AuthenticationException extends Exception
{
    /**
     * Constructor EntityAuthenticationException.
     */
    public AuthenticationException()
    {
        super();
    }
    
    /**
     * Constructor EntityAuthenticationException.
     * @param string
     */
    public AuthenticationException(String message)
    {
        super(message);
    }

    /**
     * Constructor EntityAuthenticationException.
     * @param string
     */
    public AuthenticationException(String message, Exception e)
    {
        super(message, e);
    }


}
