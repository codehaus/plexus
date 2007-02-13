package org.codehaus.plexus.discovery;

/** @author Jason van Zyl */
public class ResourceDeregistrationException
    extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = 4735344524670871427L;

    public ResourceDeregistrationException( String string )
    {
        super( string );
    }

    public ResourceDeregistrationException( String string,
                                            Throwable throwable )
    {
        super( string, throwable );
    }

    public ResourceDeregistrationException( Throwable throwable )
    {
        super( throwable );
    }
}
