package org.apache.ftpserver;

/*
 * LICENSE
 */

import java.net.URL;

import org.apache.ftpserver.interfaces.FtpServerInterface;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class FtpServerImplTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        FtpServerInterface server = (FtpServerInterface)lookup( FtpServerInterface.ROLE );

        URL url = new URL( "ftp", "localhost", 10021, "test-file.txt" );

        String contents = IOUtil.toString( url.openStream() );

        assertEquals( "Hello World!", contents );

        release( server );
    }
}
