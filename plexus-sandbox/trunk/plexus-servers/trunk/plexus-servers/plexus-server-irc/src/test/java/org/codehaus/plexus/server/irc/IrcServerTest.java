package org.codehaus.plexus.server.irc;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.synapse.SynapseServer;
import org.codehaus.plexus.server.irc.server.IrcServer;

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
