package org.codehaus.plexus.synapse;

import org.codehaus.plexus.synapse.handler.ServiceHandler;
import org.codehaus.plexus.synapse.reactor.Reactor;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface SynapseServer
{
    static String ROLE = SynapseServer.class.getName();

    void registerServiceHandler( ServiceHandler serviceHandler );

    Reactor getReactor();
}
