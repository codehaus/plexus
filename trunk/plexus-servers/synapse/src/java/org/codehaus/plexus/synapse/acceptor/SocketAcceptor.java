package org.codehaus.plexus.synapse.acceptor;

import org.codehaus.plexus.synapse.handler.ServiceHandler;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface SocketAcceptor
    extends ServiceHandler
{
    static String ROLE = SocketAcceptor.class.getName();
}
