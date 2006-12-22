package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ServicePhase
    extends AbstractPhase
{
    private static final String SERVICE_MANAGER_ROLE =
        ServiceManager.class.getName();

    public void execute( Object object, ComponentManager manager
                         )
        throws PhaseExecutionException
    {
        if ( object instanceof Serviceable )
        {
            PlexusContainer container = manager.getContainer();

            ServiceManager serviceManager = null;
            try
            {
                serviceManager = (ServiceManager) container.lookup( SERVICE_MANAGER_ROLE );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "lookup threw ComponentLookupException", e );
            }

            if ( null == serviceManager )
            {
                final String message = "ServiceManager is null";

                throw new IllegalArgumentException( message );
            }

            try
            {
                ( (Serviceable) object ).service( serviceManager );
            }
            catch ( ServiceException e )
            {
                throw new PhaseExecutionException( "service threw ServiceException", e );                
            }
        }
    }
}
