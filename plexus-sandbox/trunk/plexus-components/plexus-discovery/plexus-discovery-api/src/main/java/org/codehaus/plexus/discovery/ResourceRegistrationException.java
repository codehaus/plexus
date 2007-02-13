package org.codehaus.plexus.discovery;

/** @author Jason van Zyl */
public class ResourceRegistrationException
    extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = -7702234586850324155L;

    public ResourceRegistrationException( String string )
    {
        super( string );
    }

    public ResourceRegistrationException( String string,
                                          Throwable throwable )
    {
        super( string, throwable );
    }

    public ResourceRegistrationException( Throwable throwable )
    {
        super( throwable );
    }
}
