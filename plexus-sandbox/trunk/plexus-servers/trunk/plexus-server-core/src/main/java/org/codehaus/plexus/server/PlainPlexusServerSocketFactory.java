package org.codehaus.plexus.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * @plexus.component
 */
public class PlainPlexusServerSocketFactory
    implements PlexusServerSocketFactory
{
    public ServerSocket createServerSocket( int port,
                                            int backlog,
                                            String host )
        throws IOException
    {
        return createServerSocket( port, backlog, InetAddress.getByName( host ) );
    }

    public ServerSocket createServerSocket( int port,
                                            int backlog,
                                            InetAddress host )
        throws IOException
    {
        return new ServerSocket( port, backlog, host );
    }

}
