package org.codehaus.plexus.smtp;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.server.Server;

public class DefaultSmtpServerTest
    extends PlexusTestCase
{
    public void testSmtpServer()
        throws Exception
    {
        Server server = (Server) lookup( SmtpServer.ROLE );

        assertNotNull( server );
    }
}
