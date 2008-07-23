package org.codehaus.plexus.server;

import java.net.InetAddress;
import java.net.Socket;

/** @author Jason van Zyl */
public interface Server
    extends ServerConnectionHandler
{
    String ROLE = Server.class.getName();

    InetAddress getServerAddress();

    String getServerHost();
}
