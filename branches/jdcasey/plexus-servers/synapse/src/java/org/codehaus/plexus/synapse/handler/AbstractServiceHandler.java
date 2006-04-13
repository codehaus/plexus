package org.codehaus.plexus.synapse.handler;

import org.codehaus.plexus.synapse.reactor.Reactor;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.net.Socket;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractServiceHandler
    extends AbstractLogEnabled
    implements ServiceHandler
{
    private Reactor reactor;

    public void setReactor( Reactor reactor )
    {
        this.reactor = reactor;
    }

    public Reactor getReactor()
    {
        return reactor;
    }

    public abstract void handleEvents()
        throws Exception;

    public abstract void handleEvent( Socket socket )
        throws Exception;

    public abstract String getHandleKey();
}
