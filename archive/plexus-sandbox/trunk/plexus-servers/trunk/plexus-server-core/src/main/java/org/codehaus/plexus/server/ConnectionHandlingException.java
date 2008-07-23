package org.codehaus.plexus.server;

/** @author Jason van Zyl */
public class ConnectionHandlingException
    extends Exception
{
    public ConnectionHandlingException( String string )
    {
        super( string );
    }

    public ConnectionHandlingException( String string,
                                        Throwable throwable )
    {
        super( string, throwable );
    }

    public ConnectionHandlingException( Throwable throwable )
    {
        super( throwable );
    }
}
