package org.codehaus.plexus.redback.authentication;

/**
 * HttpAuthenticationException
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class HttpAuthenticationException
    extends AuthenticationException
{

    public HttpAuthenticationException()
    {
        super();
    }

    public HttpAuthenticationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public HttpAuthenticationException( String message )
    {
        super( message );
    }

    public HttpAuthenticationException( Throwable cause )
    {
        super( cause );
    }

}
