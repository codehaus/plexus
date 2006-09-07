package org.codehaus.plexus.security.user.policy;

/**
 * Password Encoding Exception.
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class PasswordEncodingException
    extends RuntimeException
{
    public PasswordEncodingException()
    {
        super();
    }

    public PasswordEncodingException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public PasswordEncodingException( String message )
    {
        super( message );
    }

    public PasswordEncodingException( Throwable cause )
    {
        super( cause );
    }
}
