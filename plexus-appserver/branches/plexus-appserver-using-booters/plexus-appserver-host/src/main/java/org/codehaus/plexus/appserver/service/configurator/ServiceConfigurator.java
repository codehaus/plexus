package org.codehaus.plexus.appserver.service.configurator;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public interface ServiceConfigurator
{
    String ROLE = ServiceConfigurator.class.getName();

    Object configure( Object configurationObject, PlexusConfiguration serviceConfiguration, ClassRealm realm )
        throws ComponentConfigurationException;
}
