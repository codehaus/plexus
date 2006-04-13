/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.synapse;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.synapse.acceptor.SocketAcceptor;
import org.codehaus.plexus.synapse.handler.ServiceHandler;
import org.codehaus.plexus.synapse.reactor.Reactor;

public abstract class AbstractSynapseServer
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, Startable, Disposable, SynapseServer
{
    private Reactor reactor;

    private SocketAcceptor socketAcceptor;

    // ----------------------------------------------------------------------
    // API
    // ----------------------------------------------------------------------

    public void registerServiceHandler( ServiceHandler serviceHandler )
    {
        reactor.registerServiceHandler( serviceHandler );
    }

    public Reactor getReactor()
    {
        return reactor;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** @see Serviceable#service */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        reactor = (Reactor) serviceManager.lookup( Reactor.ROLE );

        socketAcceptor = (SocketAcceptor) serviceManager.lookup( SocketAcceptor.ROLE );
    }

    /** @see Configurable#configure */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }

    /** @see Initializable#initialize */
    public void initialize()
        throws Exception
    {
        reactor.registerServiceHandler( socketAcceptor );
    }

    /** @see Startable#start */
    public void start()
        throws Exception
    {
        // Start the reactors main event handling loop.
        reactor.handleEvents();
    }

    /** @see Startable#stop */
    public void stop()
        throws Exception
    {
    }
}


