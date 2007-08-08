package org.codehaus.plexus.redback.password;

public class UnsupportedAlgorithmException
    extends Exception
{

    public UnsupportedAlgorithmException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public UnsupportedAlgorithmException( String message )
    {
        super( message );
    }

}
