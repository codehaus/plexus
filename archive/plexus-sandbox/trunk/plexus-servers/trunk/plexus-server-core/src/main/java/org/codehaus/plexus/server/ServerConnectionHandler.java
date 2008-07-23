package org.codehaus.plexus.server;

import java.net.Socket;

/** @author Jason van Zyl */
public interface ServerConnectionHandler
{
    String ROLE = ServerConnectionHandler.class.getName();

    void handleConnection( Socket socket )
        throws ConnectionHandlingException;
}
