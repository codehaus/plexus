package org.codehaus.plexus.service.jetty.configuration;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ServiceConfigurationBuilder
{
    ServiceConfiguration buildConfiguration( PlexusConfiguration serviceConfiguration );
}
