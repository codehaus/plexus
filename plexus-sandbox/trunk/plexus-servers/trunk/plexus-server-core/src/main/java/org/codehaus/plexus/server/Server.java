package org.codehaus.plexus.server;

import java.net.InetAddress;
import java.net.Socket;

/** @author Jason van Zyl */
public interface Server
{
    String ROLE = Server.class.getName();

    void handleConnection( Socket socket )
        throws ConnectionHandlingException;

    InetAddress getServerAddress();

    String getServerHost();
}
