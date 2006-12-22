package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ComposePhase
    extends AbstractPhase
{
    private static final String COMPONENT_MANAGER_ROLE =
        ComponentManager.class.getName();

    public void execute( Object object, org.codehaus.plexus.component.manager.ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Composable )
        {
            PlexusContainer container = manager.getContainer();

            ComponentManager avalonComponentManager = null;
            try
            {
                avalonComponentManager = (ComponentManager) container.lookup( COMPONENT_MANAGER_ROLE );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "lookup threw ComponentLookupException", e );
            }

            if ( null == avalonComponentManager )
            {
                final String message = "ComponentManager is null";

                throw new IllegalArgumentException( message );
            }

            try
            {
                ( (Composable) object ).compose( avalonComponentManager );
            }
            catch ( ComponentException e )
            {
                throw new PhaseExecutionException( "compose threw ComponentException", e );
            }
        }
    }
}
