package org.codehaus.plexus.rememberme;

/**
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class UsernameNotFoundException
    extends Exception
{
    public UsernameNotFoundException( String message )
    {
        super( message );
    }

    public UsernameNotFoundException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}