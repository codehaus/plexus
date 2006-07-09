package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentDiscovererManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        setupCoreComponent( "component-discoverer-manager", context );

        context.getContainer().getComponentDiscovererManager().initialize();
    }
}
