package org.codehaus.plexus.xfire.soap;

import org.codehaus.xfire.service.Service;

/**
 * @author Jason van Zyl
 */
public interface SoapServiceCreator
{
    String ROLE = SoapServiceCreator.class.getName();

    Service createService( String role )
        throws SoapServiceCreationException;
}
