package org.codehaus.plexus.rmi;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.rmi.test.DefaultMyService;
import org.codehaus.plexus.rmi.test.MyService;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RmiServiceTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        RmiService rmiService = (RmiService) lookup( RmiService.ROLE );

        Registry registry = rmiService.getRegistry();

        registry.bind( "service", new DefaultMyService() );

        pretentToBeClient();
    }

    private void pretentToBeClient()
        throws Exception
    {
        Registry registry = LocateRegistry.getRegistry( "localhost", 9999 );

        MyService service = (MyService) registry.lookup( "service" );

        System.out.println( "service.getClass().getName() = " + service.getClass().getName() );

        assertEquals( "YES!!", service.partyTime() );
    }
}
