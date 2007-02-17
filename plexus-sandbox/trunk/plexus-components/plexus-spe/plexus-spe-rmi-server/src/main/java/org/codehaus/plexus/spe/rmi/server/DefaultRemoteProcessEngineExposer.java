package org.codehaus.plexus.spe.rmi.server;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.rmi.RmiService;
import org.codehaus.plexus.rmi.RmiServiceException;
import org.codehaus.plexus.spe.ProcessService;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnector;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultRemoteProcessEngineExposer
    extends AbstractLogEnabled
    implements RemoteProcessEngineExposer, Initializable, Startable
{
    /**
     * @plexus.requirement
     */
    private RmiService rmiService;

    /**
     * @plexus.requirement
     */
    private ProcessService processService;

    private ProcessEngineConnectorServer server;

    // -----------------------------------------------------------------------
    // RemoteProcessEngineExposer Implementation
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        server = new ProcessEngineConnectorServer( processService, getLogger() );
    }

    public void start()
        throws StartingException
    {
        try
        {
            rmiService.exportObject( server, ProcessEngineConnector.class.getName() );
        }
        catch ( RmiServiceException e )
        {
            throw new StartingException( "Error while exposing connector.", e );
        }
    }

    public void stop()
        throws StoppingException
    {
    }
}
