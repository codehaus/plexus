package org.codehaus.plexus.ircd;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.synapse.SynapseServer;
import org.codehaus.plexus.ircd.server.IrcServer;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class IrcServerTest
    extends PlexusTestCase
{
    public IrcServerTest( String name )
    {
        super( name );
    }

    public void testIrcd()
        throws Exception
    {
        SynapseServer server = (SynapseServer) lookup( IrcServer.ROLE );

        assertNotNull( server );
/*
        while( true )
        {
        }
*/
    }
}
