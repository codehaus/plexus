package org.codehaus.plexus.xfire.soap;

/**
 * @author Jason van Zyl
 */
public interface SoapServiceConfigurationSource
{
    SoapServiceConfiguration getConfiguration()
        throws SoapServiceConfigurationSourceRetrievalException;
}
