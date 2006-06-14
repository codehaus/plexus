package org.codehaus.plexus;

import org.codehaus.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.lifecycle.phase.Monitorable;
import org.codehaus.plexus.lifecycle.phase.Executable;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class DefaultPopulator
    implements Populator, Configurable, Monitorable, Executable
{
    boolean configured;

    boolean monitored;

    boolean executed;

    public void configure( PlexusConfiguration configuration )
    {
        configured = true;
    }

    public void monitor( PlexusContainer container )
    {
        monitored = true;
    }

    public void execute()
    {
        executed = true;
    }

    public boolean isConfigured()
    {
        return configured;
    }

    public boolean isMonitored()
    {
        return monitored;
    }

    public boolean isExecuted()
    {
        return executed;
    }
}
