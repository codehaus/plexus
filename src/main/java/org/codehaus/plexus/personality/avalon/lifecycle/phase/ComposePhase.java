package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ComposePhase
    extends AbstractPhase
{
    private static final String COMPONENT_MANAGER_ROLE =
        ComponentManager.class.getName();

    public void execute( Object object, org.codehaus.plexus.component.manager.ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Composable )
        {
            PlexusContainer container = manager.getContainer();

            ComponentManager avalonComponentManager = (ComponentManager) container.lookup( COMPONENT_MANAGER_ROLE );

            if ( null == avalonComponentManager )
            {
                final String message = "ComponentManager is null";

                throw new IllegalArgumentException( message );
            }

            ( (Composable) object ).compose( avalonComponentManager );
        }
    }
}
