package org.codehaus.plexus.xfire.soap;

/**
 * @author Jason van Zyl
 */
public interface SoapServiceConfiguration
{
    String getName();

    String getNamespace();

    String getStyle();

    String getUse();

    String getScope();

    String getSoapVersion();

    String getWsdlUrl();
}
