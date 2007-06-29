package org.codehaus.cargo.container.plexus;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;

/**
 * Default capabilities of the standalone local container
 * @author eredmond
 */
public class PlexusStandaloneLocalConfigurationCapability
    extends AbstractStandaloneLocalConfigurationCapability
{
    private Map propertySupportMap;

    public PlexusStandaloneLocalConfigurationCapability()
    {
        super();

        propertySupportMap = new HashMap();

        propertySupportMap.put( GeneralPropertySet.HOSTNAME, Boolean.FALSE );
        propertySupportMap.put( GeneralPropertySet.JVMARGS, Boolean.FALSE );
    }

    protected Map getPropertySupportMap()
    {
        return propertySupportMap;
    }
}
