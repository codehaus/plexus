package org.codehaus.plexus.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public interface PlexusServerSocketFactory
{
    String ROLE = PlexusServerSocketFactory.class.getName();

    public ServerSocket createServerSocket( int port,
                                            int backlog,
                                            String host )
        throws IOException;

    public ServerSocket createServerSocket( int port,
                                            int backlog,
                                            InetAddress host )
        throws IOException;
}
