package org.codehaus.plexus.xfire.soap;

/**
 * @author Jason van Zyl
 */
public class SoapServiceConfigurationSourceRetrievalException
    extends Exception
{
    public SoapServiceConfigurationSourceRetrievalException( String id )
    {
        super( id );
    }

    public SoapServiceConfigurationSourceRetrievalException( String id,
                                                             Throwable throwable )
    {
        super( id, throwable );
    }

    public SoapServiceConfigurationSourceRetrievalException( Throwable throwable )
    {
        super( throwable );
    }
}
