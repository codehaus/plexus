package org.codehaus.plexus.spe.rmi.client;

import org.codehaus.plexus.spe.rmi.ProcessEngineConnector;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessEngineClient
{
    private Registry registry;

    private ProcessEngineConnector connector;

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public ProcessEngineClient( String hostname, int port )
        throws RemoteException, NotBoundException
    {
        registry = LocateRegistry.getRegistry( hostname, port );

        connector = (ProcessEngineConnector) registry.lookup( ProcessEngineConnector.class.getName() );
    }

    public ProcessEngineConnector getConnector()
    {
        return connector;
    }
}
