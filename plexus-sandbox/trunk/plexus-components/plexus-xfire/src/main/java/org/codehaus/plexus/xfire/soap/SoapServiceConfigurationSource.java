package org.codehaus.plexus.xfire.soap;

/**
 * @author Jason van Zyl
 */
public interface SoapServiceConfigurationSource
{
    public static final String ROLE = SoapServiceConfigurationSource.class.getName();
    
    SoapServiceConfiguration getConfiguration()
        throws SoapServiceConfigurationSourceRetrievalException;
}
