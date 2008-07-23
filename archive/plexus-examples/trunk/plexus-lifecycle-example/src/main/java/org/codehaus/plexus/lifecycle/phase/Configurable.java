package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public interface Configurable
{
    void configure( PlexusConfiguration configuration );
}
