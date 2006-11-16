package org.codehaus.plexus.rmi;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultRmiService
    extends AbstractLogEnabled
    implements RmiService, Startable
{
    /**
     * @plexus.configuration default-value = "1051"
     */
    private int rmiRegistryPort = 1051;

    /**
     * @plexus.configuration default-value = "9999"
     */
    private int exportedObjectPort = 9999;

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

    public Remote exportObject( Remote remote )
        throws RmiServiceException
    {
        try
        {
            getLogger().info( "Exporting " + remote.getClass().getName() + " on port " + exportedObjectPort );

            return UnicastRemoteObject.exportObject( remote, exportedObjectPort );
        }
        catch ( RemoteException e )
        {
            throw new RmiServiceException( "Error while exporting remote object.", e );
        }
    }

    public Remote exportObject( Remote remote, String name )
        throws RmiServiceException
    {
        Remote exportedRemote = exportObject( remote );

        try
        {
            getLogger().info( "Binding " + remote.getClass().getName() + " to " + name );

            getRegistry().bind( name, exportedRemote );

            return exportedRemote;
        }
        catch ( RemoteException e )
        {
            throw new RmiServiceException( "Error while binding remote object " + remote.getClass().getName() +
                " to '" + name + "'.", e );
        }
        catch ( AlreadyBoundException e )
        {
            throw new RmiServiceException( "Error while binding remote object " + remote.getClass().getName() +
                " to '" + name + "'.", e );
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
