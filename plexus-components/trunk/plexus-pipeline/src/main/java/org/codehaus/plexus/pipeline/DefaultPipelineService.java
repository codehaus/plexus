package org.codehaus.plexus.pipeline;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPipelineService
    extends AbstractLogEnabled
    implements PipelineService, Startable, Serviceable
{
    /**
     * @plexus.configuration
     */
    private List pipelines;

    /**
     * @plexus.configuration default-value="true"
     */
    private boolean traceExecution;

    private final Object lock = new Object();

    private ServiceLocator serviceLocator;

    private Map pipelineDescriptors = new HashMap();

    // -----------------------------------------------------------------------
    // PipelineService Implementation
    // -----------------------------------------------------------------------

    public void addPipeline( Pipeline pipeline )
        throws PipelineException
    {
        // -----------------------------------------------------------------------
        // Validate
        // -----------------------------------------------------------------------

        if ( StringUtils.isEmpty( pipeline.getId() ) )
        {
            throw new PipelineException( "Invalid pipeline, id is required." );
        }

        // -----------------------------------------------------------------------
        // Create the descriptor
        // -----------------------------------------------------------------------

        PipelineDescriptor descriptor = new PipelineDescriptor( pipeline.getId() );

        List valves = pipeline.getValveRoleHints();

        if ( valves == null )
        {
            valves = new ArrayList();
        }

        // -----------------------------------------------------------------------
        // Look up all the valves in the pipeline
        // -----------------------------------------------------------------------

        try
        {
            for ( Iterator it = valves.iterator(); it.hasNext(); )
            {
                String valve = (String) it.next();

                descriptor.getValveInstances().add( serviceLocator.lookup( Valve.class.getName(), valve ) );
            }
        }
        catch ( ComponentLookupException e )
        {
            removePipeline( descriptor );

            throw new PipelineException( "Error while looking up the valves.", e );
        }

        descriptor.setValveRoleHints( pipeline.getValveRoleHints() );

        // -----------------------------------------------------------------------
        // Save the pipeline
        // -----------------------------------------------------------------------

        synchronized ( lock )
        {
            pipelineDescriptors.put( descriptor.getId(), descriptor );
        }
    }

    public void processMessage( String pipeline, Map context )
        throws PipelineException
    {
        processMessage( pipeline, context, new RuntimeExceptionHandler() );
    }

    public void processMessage( String pipelineId, Map context, ExceptionHandler exceptionHandler )
        throws PipelineException
    {
        PipelineDescriptor pipelineDescriptor;

        synchronized ( lock )
        {
            pipelineDescriptor = (PipelineDescriptor) pipelineDescriptors.get( pipelineId );
        }

        if ( pipelineDescriptor == null )
        {
            throw new PipelineException( "No such pipeline '" + pipelineId + "'." );
        }

        // -----------------------------------------------------------------------
        // Invoke all the valveRoleHints in the pipelineId
        // -----------------------------------------------------------------------

        int i = 0;

        for ( Iterator it = pipelineDescriptor.getValveInstances().iterator(); it.hasNext(); i++ )
        {
            Valve valve = (Valve) it.next();

            if ( traceExecution && getLogger().isInfoEnabled() )
            {
                getLogger().info( "Trace: " + pipelineId + ":" + pipelineDescriptor.getValveRoleHints().get( i ) );
            }

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
                    throw new PipelineException(
                        "Unknown return code. This pipeline cannot handle this type: " + returnCode.toString() );
                }
            }
            catch ( Exception e )
            {
                exceptionHandler.handleException( e );
            }
        }
    }

    public void setTraceExecution( boolean traceExecution )
    {
        this.traceExecution = traceExecution;
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
            Pipeline pipeline = (Pipeline) it.next();

            try
            {
                addPipeline( pipeline );
            }
            catch ( PipelineException e )
            {
                stop();

                throw new StartingException( "Error while starting pipeline.", e );
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

                removePipeline( (PipelineDescriptor) entry.getValue() );
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private void removePipeline( PipelineDescriptor descriptor )
    {
        for ( Iterator it = descriptor.getValveInstances().iterator(); it.hasNext(); )
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
