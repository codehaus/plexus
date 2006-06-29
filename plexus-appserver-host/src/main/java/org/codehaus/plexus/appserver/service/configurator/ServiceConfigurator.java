package org.codehaus.plexus.appserver.service.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.classworlds.ClassRealm;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public interface ServiceConfigurator
{
    String ROLE = ServiceConfigurator.class.getName();

    Object configure( Object configurationObject,
                      PlexusConfiguration serviceConfiguration,
                      ClassRealm realm )
        throws ComponentConfigurationException;
}
