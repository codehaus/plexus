package org.codehaus.plexus.summit.exception;

public class SummitException
    extends Exception
{
    public SummitException( String message )
    {
        super( message );
    }

    public SummitException( String message, Throwable throwable )
    {
        super( message, throwable );
    }
}
