package org.codehaus.plexus.smtp;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.synapse.SynapseServer;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultSmtpServerTest
    extends PlexusTestCase
{
    public DefaultSmtpServerTest( String name )
    {
        super( name );
    }

    public void testSmtpServer()
        throws Exception
    {
        SynapseServer server = (SynapseServer) lookup( SmtpServer.ROLE );

        assertNotNull( server );

        while( true )
        {
        }
    }
}