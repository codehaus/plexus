package org.codehaus.plexus.xfire.soap;

import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

/**
 * @author Jason van Zyl
 * 
 * @plexus.component role="org.codehaus.xfire.service.ServiceFactory" role-hint="default"
 */
public class PlexusServiceFactory
    extends ObjectServiceFactory
{
    /**
     * @plexus.requirement
     */
    private TransportManager transportManager;
}
