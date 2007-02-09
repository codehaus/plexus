package org.codehaus.plexus.server.irc;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.server.irc.IrcServer;
import org.codehaus.plexus.server.Server;

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
    public void testIrc()
        throws Exception
    {
        Server server = (Server) lookup( IrcServer.ROLE );

        assertNotNull( server );
    }
}
