package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentManagerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        setupCoreComponent( "component-manager-manager", context );

        context.getContainer().getComponentManagerManager().setLifecycleHandlerManager( context.getContainer().getLifecycleHandlerManager() );
    }
}
