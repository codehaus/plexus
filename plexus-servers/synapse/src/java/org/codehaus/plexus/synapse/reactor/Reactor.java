package org.codehaus.plexus.synapse.reactor;

import org.codehaus.plexus.synapse.handler.ServiceHandler;

import java.net.Socket;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Reactor
{
    static String ROLE = Reactor.class.getName();

    public void handleEvents()
        throws Exception;

    public void handleEvent( Socket socket )
        throws Exception;

    public void registerServiceHandler( ServiceHandler serviceHandler );

    public void removeServiceHandler( ServiceHandler serviceHandler );

}
