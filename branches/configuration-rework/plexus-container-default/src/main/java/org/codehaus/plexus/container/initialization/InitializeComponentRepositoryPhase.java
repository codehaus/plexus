package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentRepositoryPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        setupCoreComponent( "component-repository", context );

        context.getContainer().getComponentRepository().configure( context.getContainerConfiguration() );

        context.getContainer().getComponentRepository().setClassRealm( context.getContainer().getContainerRealm() );

        try
        {
            context.getContainer().getComponentRepository().initialize();
        }
        catch ( ComponentRepositoryException e )
        {
            throw new ContainerInitializationException( "Error initializing component repository.", e );
        }
    }
}
