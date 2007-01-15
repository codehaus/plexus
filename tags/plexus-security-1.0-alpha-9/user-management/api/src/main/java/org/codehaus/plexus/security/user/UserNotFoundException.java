package org.codehaus.plexus.security.user;

/**
 * @author Jason van Zyl
 */
public class UserNotFoundException
    extends Exception
{
    public UserNotFoundException( String string )
    {
        super( string );
    }

    public UserNotFoundException( String string,
                                  Throwable throwable )
    {
        super( string, throwable );
    }

    public UserNotFoundException( Throwable throwable )
    {
        super( throwable );
    }
}
