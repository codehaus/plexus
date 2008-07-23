package org.codehaus.plexus.security.exception;

/**
 * EntityAuthenticationException.java
 *
 * @author Dan Diephouse
 * @since Nov 24, 2002
 */
public class AuthenticationException
    extends Exception
{
    /**
     * Constructor EntityAuthenticationException.
     */
    public AuthenticationException()
    {
    }

    /**
     * Constructor EntityAuthenticationException.
     *
     * @param message
     */
    public AuthenticationException( String message )
    {
        super( message );
    }

    /**
     * Constructor EntityAuthenticationException.
     *
     * @param message
     * @param cause
     */
    public AuthenticationException( String message, Exception cause )
    {
        super( message, cause );
    }
}
