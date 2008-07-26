package org.codehaus.plexus.rmi;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.rmi.test.MyRemoteService;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

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

        MyRemoteService remoteService = (MyRemoteService) lookup( MyRemoteService.ROLE );

        rmiService.exportObject( remoteService, "service" );

        pretentToBeClient();
    }

    private void pretentToBeClient()
        throws Exception
    {
        Registry registry = LocateRegistry.getRegistry( "localhost", 9999 );

        MyRemoteService service = (MyRemoteService) registry.lookup( "service" );

        System.out.println( "service.getClass().getName() = " + service.getClass().getName() );

        assertEquals( "YES!!", service.partyTime() );
    }
}
