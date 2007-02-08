package org.codehaus.plexus.server.ftp;

import java.net.URL;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class FtpServerTest
    extends PlexusTestCase
{
    public void testSimpleLookup()
        throws Exception
    {
        FtpServer server = (FtpServer) lookup( FtpServer.ROLE );

        release( server );
    }

    public void testFileRetrieval()
        throws Exception
    {
        FtpServer server = (FtpServer) lookup( FtpServer.ROLE );

        URL url = new URL( "ftp", "localhost", 10021, "test-file.txt" );

        String contents = IOUtil.toString( url.openStream() );

        assertEquals( "Hello World!", contents );

        release( server );
    }
}
