package org.codehaus.plexus.server;

import java.net.Socket;
import java.net.InetAddress;

/** @author Jason van Zyl */
public interface Server
{
    String ROLE = Server.class.getName();

    void handleConnection( Socket socket );

    InetAddress getServerAddress();    
}
