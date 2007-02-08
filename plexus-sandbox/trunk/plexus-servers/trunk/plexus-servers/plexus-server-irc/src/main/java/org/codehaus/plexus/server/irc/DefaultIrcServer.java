package org.codehaus.plexus.server.irc;

import org.codehaus.plexus.server.irc.user.HandleUser;
import org.codehaus.plexus.server.irc.user.User;
import org.codehaus.plexus.server.DefaultServer;
import org.codehaus.plexus.server.ConnectionHandlingException;

import java.net.Socket;
import java.io.IOException;

/**
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultIrcServer
    extends DefaultServer
    implements IrcServer
{
    public void dispose()
    {
        HandleUser.disconnectAll();
    }

    public void handleConnection( Socket socket )
        throws ConnectionHandlingException
    {
        try
        {
            User user = new User( socket );

            user.startProcess();
        }
        catch ( IOException e )
        {
            throw new ConnectionHandlingException( "Error processing request.", e );
        }
    }
}
