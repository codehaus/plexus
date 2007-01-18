package org.codehaus.plexus.pipeline.execution;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.pipeline.ExceptionHandler;
import org.codehaus.plexus.pipeline.PipelineException;
import org.codehaus.plexus.pipeline.PipelineListener;
import org.codehaus.plexus.pipeline.PipelineRequest;
import org.codehaus.plexus.pipeline.PipelineRuntimeManager;
import org.codehaus.plexus.pipeline.Valve;
import org.codehaus.plexus.pipeline.ValveRequest;
import org.codehaus.plexus.pipeline.ValveReturnCode;

import java.util.Iterator;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role-hint="default"
 */
public class DefaultPipelineExecutor
    extends AbstractLogEnabled
    implements PipelineExecutor, Initializable
{
    /**
     * @plexus.configuration default-value="true"
     */
    private boolean traceExecution;

    // -----------------------------------------------------------------------
    // PipelineExecutor Implementation
    // -----------------------------------------------------------------------

    public void execute( PipelineRuntimeManager pipelineRuntimeManager, PipelineRequest pipelineRequest )
        throws PipelineException
    {
        String pipelineId = pipelineRequest.getPipelineId();

        ExceptionHandler exceptionHandler = pipelineRequest.getExceptionHandler();

        // -----------------------------------------------------------------------
        // Invoke all the valveRoleHints in the pipelineId
        // -----------------------------------------------------------------------

        int i = 0;

        for ( Iterator it = pipelineRuntimeManager.getValveInstances().iterator(); it.hasNext(); i++ )
        {
            Valve valve = (Valve) it.next();

            String valveId = (String) pipelineRuntimeManager.getValveRoleHints().get( i );

            // -----------------------------------------------------------------------
            // Trace
            // -----------------------------------------------------------------------

            if ( traceExecution && getLogger().isInfoEnabled() )
            {
                getLogger().info( "Trace: " + pipelineId + ":" + valveId );
            }

            // -----------------------------------------------------------------------
            // Call beforeValve() on all the listeners
            // -----------------------------------------------------------------------

            ValveRequest request = new ValveRequest( pipelineRequest, valveId );

            for ( Iterator j = pipelineRequest.getListeners().iterator(); j.hasNext(); )
            {
                PipelineListener listener = (PipelineListener) j.next();

                try
                {
                    listener.beforeValve( pipelineRequest );
                }
                catch ( Throwable e )
                {
                    getLogger().error( "Error while calling listener in pipeline. PipelineDescriptor: " + pipelineId + ", " +
                        "valve: " + valveId + ", listener: " + listener );
                }
            }

            // -----------------------------------------------------------------------
            // Run the valve
            // -----------------------------------------------------------------------

            boolean shouldBreak;

            PipelineException newException = null;

            try
            {
                ValveReturnCode returnCode = valve.invoke( request );

                if ( returnCode.equals( Valve.PROCEED ) )
                {
                    shouldBreak = false;
                }
                else if ( returnCode.equals( Valve.STOP ) )
                {
                    shouldBreak = true;
                }
                else
                {
                    newException = new PipelineException( "Unknown return code. This pipeline cannot handle this type: " + returnCode.toString() );

                    shouldBreak = true;
                }
            }
            catch ( Throwable e )
            {
                exceptionHandler.handleException( e );

                shouldBreak = false;
            }

            // -----------------------------------------------------------------------
            // Run all the afterValve()
            // -----------------------------------------------------------------------

            for ( Iterator j = pipelineRequest.getListeners().iterator(); j.hasNext(); )
            {
                PipelineListener listener = (PipelineListener) j.next();

                try
                {
                    listener.afterValve( pipelineRequest );
                }
                catch ( Throwable e )
                {
                    getLogger().error( "Error while calling listener in pipeline. PipelineDescriptor: " + pipelineId + ", " +
                        "valve: " + valveId + ", listener: " + listener );
                }
            }

            if ( shouldBreak )
            {
                if ( newException != null )
                {
                    throw newException;
                }

                break;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }
}
