package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeLifecycleHandlerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        setupCoreComponent( "lifecycle-handler-manager", context );

        context.getContainer().getLifecycleHandlerManager().initialize();
    }
}
