package org.codehaus.plexus.synapse.handler;

import org.codehaus.plexus.synapse.reactor.Reactor;

import java.net.Socket;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ServiceHandler
{
    String getHandleKey();

    void handleEvents()
        throws Exception;

    void handleEvent( Socket socket )
        throws Exception;

    void setReactor( Reactor reactor );

    Reactor getReactor();
}
