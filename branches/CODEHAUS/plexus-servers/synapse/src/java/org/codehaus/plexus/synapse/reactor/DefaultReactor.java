package org.codehaus.plexus.synapse.reactor;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.synapse.handler.ServiceHandler;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsibilities of the Reactor:
 *
 *  -> Register and removes event handlers and their associated handles.
 *  -> Manages a handle set.
 *  -> Run's the applications event loop.
 *
 * The reactor pattern 'inverts' teh flow of control within an application. it is
 * the responsibility of a reactor, not the application, to wait for indication events,
 * demultiplex these events to their concrete event handlers and dispatch the appropriate
 * hook method on the concrete event handler. In particular, a reactor is not called by a
 * concrete event handler but instead a reactor dispatches concrete event handlers, which
 * react to the occurence of a specific event. This 'inversion of control', is known as
 * the Hollywood principle as coined by John Vlissides in Pattern Hatching.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultReactor
    extends AbstractLogEnabled
    implements Reactor
{
    private Map serviceHandlers;

    public DefaultReactor()
    {
        serviceHandlers = new HashMap();
    }

    /**
     * This is the reactors event loop.
     */
    public void handleEvents()
        throws Exception
    {
        ServiceHandler serviceHandler = (ServiceHandler) serviceHandlers.get( "ACCEPT" );

        serviceHandler.handleEvents();
    }

    public void handleEvent( Socket socket )
        throws Exception
    {
        ServiceHandler serviceHandler = (ServiceHandler) serviceHandlers.get( "PROCESS" );

        serviceHandler.handleEvent( socket );
    }

    public void registerServiceHandler( ServiceHandler serviceHandler )
    {
        serviceHandler.setReactor( this );

        serviceHandlers.put( serviceHandler.getHandleKey(), serviceHandler );
    }

    public void removeServiceHandler( ServiceHandler serviceHandler )
    {
        serviceHandlers.remove( serviceHandler.getHandleKey() );
    }
}
