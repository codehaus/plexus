package org.codehaus.plexus.rmi;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultRmiService
    extends AbstractLogEnabled
    implements RmiService, Startable
{
    /**
     * @plexus.configuration
     */
    private int rmiRegistryPort = 1051;

    private Registry registry;

    // -----------------------------------------------------------------------
    // RmiService Implementation
    // -----------------------------------------------------------------------

    public synchronized Registry getRegistry()
        throws RmiServiceException
    {
        if ( registry != null )
        {
            return registry;
        }

        getLogger().info( "Starting in-JVM RMI registry on port " + rmiRegistryPort + "." );

        try
        {
            return registry = LocateRegistry.createRegistry( rmiRegistryPort );
        }
        catch ( RemoteException e )
        {
            throw new RmiServiceException( "Error while starting RMI registry.", e );
        }
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void start()
        throws StartingException
    {
        getLogger().info( "Starting RMI Service." );
    }

    public void stop()
        throws StoppingException
    {
        getLogger().info( "Stopping RMI Service." );
    }
}
