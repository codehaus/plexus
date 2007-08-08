package org.codehaus.plexus.redback.password.hash;

public class PasswordHashException
    extends Exception
{

    public PasswordHashException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public PasswordHashException( String message )
    {
        super( message );
    }

}
