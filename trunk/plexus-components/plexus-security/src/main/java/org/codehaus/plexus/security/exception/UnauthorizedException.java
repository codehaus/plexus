package org.codehaus.plexus.security.exception;

/**
 * The exception thrown if an entity is unauthorized to access a resource.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public class UnauthorizedException
    extends Exception
{
    /**
     * @param message
     */
    public UnauthorizedException( String message )
    {
        super( message );
    }
}
