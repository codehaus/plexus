package org.codehaus.plexus.server;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;

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
