package org.codehaus.plexus.spe.apps.client;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnection;
import org.codehaus.plexus.spe.rmi.client.ProcessEngineClient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SpeClient
{
    private Embedder embedder;

    private Map<String, Object> instances = new HashMap<String, Object>();

    public SpeClient()
        throws Exception
    {
        embedder = new Embedder();

        embedder.start();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public ProcessEngineConnection getConnection()
        throws RemoteException, NotBoundException, ProcessException
    {
        System.err.println( "Connecting to server..." );

        ProcessEngineClient client = new ProcessEngineClient( "localhost", Registry.REGISTRY_PORT );

        ProcessEngineConnection connection = client.getConnector().getConnection();

        System.out.println( "Connected!" );

        return connection;
    }

    public Prompter getPrompter()
        throws Exception
    {
        return (Prompter) lookup( Prompter.ROLE );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private Object lookup( String role )
        throws Exception
    {
        Object instance = instances.get( role );

        if ( instance == null )
        {
            instance = embedder.lookup( role );

            instances.put( role, instance );
        }

        return instance;
    }
}
