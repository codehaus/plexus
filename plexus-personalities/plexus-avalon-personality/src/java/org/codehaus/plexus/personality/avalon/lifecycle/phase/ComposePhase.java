package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonLifecycleHandler;

public class ComposePhase
    extends AbstractPhase
{
    public void execute( Object object, org.codehaus.plexus.component.manager.ComponentManager manager )
        throws Exception
    {
        ComponentManager avalonComponentManager = (ComponentManager)
            manager.getLifecycleHandler().getEntities().get( AvalonLifecycleHandler.COMPONENT_MANAGER );

        if ( object instanceof Composable )
        {
            if ( null == avalonComponentManager )
            {
                final String message = "ComponentManager is null";

                throw new IllegalArgumentException( message );
            }

            ( (Composable) object ).compose( avalonComponentManager );
        }
    }
}
