package org.codehaus.plexus.pipeline;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPipeline
    extends AbstractLogEnabled
    implements Pipeline, Startable, Serviceable
{
    /**
     * @plexus.configuration
     */
    private List valveRoleHints;

    private ServiceLocator serviceLocator;

    /**
     * @plexus.configuration
     */
    private List valveInstances;

    // -----------------------------------------------------------------------
    // Pipeline Implementation
    // -----------------------------------------------------------------------

    public void processMessage( Map context )
        throws PipelineException
    {
        processMessage( context, new RuntimeExceptionHandler() );
    }

    public void processMessage( Map context, ExceptionHandler exceptionHandler )
        throws PipelineException
    {
        // -----------------------------------------------------------------------
        // Invoke all the valveRoleHints in the pipeline
        // -----------------------------------------------------------------------

        for ( Iterator it = valveInstances.iterator(); it.hasNext(); )
        {
            Valve valve = (Valve) it.next();

            try
            {
                ValveReturnCode returnCode = valve.invoke( context );

                if ( returnCode.equals( Valve.PROCEED ) )
                {
                    // continue
                }
                else if ( returnCode.equals( Valve.STOP ) )
                {
                    break;
                }
                else
                {
                    throw new PipelineException( "Unknown return code. This pipeline cannot handle this type: " +
                        returnCode.toString() );
                }
            }
            catch ( Exception e )
            {
                exceptionHandler.handleException( e );
            }
        }
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void service( ServiceLocator serviceLocator )
    {
        this.serviceLocator = serviceLocator;
    }

    public void start()
        throws StartingException
    {
        valveInstances = new ArrayList();

        if ( valveRoleHints == null )
        {
            valveRoleHints = new ArrayList();
        }

        if ( valveRoleHints.size() == 0 )
        {
            getLogger().warn( "Possibly misconfigured: The pipline has no steps!" );

            return;
        }

        try
        {
            for ( Iterator it = valveRoleHints.iterator(); it.hasNext(); )
            {
                String valve = (String) it.next();

                valveInstances.add( (Valve) serviceLocator.lookup( Valve.class.getName(), valve ) );
            }
        }
        catch ( ComponentLookupException e )
        {
            stop();

            throw new StartingException( "Error while looking up the valves.", e );
        }
    }

    public void stop()
    {
        for ( Iterator it = valveInstances.iterator(); it.hasNext(); )
        {
            Valve valve = (Valve) it.next();

            try
            {
                serviceLocator.release( valve );
            }
            catch ( ComponentLifecycleException e )
            {
                getLogger().warn( "Error while releasing component '" + valve + "'.", e );
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    protected void handleException( Exception e )
    {
        e.printStackTrace( System.out );
    }
}
