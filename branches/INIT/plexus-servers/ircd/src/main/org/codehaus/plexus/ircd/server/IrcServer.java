package org.codehaus.plexus.ircd.server;

import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.user.User;
import org.codehaus.plexus.synapse.AbstractSynapseServer;
import org.codehaus.plexus.synapse.handler.AbstractServiceHandler;

import java.net.Socket;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class IrcServer
    extends AbstractSynapseServer
{
    /** @see org.apache.avalon.framework.activity.Initializable#initialize */
    public void initialize()
        throws Exception
    {
        super.initialize();

        getReactor().registerServiceHandler( new IrcServiceHandler() );
    }

    /** @see org.apache.avalon.framework.activity.Disposable#dispose() */
    public void dispose()
    {
        HandleUser.disconnectAll();
    }

    static class IrcServiceHandler
        extends AbstractServiceHandler
    {
        public String getHandleKey()
        {
            return "PROCESS";
        }

         public void handleEvents()
            throws Exception
         {
         }

        public void handleEvent( Socket socket )
            throws Exception
        {
            User user = new User( socket );

            user.startProcess();
        }
    }
}
