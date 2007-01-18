package org.codehaus.plexus.pipeline;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.pipeline.execution.PipelineExecutor;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is NOT annotated as a Plexus component as should always be configured in applications using it.
 *
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPipelineService
    extends AbstractLogEnabled
    implements PipelineService, Startable, Serviceable
{
    /**
     * @plexus.requirement
     */
    private PipelineExecutor executor;

    /**
     * @plexus.configuration
     */
    private List pipelines;

    private final Object lock = new Object();

    private ServiceLocator serviceLocator;

    private Map pipelineDescriptors = new HashMap();

    private Boolean traceExecution;

    // -----------------------------------------------------------------------
    // PipelineService Implementation
    // -----------------------------------------------------------------------

    public void addPipeline( PipelineDescriptor pipelineDescriptor )
        throws PipelineException
    {
        // -----------------------------------------------------------------------
        // Validate
        // -----------------------------------------------------------------------

        if ( StringUtils.isEmpty( pipelineDescriptor.getId() ) )
        {
            throw new PipelineException( "Invalid pipelineDescriptor, id is required." );
        }

        // -----------------------------------------------------------------------
        // Create the runtimeManager
        // -----------------------------------------------------------------------

        PipelineRuntimeManager runtimeManager = new PipelineRuntimeManager( pipelineDescriptor.getId() );

        List valves = pipelineDescriptor.getValveRoleHints();

        if ( valves == null )
        {
            valves = new ArrayList();
        }

        // -----------------------------------------------------------------------
        // Look up all the valves in the pipelineDescriptor
        // -----------------------------------------------------------------------

        try
        {
            for ( Iterator it = valves.iterator(); it.hasNext(); )
            {
                String valve = (String) it.next();

                runtimeManager.getValveInstances().add( serviceLocator.lookup( Valve.class.getName(), valve ) );
            }
        }
        catch ( ComponentLookupException e )
        {
            removePipeline( runtimeManager );

            throw new PipelineException( "Error while looking up the valves.", e );
        }

        runtimeManager.setValveRoleHints( pipelineDescriptor.getValveRoleHints() );

        // -----------------------------------------------------------------------
        // Save the pipelineDescriptor
        // -----------------------------------------------------------------------

        synchronized ( lock )
        {
            pipelineDescriptors.put( runtimeManager.getId(), runtimeManager );
        }
    }

    public void processMessage( PipelineRequest request )
        throws PipelineException
    {
        // -----------------------------------------------------------------------
        // Validate the request
        // -----------------------------------------------------------------------

        String pipelineId = request.getPipelineId();

        if ( StringUtils.isEmpty( pipelineId ) )
        {
            throw new PipelineException( "Illegal request: missing required field 'pipeline id'" );
        }

        if ( request.getTraceExecution() == null )
        {
            request.setTraceExecution( traceExecution );
        }

        // -----------------------------------------------------------------------
        // Find the descriptor and call the executor
        // -----------------------------------------------------------------------

        PipelineRuntimeManager pipelineRuntimeManager;

        synchronized ( lock )
        {
            pipelineRuntimeManager = (PipelineRuntimeManager) pipelineDescriptors.get( pipelineId );
        }

        if ( pipelineRuntimeManager == null )
        {
            throw new PipelineException( "No such pipeline '" + pipelineId + "'." );
        }

        executor.execute( pipelineRuntimeManager, request );
    }

    public void processMessage( String pipelineId, Map context )
        throws PipelineException
    {
        PipelineRequest request = new PipelineRequest();

        request.setPipelineId( pipelineId );
        request.setContext( context );
        request.setExceptionHandler( new RuntimeExceptionHandler() );

        processMessage( request );
    }

    public void processMessage( String pipelineId, Map context, ExceptionHandler exceptionHandler )
        throws PipelineException
    {
        PipelineRequest request = new PipelineRequest();

        request.setPipelineId( pipelineId );
        request.setContext( context );
        request.setExceptionHandler( exceptionHandler );

        processMessage( request );
    }

    public void setTraceExecution( boolean traceExecution )
    {
        this.traceExecution = Boolean.valueOf( traceExecution );
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
        if ( pipelines == null )
        {
            return;
        }

        for ( Iterator it = pipelines.iterator(); it.hasNext(); )
        {
            PipelineDescriptor pipelineDescriptor = (PipelineDescriptor) it.next();

            try
            {
                addPipeline( pipelineDescriptor );
            }
            catch ( PipelineException e )
            {
                stop();

                throw new StartingException( "Error while starting pipelineDescriptor.", e );
            }
        }
    }

    public void stop()
    {
        synchronized ( lock )
        {
            for ( Iterator it = pipelineDescriptors.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                removePipeline( (PipelineRuntimeManager) entry.getValue() );
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private void removePipeline( PipelineRuntimeManager runtimeManager )
    {
        for ( Iterator it = runtimeManager.getValveInstances().iterator(); it.hasNext(); )
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

    protected void handleException( Exception e )
    {
        e.printStackTrace( System.out );
    }
}
