package org.codehaus.plexus.xfire.soap;

/**
 * @author Jason van Zyl
 */
public class SoapServiceCreationException
    extends Exception
{
    public SoapServiceCreationException( String id )
    {
        super( id );
    }

    public SoapServiceCreationException( String id,
                                         Throwable throwable )
    {
        super( id, throwable );
    }

    public SoapServiceCreationException( Throwable throwable )
    {
        super( throwable );
    }
}
