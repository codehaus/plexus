package org.codehaus.plexus.xfire;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.service.Service;

/**
 * Builds a service of a specified type from a xfire
 * configuration.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public interface Configurator
{
    String ROLE = Configurator.class.getName();

    public Service createService( PlexusConfiguration config ) 
        throws Exception;
}
