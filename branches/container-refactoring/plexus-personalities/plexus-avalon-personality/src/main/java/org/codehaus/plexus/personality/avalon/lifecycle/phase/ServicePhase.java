package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ServicePhase
    extends AbstractPhase
{
    private static final String SERVICE_MANAGER_ROLE =
        ServiceManager.class.getName();

    public void execute( Object object, ComponentManager manager
                         )
        throws Exception
    {
        if ( object instanceof Serviceable )
        {
            PlexusContainer container = manager.getContainer();

            ServiceManager serviceManager = (ServiceManager) container.lookup( SERVICE_MANAGER_ROLE );

            if ( null == serviceManager )
            {
                final String message = "ServiceManager is null";

                throw new IllegalArgumentException( message );
            }

            ( (Serviceable) object ).service( serviceManager );
        }
    }
}
