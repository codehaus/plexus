package org.codehaus.plexus.pipeline;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PipelineService
{
    String ROLE = PipelineService.class.getName();

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    void addPipeline( PipelineDescriptor pipelineDescriptor )
        throws PipelineException;

    // -----------------------------------------------------------------------
    // Execution
    // -----------------------------------------------------------------------

    void processMessage( PipelineRequest request )
        throws PipelineException;

    void processMessage( String pipelineId, Map context )
        throws PipelineException;

    void processMessage( String pipelineId, Map context, ExceptionHandler exceptionHandler )
        throws PipelineException;

    void setTraceExecution( boolean traceExecution );
}
